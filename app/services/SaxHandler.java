package services;

import model.MeasurementValueXml;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Dient zum Lesen von SY289_data.xml, das durch die Anwendung Sylcom zur Speicherung der Messwerte der Syltec-Messuhr verwendet wird.
 * Da das XML-File kein Root-Element hat kann es nicht gepart werden, und muss Element fuer Element eingelesen werden.
 *
 * Created by Nett on 21.06.2017.
 */
public class SaxHandler extends DefaultHandler {

    private boolean hasId = false;
    private boolean hasValue = false;
    private boolean hasTime = false;
    private List<MeasurementValueXml> measurementValues = new ArrayList<>();
    private MeasurementValueXml actualValue = null;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes){

        if(qName.equals("ChannelResult")){
            actualValue = new MeasurementValueXml();
        }
        if (qName.equalsIgnoreCase("ChannelName")) {
            hasId = true;
        }
        if (qName.equalsIgnoreCase("Value")) {
            hasValue = true;
        }
        if (qName.equalsIgnoreCase("Date")) {
            hasTime = true;
        }

    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if(qName.equals("ChannelResult")){
            if((actualValue.getId() != "") && (actualValue.getValue() != "") && actualValue.getTime() > 0){
                measurementValues.add(actualValue);
            }
        }
    }

    @Override
    public void characters(char ch[], int start, int length) {

        if (hasId) {
            actualValue.setId(new String(ch, start, length));
            hasId = false;
        }
        if (hasValue) {
            actualValue.setValue(new String(ch, start, length));
            hasValue = false;
        }
        if (hasTime) {
            String actualTime = new String(ch, start, length);
            String formatTime = actualTime.replace("T", " ");

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date;
            try {
                date = simpleDateFormat.parse(formatTime);
                actualValue.setTime(date.getTime());
            } catch (ParseException ex) {
                actualValue.setTime(0);
            }
            hasTime = false;
        }
    }

    public List<MeasurementValueXml> getMeasurementValues() {
        return measurementValues;
    }
}
