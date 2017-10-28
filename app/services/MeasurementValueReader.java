package services;

import model.MeasurementValueXml;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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
    public static final String ID_TAG_NAME = "Id";
    public static final String DEVICE_TAG_NAME = "Device";
    public static final String VALUE_TAG_NAME = "Value";

    /**
     * Dient als Weiche um die, gemaess Prokoll des Devices, passende Methode aufzurufen.
     *
     * @param deviceId
     * @return
     * @throws ReadWriteException
     */
    public static MeasurementValueXml getActualValue(String deviceId) throws ReadWriteException{
        String protocol = DeviceMapperJson.getMeasurementValueProtocol(deviceId);
        String path = DeviceMapperJson.getMeasurementValuePath(deviceId);
        try{
          switch (protocol){
              case "xml-1":
                  return getActualValueFromXml(deviceId, path);
              case "txt-1":
                  return getActualValueFromTxt(deviceId);
              case "xml-2":
                  return getActualValueFromXmlSax(deviceId);
              default:
                  throw new IllegalArgumentException("Unsupported protocol: " + protocol);
          }
        }catch(ReadWriteException | IllegalArgumentException e){
          throw new ReadWriteException("Fehler beim Lesen von Messwert für " + deviceId + " in " + path, e);
        }
    }

    private static MeasurementValueXml getActualValueFromXml(String deviceId, String path) throws ReadWriteException {
        File xmlFile = new File(path);
        Document doc = createXmlDocument(xmlFile);
        NodeList nodeList = doc.getElementsByTagName(DEVICE_TAG_NAME);
        String value = findValue(deviceId, nodeList);

        MeasurementValueXml measurementValue = new MeasurementValueXml();
        measurementValue.setId(deviceId);
        measurementValue.setTime(xmlFile.lastModified());
        measurementValue.setValue(value);

        return measurementValue;
    }

    private static String findValue(String deviceId, NodeList nodeList) throws ReadWriteException {
      for (int i = 0; i < nodeList.getLength(); i++) {
          Node node = nodeList.item(i);
          if (node.getNodeType() == Node.ELEMENT_NODE) {
              Element element = (Element) node;
              String xmlDeviceId = element.getElementsByTagName(ID_TAG_NAME).item(0).getTextContent();
              if(xmlDeviceId.equals(deviceId)){
                  return element.getElementsByTagName(VALUE_TAG_NAME).item(0).getTextContent();
              }
          }
      }

      throw new ReadWriteException("Id " + deviceId + " nicht gefunden.");
    }

    private static Document createXmlDocument(File xmlFile) {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        Document doc;

        try {
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            doc = docBuilder.parse(xmlFile);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new ReadWriteException("Fehler beim Lesen von " + xmlFile.getAbsolutePath(), e);
        }

        doc.getDocumentElement().normalize();
        return doc;
    }

    /**
     * Gibt einen Messwert mit Zeitstempel in Form einer Instanz von MeasurementValueXml zurueck,
     * wenn das Protokoll xml-2 konfiguriert wurde.
     *
     * Wirft im Fehlerfall eine ReadWriteException
     *
     * @param deviceId
     * @return
     * @throws ReadWriteException
     */
    public static MeasurementValueXml getActualValueFromXmlSax(String deviceId)throws ReadWriteException {

        // Pfad der Messwerte-Files gemaess deviceId aus der Konfiguration lesen und ein File erstellen
        String path = DeviceMapperJson.getMeasurementValuePath(deviceId);
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
            throw  new ReadWriteException("Der Name " + deviceId + " wurde in " + path +" nicht gefunden");
        }
        //Bei Abbruch des Bluetooth-Signals der Messuhr wird NaN (Not_A_Number) als Messwert eingelesen
        if(actualValue.getValue().equals("NaN")){
            throw  new ReadWriteException("Kein gültiger Messwert in " + path +" für " + deviceId + " gefunden");
        }
        return actualValue;
    }


    /**
     * Gibt einen Messwert mit Zeitstempel in Form einer Instanz von MeasurementValueXml zurueck,
     * wenn das Protokoll txt-1 konfiguriert wurde.
     *
     * Wirft im Fehlerfall eine ReadWriteException
     *
     * @param deviceId
     * @return
     * @throws ReadWriteException
     */
    public static MeasurementValueXml getActualValueFromTxt(String deviceId)throws ReadWriteException{

        FileInputStream in = null;
        BufferedReader br = null;
        String strLine = null;
        String line = null;
        String lastLine = null;
        MeasurementValueXml measurementValue = new MeasurementValueXml();
        // Pfad der Messwerte-Files gemaess deviceId aus der Konfiguration lesen und ein File erstellen
        String path = DeviceMapperJson.getMeasurementValuePath(deviceId);

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
