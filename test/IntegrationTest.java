import org.junit.*;
import static play.test.Helpers.*;
import static org.junit.Assert.*;


public class IntegrationTest {


     //integration test
     //prüft ob device-list (list, update and delete) page geladen wird
    @Test
    public void testDeviceListPageReturnsTrue() {
        running(testServer(9000, fakeApplication(inMemoryDatabase())), HTMLUNIT, browser -> {
            browser.goTo("http://localhost:9000/list");
            assertTrue(browser.pageSource().contains("Device-Liste")); //erwarteter Inhalt "Device-Liste"
        });
    }

    @Test
    public void testDeviceUpdatePageReturnsTrue() {
        running(testServer(9000, fakeApplication(inMemoryDatabase())), HTMLUNIT, browser -> {
            browser.goTo("http://localhost:9000/device/update/device1");
            assertTrue(browser.pageSource().contains("update")); //erwarteter Inhalt "update"
        });
    }

    @Test
    public void testDeviceDeletePageReturnsTrue() {
        running(testServer(9000, fakeApplication(inMemoryDatabase())), HTMLUNIT, browser -> {
            browser.goTo("http://localhost:9000/device/delete/device1");
            assertTrue(browser.pageSource().contains("delete")); //erwarteter Inhalt "delete"
        });
    }

}
