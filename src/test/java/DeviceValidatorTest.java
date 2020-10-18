import model.Device;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import services.DeviceValidator;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DeviceValidatorTest {

    //testet DeviceValidator

    private static List<Device> devices;

    @BeforeAll
    public static void initialize() {
        devices = new ArrayList<>();
        devices.add(new Device("device1", "192.230.25.26", "c:/temp/values.txt", "group1", "txt"));
        devices.add(new Device("device2", "192.230.25.26", "c:/temp/values.txt", "group2", "xml"));
        devices.add(new Device("device3", "192.230.25.26", "c:/temp/values.txt", "group1", "txt"));
    }

    //new Device und update Device
    @Test
    public void validateDeviceWithValidDeviceReturnsTrue() {
        Device newDevice = new Device("device4", "192.230.25.26", "c:/temp/values.txt", "group3", "xml");

        boolean valid = DeviceValidator.validateDevice(devices, newDevice);

        assertTrue(valid);
    }


    @Test
    public void validateDeviceWithExistingNameReturnsFalse() {
        Device newDevice = new Device("device1", "192.230.25.26", "c:/temp/values.txt", "group3", "xml");

        boolean valid = DeviceValidator.validateDevice(devices, newDevice);

        assertFalse(valid);
    }

    @Test
    public void validateDeviceWithEmptyStringNameReturnsFalse() {
        Device newDevice = new Device("", "192.230.25.26", "c:/temp/values.txt", "group3", "xml");

        boolean valid = DeviceValidator.validateDevice(devices, newDevice);

        assertFalse(valid);
    }

    @Test
    public void validateDeviceWithEmptyStringDataSourceReturnsFalse() {
        Device newDevice = new Device("device1", "192.230.25.26", "", "group3", "xml");

        boolean valid = DeviceValidator.validateDevice(devices, newDevice);

        assertFalse(valid);
    }

    @Test
    public void isValidForUpdateWithExistingNameReturnsTrue() {
        Device newDevice = new Device("device1", "192.230.25.26", "c:/temp/values.txt", "group3", "xml");

        boolean valid = DeviceValidator.isValidForUpdate(devices, newDevice);

        assertTrue(valid);
    }

    @Test
    public void isValidForUpdateWithNewDeviceNameReturnsFalse() {
        Device newDevice = new Device("device5", "192.230.28.29", "c:/temp/values.txt", "group4", "xml");

        boolean valid = DeviceValidator.isValidForUpdate(devices, newDevice);

        assertFalse(valid);
    }

    @Test
    public void isValidForUpdateWithEmptyStringNameReturnsFalse() {
        Device newDevice = new Device("", "192.230.25.26", "c:/temp/values.txt", "group4", "xml");

        boolean valid = DeviceValidator.isValidForUpdate(devices, newDevice);

        assertFalse(valid);
    }

    @Test
    public void isValidForUpdateWithEmptyStringDataSourceReturnsFalse() {
        Device newDevice = new Device("device1", "192.230.25.26", "", "group4", "xml");

        boolean valid = DeviceValidator.isValidForUpdate(devices, newDevice);

        assertFalse(valid);
    }

}
