import model.Device;
import play.mvc.Result;
import services.DeviceValidator;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

import static org.junit.Assert.assertTrue;
import org.junit.Test;

import static play.test.Helpers.*;
import play.mvc.Http.RequestBuilder;


public class FunctionalTest {

    //Testing Controller Action through Routing
    @Test
    public void testBadRoute() {
        running(testServer(9000, fakeApplication(inMemoryDatabase())), HTMLUNIT, browser -> {
            browser.goTo("http://localhost:9000");
            RequestBuilder request = new RequestBuilder()
                    .method(GET)
                    .uri("/xx/wrongRoute");

            Result result = route(request);
            assertEquals(NOT_FOUND, result.status()); //erwartet 404 "Not Found"
        });
    }

    @Test
    public void testGoodRoute() {
        running(testServer(9000, fakeApplication(inMemoryDatabase())), HTMLUNIT, browser -> {
            browser.goTo("http://localhost:9000");
            RequestBuilder request = new RequestBuilder()
                    .method(GET)
                    .uri("/list");

            Result result = route(request);
            assertEquals(OK, result.status()); //erwartet 200 "OK"
            assertEquals("text/html", result.contentType().get());
            assertEquals("utf-8", result.charset().get());
        });
    }


    //Testing DeviceValidator
    @Test
    public void validateDeviceWithValidReturnsTrue() {

        List<Device> devices = new ArrayList<>();
        devices.add(new Device("device1", "192.230.25.26", "c:/temp/values.txt", "group1", "txt"));
        devices.add(new Device("device2", "192.230.25.26", "c:/temp/values.txt", "group2", "xml"));
        devices.add(new Device("device3", "192.230.25.26", "c:/temp/values.txt", "group1", "txt"));
        Device newDevice = new Device("device4", "192.230.25.26", "c:/temp/values.txt", "group3", "xml");

        boolean valid = DeviceValidator.validateDevice(devices, newDevice);

        assertTrue("Valid device returns true", valid);
    }

    @Test
    public void validateDeviceWithExistingNameReturnsFalse() {

        List<Device> devices = new ArrayList<>();
        devices.add(new Device("device1", "192.230.25.26", "c:/temp/values.txt", "group1", "txt"));
        devices.add(new Device("device2", "192.230.25.26", "c:/temp/values.txt", "group2", "xml"));
        devices.add(new Device("device3", "192.230.25.26", "c:/temp/values.txt", "group1", "txt"));
        Device newDevice = new Device("device1", "192.230.25.26", "c:/temp/values.txt", "group3", "xml");

        boolean valid = DeviceValidator.validateDevice(devices, newDevice);

        assertFalse("Device with existing name returns false", valid);
    }

    @Test
    public void validateDeviceWithEmptyStringNameReturnsFalse() {

        List<Device> devices = new ArrayList<>();
        devices.add(new Device("device1", "192.230.25.26", "c:/temp/values.txt", "group1", "txt"));
        devices.add(new Device("device2", "192.230.25.26", "c:/temp/values.txt", "group2", "xml"));
        devices.add(new Device("device3", "192.230.25.26", "c:/temp/values.txt", "group1", "txt"));
        Device newDevice = new Device("", "192.230.25.26", "c:/temp/values.txt", "group3", "xml");

        boolean valid = DeviceValidator.validateDevice(devices, newDevice);

        assertFalse("Device with empty string name returns false", valid);
    }

}
