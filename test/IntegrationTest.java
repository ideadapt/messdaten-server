import org.junit.*;

import static play.test.Helpers.*;
import static org.junit.Assert.*;


public class IntegrationTest {

    /**
     * integration test
     * just check if the device-liste (list, update and delete) page is being shown
     */
    @Test
    public void testDeviceListPage() {
        running(testServer(9000, fakeApplication(inMemoryDatabase())), HTMLUNIT, browser -> {
            browser.goTo("http://localhost:9000/list");
            assertTrue(browser.pageSource().contains("Device-Liste")); //erwarteter Inhalt "Device-Liste"
        });
    }

    @Test
    public void testDeviceUpdatePage() {
        running(testServer(9000, fakeApplication(inMemoryDatabase())), HTMLUNIT, browser -> {
            browser.goTo("http://localhost:9000/device/update/device1");
            assertTrue(browser.pageSource().contains("update")); //erwarteter Inhalt "update"
        });
    }

    @Test
    public void testDeviceDeletePage() {
        running(testServer(9000, fakeApplication(inMemoryDatabase())), HTMLUNIT, browser -> {
            browser.goTo("http://localhost:9000/device/delete/device1");
            assertTrue(browser.pageSource().contains("delete")); //erwarteter Inhalt "delete"
        });
    }

}
