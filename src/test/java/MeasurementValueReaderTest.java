import model.MeasurementValueXml;
import org.junit.jupiter.api.Test;
import services.DeviceId;
import services.MeasurementValueReader;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class MeasurementValueReaderTest {

    //testet test.java.MeasurementValueReaderTest - Messwert aus Test-xml auslesen

    @Test
    public void getActualValueWithValidDeviceNameReturnsTrue() {
        DeviceId deviceTest = new DeviceId("deviceTest");
        MeasurementValueXml value = MeasurementValueReader.getActualValue(deviceTest);

        assertEquals("2.217", value.getValue());
    }
}
