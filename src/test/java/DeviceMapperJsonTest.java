
import org.junit.jupiter.api.Test;
import services.DeviceMapperJson;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class DeviceMapperJsonTest {

    //testet DeviceMapperJson - Protocol und Pfad aus Config-JSON prüfen

    @Test
    public void getMeasurementValuePathWithValidDeviceNameReturnsTrue() {
        String path = DeviceMapperJson.getMeasurementValuePath("deviceTest");

        assertEquals("/rsc/DeviceValuesTest.xml", path);
    }

    @Test
    public void getMeasurementValueProtocolWithValidDeviceNameReturnsTrue() {
        Optional<String> protocol = DeviceMapperJson.getMeasurementValueProtocol("deviceTest");

        assertEquals("xml-1", protocol.get());
    }
}
