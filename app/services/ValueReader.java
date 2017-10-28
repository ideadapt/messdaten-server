package services;

import model.MeasurementValueXml;

public interface ValueReader {
  MeasurementValueXml getValue(String deviceId, String path);
}
