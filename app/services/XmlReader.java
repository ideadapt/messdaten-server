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

public class XmlReader implements ValueReader {
  private static final String ID_TAG_NAME = "Id";
  private static final String VALUE_TAG_NAME = "Value";
  private static final String DEVICE_TAG_NAME = "Device";

  @Override
  public MeasurementValueXml getValue(String deviceId, String path) {
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
}
