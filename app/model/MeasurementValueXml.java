package model;

/**
 * Beschreibt einen Messwert mit Zeitstempel
 *
 * Created by Nett on 04.06.2017.
 */
public class MeasurementValueXml {

    String value = "";
    long time = 0;

    public MeasurementValueXml() {
    }

    public String getValue() {
        return value;
    }

    public void setValue(String id) {
        this.value = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
