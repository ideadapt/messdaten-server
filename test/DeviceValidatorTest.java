import model.Device;
import org.junit.Before;
import org.junit.Test;
import services.DeviceValidator;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DeviceValidatorTest {

    //testet DeviceValidator

    private List<Device> devices;

    @Before
    public void initialize(){
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

        assertTrue("Valid device returns true", valid);
    }


    @Test
    public void validateDeviceWithExistingNameReturnsFalse() {
        Device newDevice = new Device("device1", "192.230.25.26", "c:/temp/values.txt", "group3", "xml");

        boolean valid = DeviceValidator.validateDevice(devices, newDevice);

        assertFalse("Device with existing name returns false", valid);
    }

    @Test
    public void validateDeviceWithEmptyStringNameReturnsFalse() {
        Device newDevice = new Device("", "192.230.25.26", "c:/temp/values.txt", "group3", "xml");

        boolean valid = DeviceValidator.validateDevice(devices, newDevice);

        assertFalse("Device with empty string name returns false", valid);
    }

    @Test
    public void validateDeviceWithEmptyStringDataSourceReturnsFalse() {
        Device newDevice = new Device("device1", "192.230.25.26", "", "group3", "xml");

        boolean valid = DeviceValidator.validateDevice(devices, newDevice);

        assertFalse("Device with empty string name returns false", valid);
    }

    @Test
    public void isValidForUpdateWithExistingNameReturnsTrue() {
        Device newDevice = new Device("device1", "192.230.25.26", "c:/temp/values.txt", "group3", "xml");

        boolean valid = DeviceValidator.isValidForUpdate(devices, newDevice);

        assertTrue("Update device with existing name returns true", valid);
    }

    @Test
    public void isValidForUpdateWithNewDeviceNameReturnsFalse() {
        Device newDevice = new Device("device5", "192.230.28.29", "c:/temp/values.txt", "group4", "xml");

        boolean valid = DeviceValidator.isValidForUpdate(devices, newDevice);

        assertFalse("Update with new device name returns false", valid);
    }

    @Test
    public void isValidForUpdateWithEmptyStringNameReturnsFalse() {
        Device newDevice = new Device("", "192.230.25.26", "c:/temp/values.txt", "group4", "xml");

        boolean valid = DeviceValidator.isValidForUpdate(devices, newDevice);

        assertFalse("Update device with empty string name returns false", valid);
    }

    @Test
    public void isValidForUpdateWithEmptyStringDataSourceReturnsFalse() {
        Device newDevice = new Device("device1", "192.230.25.26", "", "group4", "xml");

        boolean valid = DeviceValidator.isValidForUpdate(devices, newDevice);

        assertFalse("Update device with empty string dataSource returns false", valid);
    }

}
