import model.Device;
import services.DeviceValidator;
import java.util.ArrayList;
import java.util.List;
import org.junit.*;
import static org.junit.Assert.*;


public class DeviceValidatorTest {

    @Test
    public void validateDeviceWithValidReturnsTrue() {

        List<Device> devices = new ArrayList<>();
        devices.add(new Device("device1", "192.230.25.26", "c:/temp/values.txt", "A", "txt-1"));
        devices.add(new Device("device2", "192.230.25.26", "c:/temp/values.txt", "A", "txt-1"));
        devices.add(new Device("device3", "192.230.25.26", "c:/temp/values.txt", "A", "txt-1"));
        Device newDevice = new Device("device4", "192.230.25.26", "c:/temp/values.txt", "A", "txt-1");

        boolean valid = DeviceValidator.validateDevice(devices, newDevice);

        assertTrue("Valid device returns true", valid);
    }

    @Test
    public void validateDeviceWithExistingNameReturnsFalse() {

        List<Device> devices = new ArrayList<>();
        devices.add(new Device("device1", "192.230.25.26", "c:/temp/values.txt", "A", "txt-1"));
        devices.add(new Device("device2", "192.230.25.26", "c:/temp/values.txt", "A", "txt-1"));
        devices.add(new Device("device3", "192.230.25.26", "c:/temp/values.txt", "A", "txt-1"));
        Device newDevice = new Device("device1", "192.230.25.26", "c:/temp/values.txt", "A", "txt-1");

        boolean valid = DeviceValidator.validateDevice(devices, newDevice);

        assertFalse("Device with existing name returns false", valid);
    }

    @Test
    public void validateDeviceWithEmptyStringNameReturnsFalse() {

        List<Device> devices = new ArrayList<>();
        devices.add(new Device("device1", "192.230.25.26", "c:/temp/values.txt", "A", "txt-1"));
        devices.add(new Device("device2", "192.230.25.26", "c:/temp/values.txt", "A", "txt-1"));
        devices.add(new Device("device3", "192.230.25.26", "c:/temp/values.txt", "A", "txt-1"));
        Device newDevice = new Device("", "192.230.25.26", "c:/temp/values.txt", "A", "txt-1");

        boolean valid = DeviceValidator.validateDevice(devices, newDevice);

        assertFalse("Device with empty string name returns false", valid);
    }

}
