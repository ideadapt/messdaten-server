import org.junit.*;

import play.mvc.*;
import play.test.*;

import static play.test.Helpers.*;
import static org.junit.Assert.*;

import static org.fluentlenium.core.filter.FilterConstructor.*;

public class IntegrationTest {

    /**
     * integration test
     * just check if the device-liste page is being shown
     */
    @Test
    public void test() {
        running(testServer(9000, fakeApplication(inMemoryDatabase())), HTMLUNIT, browser -> {
            browser.goTo("http://localhost:9000/list");
            assertTrue(browser.pageSource().contains("Device-Liste"));
        });
    }

}
