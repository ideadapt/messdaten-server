package services;

import model.MeasurementValueXml;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class SaxXmlReader implements ValueReader {

  @Override
  public MeasurementValueXml getValue(String deviceId, String path) {
    ArrayList<MeasurementValueXml> measurementValues = extractMeasurementValues(path);
    MeasurementValueXml actualValue = findNewest(measurementValues);
    if(actualValue.isNaN()){
      throw new ReadWriteException("Kein gültiger Messwert in " + path +" für " + deviceId + " gefunden");
    }
    return actualValue;
  }

  private static MeasurementValueXml findNewest(ArrayList<MeasurementValueXml> measurementValues) {
    long time = 0;
    MeasurementValueXml actualValue = null;
    for (MeasurementValueXml value : measurementValues){
      if(value.getTime() > time){
        actualValue = value;
        time = value.getTime();
      }
    }
    return actualValue;
  }

  private static ArrayList<MeasurementValueXml> extractMeasurementValues(String path) {
    SaxHandler handler = new SaxHandler();
    SAXParserFactory factory = SAXParserFactory.newInstance();
    try {
      SAXParser saxParser = factory.newSAXParser();
      saxParser.parse(
              new SequenceInputStream(
                      Collections.enumeration(Arrays.asList(
                              new ByteArrayInputStream("<ChannelResult>".getBytes()),
                              new FileInputStream(path),
                              new ByteArrayInputStream("</ChannelResult>".getBytes())))
              ), handler);
    } catch (ParserConfigurationException | SAXException | IOException e) {
      throw new ReadWriteException("Fehler beim parsen von " + path, e);
    }

    if(handler.getMeasurementValues().isEmpty()){
      throw new ReadWriteException("Keine Messwerte gefunden in " + path);
    }

    return (ArrayList<MeasurementValueXml>) handler.getMeasurementValues();
  }

}
