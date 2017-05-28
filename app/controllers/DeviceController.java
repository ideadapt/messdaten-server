package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import model.Device;

import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import services.DeviceMapperJson;
import services.DeviceValidator;
import views.html.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;


/**
 * Created by Nett on 27.05.2017.
 */
public class DeviceController extends Controller {

    public DeviceController() {
    }

    /**
     * Gibt eine List<Device> aller in DeviceConfiguration.json enthaltenen Devices als Render-Objekt an die List-View.
     *
     * @return
     */
    public CompletionStage<Result> list(){

        CompletionStage<List<Device>> promiseOfDevices = CompletableFuture.
                supplyAsync(()->DeviceMapperJson.getDeviceListFromConfig());

        CompletionStage<Result> promiseOfResult = promiseOfDevices.
                thenApply(devices -> ok(list.render(devices)));

        return promiseOfResult;
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

}
