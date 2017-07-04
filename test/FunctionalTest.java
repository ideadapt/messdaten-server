import play.mvc.Result;
import static org.junit.Assert.*;
import org.junit.Test;
import static play.test.Helpers.*;
import play.mvc.Http.RequestBuilder;


public class FunctionalTest {

    //test Controller Action über Routing
    @Test
    public void testBadRouteReturnsTrue() {
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
    public void testGoodRouteReturnsTrue() {
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
}
