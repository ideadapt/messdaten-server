package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import model.Device;
import model.MeasurementValueXml;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.DeviceMapperJson;
import services.MeasurementValueReader;
import services.ReadWriteException;
import views.html.delete;
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
     * Liefert den aktuellen Messwert mit Zeitstempel des Devices gemäss deviceName
     *
     * @param deviceName
     * @return
     */
    public Result getMeasureValue(String deviceName){

        MeasurementValueXml actualValue = null;
        try{
            actualValue = MeasurementValueReader.getActualValueFromXml(deviceName);
        }catch(ReadWriteException ex){
            return badRequest(ex.getMessage());
        }
        return ok(Json.toJson(actualValue));
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
        Form<Device> saveForm = deviceForm.bindFromRequest();
        if(saveForm.hasErrors()) {
            flash("error", "Device not added please correct the form below.");
            return badRequest(details.render(saveForm));
        }

        Device newDevice = saveForm.get();
        try{
            DeviceMapperJson.addDeviceToConfig(newDevice);
            flash("success",
                    String.format("Successfully added %s to configuration" , newDevice.getName()));
        }catch(ReadWriteException ex){
            flash("error", ex.getMessage());
            return badRequest(details.render(saveForm));
        }

        return redirect(routes.DeviceController.list());
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
        try {
            DeviceMapperJson.updateDeviceInConfig(updateDevice);
            flash("success",
                    String.format("Successfully updated %s in configuration" , updateDevice.getName()));
        }catch(ReadWriteException ex){
            flash("error", ex.getMessage());
            return badRequest(update.render(updateForm));
        }

        return redirect(routes.DeviceController.list());
    }

    /**
     * Gibt ein ausgefuelltes Form für den gesuchten Device zurück
     *
     * @return
     */
    public Result deleteDeviceForm(String name){

        Device deleteDevice = DeviceMapperJson.findDevice(name);
        Form<Device> deleteForm = deviceForm.fill(deleteDevice);

        if (deleteForm == null) {
            return notFound(String.format("Device %s does not exist.", name));
        }
        return ok(delete.render(deleteForm));
    }

    /**
     * Loescht einen Device in der Konfiguration
     *
     * Fehlerfall: Erzeugt eine Error-Meldung im main-template und gibt das fehlerhaft ausgefüllte Formular zurueck
     *
     * Erfolg: Erzeugt eine Success-Meldung im main-template und gibt die Liste aller konfigurierten Devices zurueck
     *
     * @return
     */
    public Result deleteDevice(){

        Form<Device> deleteForm = deviceForm.bindFromRequest();

        Device deleteDevice = deleteForm.get();
        if(DeviceMapperJson.deleteDeviceInConfig(deleteDevice)){
            flash("success",
                    String.format("Successfully deleted %s in configuration" , deleteDevice.getName()));
        }else{
            flash("error", "Device not deleted please check the form below.");
            return badRequest(delete.render(deleteForm));
        }
        return redirect(routes.DeviceController.list());
    }

}
