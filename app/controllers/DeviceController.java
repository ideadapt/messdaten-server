package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import model.Device;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import services.DeviceMapperJson;
import views.html.details;
import views.html.list;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;


/**
 * Created by Nett on 27.05.2017.
 */
public class DeviceController extends Controller {

    private FormFactory formFactory;
    private Form<Device> deviceForm;

    @Inject
    public DeviceController(FormFactory formFactory) {
        this.formFactory = formFactory;
        this.deviceForm = formFactory.form(Device.class);
    }

    /**
     * Gibt eine List<Device> aller in DeviceConfiguration.json enthaltenen Devices als Render-Objekt an die List-View.
     *
     * @return
     */
    public Result list(){

        List<Device> devices = DeviceMapperJson.getDeviceListFromConfig();

        return ok(list.render(devices));
    }

    /**
     * Gibt ein JsonNode aller in DeviceConfiguration.json enthaltenen Devices als Render-Objekt an die List-View.
     *
     * @return
     */
    public CompletionStage<Result> listJson(){

        CompletionStage<JsonNode> promiseOfReports = CompletableFuture.
                supplyAsync(()->DeviceMapperJson.getJsonNode());

        CompletionStage<Result> promiseOfResult = promiseOfReports.
                thenApply(devices -> ok(devices));

        return promiseOfResult;
    }

    /**
     * Fuegt der Konfiguration einen neuen Device hinzu
     *
     * @return
     */
    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> newDevice(){
        JsonNode json = request().body().asJson();
        Device newDevice = Json.fromJson(json,Device.class);

        CompletionStage<Device> promiseOfDevice = CompletableFuture.
                supplyAsync(() -> DeviceMapperJson.addDeviceToConfig(newDevice));

        CompletionStage<Result> promiseOfResult = promiseOfDevice.
                thenApply(device -> ok(Json.toJson(device)));

        return promiseOfResult;
    }

    /**
     * Aktualisiert einen Device in der Konfiguration
     *
     * @return
     */
    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> updateDevice(){
        JsonNode json = request().body().asJson();
        Device newDevice = Json.fromJson(json,Device.class);

        CompletionStage<Device> promiseOfDevice = CompletableFuture.
                supplyAsync(() -> DeviceMapperJson.updateDeviceInConfig(newDevice));

        CompletionStage<Result> promiseOfResult = promiseOfDevice.
                thenApply(device -> ok(Json.toJson(device)));

        return promiseOfResult;
    }

    /**
     * Gibt ein Form auf Basis der Klasse Device zurück
     *
     * @return
     */
    public Result newDeviceForm(){

        return ok(details.render(deviceForm));
    }

    /**
     * Speichert die Eigenschaften eines Devices in der Konfiguration
     *
     * Fehlerfall: Erzeugt eine Error-Meldung im main-template und gibt das fehlerhaft ausgefüllte Formular zurueck
     *
     * Erfolg: Erzeugt eine Success-Meldung im main-template und gibt die Liste aller konfigurierten Devices zurueck
     *
     *
     * @return
     */
    public Result save() {
        Form<Device> boundForm = deviceForm.bindFromRequest();
        if(boundForm.hasErrors()) {
            flash("error", "Device not added please correct the form below.");
            return badRequest(details.render(boundForm));
        }

        Device newDevice = boundForm.get();
        DeviceMapperJson.addDeviceToConfig(newDevice);
        flash("success",
                String.format("Successfully added device %s to configuration" , newDevice.getName()));

        return redirect(routes.DeviceController.list());
    }
}
