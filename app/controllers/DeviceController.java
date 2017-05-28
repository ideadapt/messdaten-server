package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import model.Device;

import play.mvc.Controller;
import play.mvc.Result;
import services.DeviceMapperJson;
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
                supplyAsync(()->DeviceMapperJson.getDeviceListJson());

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
}
