package services;

import model.MeasurementValueXml;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;


/**
 * Die Klasse dient als Schnittstelle zur Datenhaltung der Messwerte.
 * Je nach konfiguriertem Protokoll wird die entsprechende Methode aufgerufen.
 *
 * Created by Nett on 04.06.2017.
 */
public class MeasurementValueReader {


    /**
     * Dient als Weiche um die, gemaess Prokoll des Devices, passende Methode aufzurufen.
     *
     * @param deviceName
     * @return
     * @throws ReadWriteException
     */
    public static MeasurementValueXml getActualValue(String deviceName)throws ReadWriteException{

        String protocol = DeviceMapperJson.getMeasurementValueProtocol(deviceName);
        switch (protocol){

            case "xml-1":
                return getActualValueFromXml(deviceName);
            case "txt-1":
                return getActualValueFromTxt(deviceName);
            case "xml-2":
                return getActualValueFromXmlSax(deviceName);
            default:
                throw new ReadWriteException("Not possible to read protocol-type: " + protocol);
        }
    }

    /**
     * Gibt einen Messwert mit Zeitstempel in Form einer Instanz von MeasurementValueXml zurueck.
     *
     * @param deviceName
     * @return MeasurementValueXml
     * @throws ReadWriteException
     */
    public static MeasurementValueXml getActualValueFromXml(String deviceName)throws ReadWriteException{
        File xmlFile = getMeasurementFile(deviceName);
        NodeList nList = getDeviceNodes(xmlFile);
        return extractMeasurementValue(deviceName, xmlFile, nList);
    }

    private static File getMeasurementFile(String deviceName) {
        String path = DeviceMapperJson.getMeasurementValuePath(deviceName);
        File xmlFile;
        if(path != null){
            xmlFile = new File(path);
        }else{
            throw  new ReadWriteException("Path for DataSource from " + deviceName + " not found in configuration");
        }
        return xmlFile;
    }

    private static MeasurementValueXml extractMeasurementValue(String deviceName, File xmlFile, NodeList nList) {
        MeasurementValueXml measurementValue = new MeasurementValueXml();
        for (int i = 0; i < nList.getLength(); i++) {
            Node nNode = nList.item(i);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                String deviceId = eElement.getElementsByTagName("Id").item(0).getTextContent();
                if (deviceId.equals(deviceName)) {
                    measurementValue.setId(deviceId);
                    measurementValue.setValue(eElement.getElementsByTagName("Value").item(0).getTextContent());
                    measurementValue.setTime(xmlFile.lastModified());
                    break;
                }
            }
        }
        return measurementValue;
    }

    private static NodeList getDeviceNodes(File xmlFile) {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();
            return doc.getElementsByTagName("Device");
        } catch (ParserConfigurationException e) {
            throw new ReadWriteException("Fehler beim Lesen von " + xmlFile.getAbsolutePath() + "\n" + e.getMessage());
        } catch (SAXException e) {
            throw new ReadWriteException("Fehler beim Lesen von " + xmlFile.getAbsolutePath() + "\n" + e.getMessage());
        } catch (IOException e) {
            throw new ReadWriteException("Fehler beim Lesen von " + xmlFile.getAbsolutePath() + "\n" + e.getMessage());
        }
    }

    /**
     * Gibt einen Messwert mit Zeitstempel in Form einer Instanz von MeasurementValueXml zurueck,
     * wenn das Protokoll xml-2 konfiguriert wurde.
     *
     * Wirft im Fehlerfall eine ReadWriteException
     *
     * @param deviceName
     * @return
     * @throws ReadWriteException
     */
    public static MeasurementValueXml getActualValueFromXmlSax(String deviceName)throws ReadWriteException {

        // Pfad der Messwerte-Files gemaess deviceName aus der Konfiguration lesen und ein File erstellen
        String path = DeviceMapperJson.getMeasurementValuePath(deviceName);
        long time = 0;
        MeasurementValueXml actualValue = null;
        SaxHandler handler = new SaxHandler();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        ArrayList<MeasurementValueXml> measurementValues = (ArrayList<MeasurementValueXml>) handler.getMeasurementValues();
        try {
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(
                    new SequenceInputStream(
                            Collections.enumeration(Arrays.asList(
                                    new InputStream[] {
                                            new ByteArrayInputStream("<ChannelResult>".getBytes()),
                                            new FileInputStream(path),
                                            new ByteArrayInputStream("</ChannelResult>".getBytes()),
                                    }))
                    ), handler);
        } catch (ParserConfigurationException e) {
            throw new ReadWriteException("Fehler beim Lesen von " + path + "\n" + e.getMessage());
        } catch (SAXException e) {
            throw new ReadWriteException("Fehler beim Lesen von " + path + "\n" + e.getMessage());
        } catch (FileNotFoundException e) {
            throw new ReadWriteException("Datei konnte nicht gefunden werden " + path + "\n" + e.getMessage());
        } catch (IOException e) {
            throw new ReadWriteException("Fehler beim Lesen von " + path + "\n" + e.getMessage());
        }

        // Aktuellster Messwert zuweisen
        for (MeasurementValueXml value : measurementValues){
            if(value.getTime() > time){
                actualValue = value;
                time = value.getTime();
            }
        }
        if(actualValue == null){
            throw  new ReadWriteException("Der Name " + deviceName + " wurde in " + path +" nicht gefunden");
        }
        //Bei Abbruch des Bluetooth-Signals der Messuhr wird NaN (Not_A_Number) als Messwert eingelesen
        if(actualValue.getValue().equals("NaN")){
            throw  new ReadWriteException("Kein gültiger Messwert in " + path +" für " + deviceName + " gefunden");
        }
        return actualValue;
    }


    /**
     * Gibt einen Messwert mit Zeitstempel in Form einer Instanz von MeasurementValueXml zurueck,
     * wenn das Protokoll txt-1 konfiguriert wurde.
     *
     * Wirft im Fehlerfall eine ReadWriteException
     *
     * @param deviceName
     * @return
     * @throws ReadWriteException
     */
    public static MeasurementValueXml getActualValueFromTxt(String deviceName)throws ReadWriteException{

        FileInputStream in = null;
        BufferedReader br = null;
        String strLine = null;
        String line = null;
        String lastLine = null;
        MeasurementValueXml measurementValue = new MeasurementValueXml();
        // Pfad der Messwerte-Files gemaess deviceName aus der Konfiguration lesen und ein File erstellen
        String path = DeviceMapperJson.getMeasurementValuePath(deviceName);

        try {
            in = new FileInputStream(path);
            br = new BufferedReader(new InputStreamReader(in));

            while ((line = br.readLine()) != null)
            {
                strLine = line;
            }
            lastLine = strLine;

            System.out.println(lastLine);
            in.close();

        } catch (FileNotFoundException ex) {
            throw  new ReadWriteException(ex.getMessage());
        } catch (IOException ex) {
            throw  new ReadWriteException(ex.getMessage());
        }

        String[] tokens = lastLine.split(";");
        measurementValue.setId(tokens[0]);
        measurementValue.setValue(tokens[1]);
        tokens[2].split(" ");

        String pattern = "dd.MM.yyyy HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        Date date = null;

        try {
            date = simpleDateFormat.parse(tokens[2]);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        measurementValue.setTime(date.getTime());

        return measurementValue;
    }

}
