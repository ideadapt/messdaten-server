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
import views.html.update;

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
     * Gibt ein Form auf Basis der Klasse Device zurück
     *
     * @return
     */
    public Result newDeviceForm(){

        return ok(details.render(deviceForm));
    }

    /**
     * Gibt ein ausgefuelltes Form für den gesuchten Device zurück
     *
     * @return
     */
    public Result updateDeviceForm(String name){

        Device updateDevice = DeviceMapperJson.findDevice(name);
        Form<Device> updateForm = deviceForm.fill(updateDevice);

        if (updateForm == null) {
            return notFound(String.format("Device %s does not exist.", name));
        }
        return ok(update.render(updateForm));
    }

    /**
     * Aktualisiert einen Device in der Konfiguration
     *
     * Fehlerfall: Erzeugt eine Error-Meldung im main-template und gibt das fehlerhaft ausgefüllte Formular zurueck
     *
     * Erfolg: Erzeugt eine Success-Meldung im main-template und gibt die Liste aller konfigurierten Devices zurueck
     *
     * @return
     */
    public Result updateDevice(){

        Form<Device> updateForm = deviceForm.bindFromRequest();
        if(updateForm.hasErrors()) {
            flash("error", "Device not updated please correct the form below.");
            return badRequest(update.render(updateForm));
        }

        Device updateDevice = updateForm.get();
        DeviceMapperJson.updateDeviceInConfig(updateDevice);
        flash("success",
                String.format("Successfully updated device %s in configuration" , updateDevice.getName()));

        return redirect(routes.DeviceController.list());
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
        Form<Device> saveForm = deviceForm.bindFromRequest();
        if(saveForm.hasErrors()) {
            flash("error", "Device not added please correct the form below.");
            return badRequest(details.render(saveForm));
        }

        Device newDevice = saveForm.get();
        DeviceMapperJson.addDeviceToConfig(newDevice);
        flash("success",
                String.format("Successfully added device %s to configuration" , newDevice.getName()));

        return redirect(routes.DeviceController.list());
    }
}
