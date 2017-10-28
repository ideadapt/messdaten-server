package services;

import model.MeasurementValueXml;


/**
 * Die Klasse dient als Schnittstelle zur Datenhaltung der Messwerte.
 * Je nach konfiguriertem Protokoll wird die entsprechende Methode aufgerufen.
 *
 * Created by Nett on 04.06.2017.
 */
public class MeasurementValueReader {

    public static MeasurementValueXml getActualValue(String deviceId) throws ReadWriteException{
        String protocol = DeviceMapperJson.getMeasurementValueProtocol(deviceId);
        String path = DeviceMapperJson.getMeasurementValuePath(deviceId);
        ValueReader reader = createReaderForProtocol(protocol);
        return reader.getValue(deviceId, path);
    }

    public static ValueReader createReaderForProtocol(String protocol){
      switch (protocol){
        case "xml-1":
          return new XmlReader();
        case "txt-1":
          return new TxtReader();
        case "xml-2":
          return new SaxXmlReader();
        default:
          throw new IllegalArgumentException("Unsupported protocol: " + protocol);
      }
    }
}
