package services;

import model.MeasurementValueXml;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TxtReader implements ValueReader {

  @Override
  public MeasurementValueXml getValue(String deviceId, String path) {
    FileInputStream in = null;
    BufferedReader br = null;
    String strLine = null;
    String line = null;
    String lastLine = null;
    MeasurementValueXml measurementValue = new MeasurementValueXml();

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
