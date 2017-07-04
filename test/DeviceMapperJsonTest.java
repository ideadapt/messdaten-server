import org.junit.Test;
import services.DeviceMapperJson;
import static org.junit.Assert.assertEquals;


public class DeviceMapperJsonTest {

    //testet DeviceMapperJson - Protocol und Pfad aus Config-JSON prüfen

    @Test
    public void getMeasurementValuePathWithValidDeviceNameReturnsTrue() {
        String path = DeviceMapperJson.getMeasurementValuePath("deviceTest");

        assertEquals("c:/temp/DeviceValuesTest.xml", path);
    }

    @Test
    public void getMeasurementValueProtocolWithValidDeviceNameReturnsTrue() {
        String  protocol = DeviceMapperJson.getMeasurementValueProtocol("deviceTest");

        assertEquals("xml-1", protocol);
    }
}
