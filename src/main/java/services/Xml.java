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
import java.io.File;
import java.io.IOException;

public class Xml {

    /**
     * Gibt einen Messwert mit Zeitstempel in Form einer Instanz von MeasurementValueXml zurueck,
     * wenn das Protokoll xml-1 konfiguriert wurde.
     */
    public static MeasurementValueXml getActualValue(String deviceName) {
        String path = DeviceMapperJson.getMeasurementValuePath(deviceName);
        path = DeviceMapperJson.class.getResource(path).getFile().replaceAll("%20", " ");
        return getMeasurementValueXml(deviceName, path);
    }


    private static MeasurementValueXml getMeasurementValueXml(String deviceName, String path) {
        MeasurementValueXml measurementValue = new MeasurementValueXml();
        NodeList nList = getNodeList(path);

        for (int i = 0; i < nList.getLength(); i++) {
            Node nNode = nList.item(i);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                String deviceId = eElement.getElementsByTagName("Id").item(0).getTextContent();
                if (deviceId.equals(deviceName)) {
                    measurementValue.setId(deviceId);
                    measurementValue.setValue(eElement.getElementsByTagName("Value").item(0).getTextContent());
                    measurementValue.setTime(new File(path).lastModified());
                    return measurementValue;
                }
            }
        }
        throw new ReadWriteException("Gerät \"" + deviceName + "\" wurde nicht gefunden in: " + path);
    }


    private static NodeList getNodeList(String path) {
        File xmlFile = new File(path);
        Document doc = getDocument(xmlFile, path);
        return doc.getElementsByTagName("Device");
    }


    private static Document getDocument(File xmlFile, String path) {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();
            return doc;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new ReadWriteException("Fehler beim Lesen von " + path + "\n" + e.getMessage());
        }
    }

}
