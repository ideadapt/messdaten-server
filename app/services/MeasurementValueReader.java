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

/**
 * Created by Nett on 04.06.2017.
 */
public class MeasurementValueReader {


    /**
     * Gibt einen Messwert mit Zeitstempel in Form einer Instanz von MeasurementValueXml zurueck
     *
     * Wirft im Fehlerfall eine ReadWriteException
     *
     * @param deviceName
     * @return
     * @throws ReadWriteException
     */
    public static MeasurementValueXml getActualValueFromXml(String deviceName)throws ReadWriteException{
        MeasurementValueXml measurementValue = new MeasurementValueXml();
        // Pfad der Messwerte-Files gemaess deviceName aus der Konfiguration lesen und ein File erstellen
        String path = DeviceMapperJson.getMeasurementValuePath(deviceName);
        File xmlFile = null;
        if(path != null){
            xmlFile = new File(path);
        }else{
            throw  new ReadWriteException("Path for DataSource from " + deviceName + " not found in configuration");
        }


        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("Device");

            for (int i = 0; i < nList.getLength(); i++) {
                Node nNode = nList.item(i);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    String deviceId = eElement.getElementsByTagName("Id").item(0).getTextContent();
                    //Suchbegriff mit dem Wert des aktuellen Elements vergleichen
                    if(deviceId.equals(deviceName)){
                        // Messwert erstellen aus Xml-File
                        measurementValue.setValue(eElement.getElementsByTagName("Value").item(0).getTextContent());
                        measurementValue.setTime(xmlFile.lastModified());
                        break;
                    }
                }
            }
        } catch (ParserConfigurationException e) {
            throw new ReadWriteException("Fehler beim Lesen von " + path + "\n" + e.getMessage());
        } catch (SAXException e) {
            throw new ReadWriteException("Fehler beim Lesen von " + path + "\n" + e.getMessage());
        } catch (IOException e) {
            throw  new ReadWriteException("Fehler beim Lesen von " + path + "\n" + e.getMessage());
        }
        if(measurementValue == null){
            throw  new ReadWriteException("Der Name " + deviceName + " wurde in " + path +" nicht gefunden");
        }
        return measurementValue;
    }

}
