import model.MeasurementValueXml;
import org.junit.jupiter.api.Test;
import services.MeasurementValueReader;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class MeasurementValueReaderTest {

    //testet test.java.MeasurementValueReaderTest - Messwert aus Test-xml auslesen

    @Test
    public void getActualValueWithValidDeviceXmlNameReturnsTrue() {
        MeasurementValueXml value = MeasurementValueReader.getActualValue("deviceTest");

        assertEquals("2.217", value.getValue());
    }

    @Test
    public void getActualValueWithValidDeviceSaxNameReturnsTrue() {
        MeasurementValueXml value = MeasurementValueReader.getActualValue("deviceSaxTest");

        assertEquals("9.88", value.getValue());
    }
}
