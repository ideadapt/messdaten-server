package services;

import model.MeasurementValueXml;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.SequenceInputStream;
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
     * Dient als Weiche um die, gemaess Protokoll des Devices, passende Methode aufzurufen.
     *
     * @param deviceName
     * @return
     */
    public static MeasurementValueXml getActualValue(String deviceName) {

        try {
            String protocol = DeviceMapperJson.getMeasurementValueProtocol(deviceName);

            assert protocol != null;
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

        }catch (ParserConfigurationException | IOException | SAXException e) {
            throw new ReadWriteException("Fehler beim Lesen von " /*+ path*/ + "\n" + e.getMessage());
        } catch (ParseException e) {
            throw new ReadWriteException("Der Name "  + deviceName + " wurde nicht gefunden");
        }catch (NumberFormatException e) {
            throw new ReadWriteException("Kein gültiger Messwert für " + deviceName + " gefunden");
        }
    }

    /**

     * @param deviceName
     * @return
     */
    private static MeasurementValueXml getActualValueFromXml(String deviceName) throws ReadWriteException, ParserConfigurationException, SAXException, IOException{
        MeasurementValueXml measurementValue = new MeasurementValueXml();
        String path = DeviceMapperJson.getMeasurementValuePath(deviceName);
        assert path != null;
        InputStream xmlFile = MeasurementValueReader.class.getResourceAsStream(path);

        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();

            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("Device");

            for (int i = 0; i < nList.getLength(); i++) {
                Node nNode = nList.item(i);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    String deviceId = eElement.getElementsByTagName("Id").item(0).getTextContent();
                    if(deviceId.equals(deviceName)){
                        measurementValue.setId(deviceId);
                        measurementValue.setValue(eElement.getElementsByTagName("Value").item(0).getTextContent());
                        measurementValue.setTime(new File(path).lastModified());
                        break;
                    }
                }
            }
        return measurementValue;
    }

    /**
     * @param deviceName
     * @return
     */

    private static MeasurementValueXml getActualValueFromXmlSax(String deviceName) throws ReadWriteException, ParserConfigurationException, SAXException, IOException {

        String path = DeviceMapperJson.getMeasurementValuePath(deviceName);
        long time = 0;
        MeasurementValueXml actualValue = null;
        SaxHandler handler = new SaxHandler();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        ArrayList<MeasurementValueXml> measurementValues = (ArrayList<MeasurementValueXml>) handler.getMeasurementValues();
            SAXParser saxParser = factory.newSAXParser();
            assert path != null;
            saxParser.parse(
                    new SequenceInputStream(
                            Collections.enumeration(Arrays.asList(
                                    new ByteArrayInputStream("<ChannelResult>".getBytes()),
                                    new FileInputStream(path),
                                    new ByteArrayInputStream("</ChannelResult>".getBytes())))
                    ), handler);

        for (MeasurementValueXml value : measurementValues){
            if(value.getTime() > time){
                actualValue = value;
                time = value.getTime();
            }
        }
        return actualValue;
    }


    /**
     * @param deviceName
     * @return
     */

    private static MeasurementValueXml getActualValueFromTxt(String deviceName) throws ReadWriteException, ParseException, IOException {

        String strLine = null;
        String line;
        MeasurementValueXml measurementValue = new MeasurementValueXml();
        String path = DeviceMapperJson.getMeasurementValuePath(deviceName);

            assert path != null;
            FileInputStream in = new FileInputStream(path);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            while ((line = br.readLine()) != null)
            {
                strLine = line;
            }
            String lastLine = strLine;

            System.out.println(lastLine);
            in.close();

        assert lastLine != null;
        String[] tokens = lastLine.split(";");
        measurementValue.setId(tokens[0]);
        measurementValue.setValue(tokens[1]);
        tokens[2].split(" ");

        String pattern = "dd.MM.yyyy HH:mm:ss";

        Date date = new SimpleDateFormat(pattern).parse(tokens[2]);

        assert date != null;
        measurementValue.setTime(date.getTime());

        return measurementValue;
    }
}
