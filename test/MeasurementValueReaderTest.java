import model.MeasurementValueXml;
import org.junit.Test;
import services.MeasurementValueReader;
import static org.junit.Assert.assertEquals;


public class MeasurementValueReaderTest {

    //testet MeasurementValueReaderTest - Messwert aus Test-xml auslesen

    @Test
    public void getActualValueWithValidDeviceNameReturnsTrue() {
        MeasurementValueXml value = MeasurementValueReader.getActualValue("deviceTest");

        assertEquals("2.217", value.getValue());
    }
}
