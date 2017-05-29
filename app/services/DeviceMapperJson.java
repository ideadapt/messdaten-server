package services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.Device;
import play.libs.Json;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by Nett on 27.05.2017.
 *
 * Die Klasse DeviceMapperJson stellt Methoden für die Konvertierung
 * von Json nach List<Device> und umgekehrt zur Verfügung.
 */
public class DeviceMapperJson {

    /**
     * Mapped DeviceConfiguration.json nach List<Device>devices und gibt diese Liste zurueck
     * Wirft eine IOException wenn die Liste nicht erstellt werden konnte
     *
     * @return
     */
    public static List<Device> getDeviceListFromConfig() {
        ObjectMapper objectMapper = new ObjectMapper();
        TypeReference<List<Device>> listType = new TypeReference<List<Device>>(){};
        List<Device>devices = null;
        String configPath = "c:\\temp\\DeviceConfiguration.json";
        try {
            devices = objectMapper.readValue(new File(configPath),listType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return devices;
    }

    /**
     * Fuegt der List<Device> einen neuen Device hinzu, falls dieser valid ist.
     * Mapped die Liste ins Json-Config-File
     *
     * Wirft eine IOException falls das Config-File nicht erstellt werden konnte
     *
     * @param newDevice
     * @return
     */
    public static Device addDeviceToConfig(Device newDevice){
        ObjectMapper objectMapper = new ObjectMapper();
        List<Device>devices = getDeviceListFromConfig();

        if(DeviceValidator.validateDevice(devices,newDevice)){
            devices.add(newDevice);
        }
        try {
            objectMapper.writeValue(new File("c:\\temp\\DeviceConfiguration.json"), devices);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newDevice;
    }

    /**
     * Aktualisiert einen Device in der Konfiguration
     *
     * Wirft eine IOException falls das Config-File nicht erstellt werden konnte
     *
     * @param updDevice
     * @return
     */
    public static Device updateDeviceInConfig(Device updDevice){
        ObjectMapper objectMapper = new ObjectMapper();
        List<Device>devices = getDeviceListFromConfig();

        if(DeviceValidator.isValidForUpdate(devices,updDevice)){
            for(Device device : devices){
                if(device.getId().equals(updDevice.getId())){
                    device.setHostIp(updDevice.getHostIp());
                    device.setDataSource(updDevice.getDataSource());
                    device.setGroup(updDevice.getGroup());
                    device.setProtocol(updDevice.getProtocol());
                }
            }
        }
        try {
            objectMapper.writeValue(new File("c:\\temp\\DeviceConfiguration.json"), devices);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return updDevice;
    }

    /**
     * Parsed das File DeviceConfiguration.json in eine JsonNode und gibt diese zurueck
     * Wirft eine IOException wenn die Liste nicht erstellt werden konnte
     *
     * @return
     */
    public static JsonNode getJsonNode(){

        File file = new File( "c:\\temp\\DeviceConfiguration.json");
        JsonNode devicesJson = null;
        try (
                FileInputStream is =new FileInputStream(file);
        ){
            devicesJson = Json.parse(is);
        } catch(IOException e){
            e.printStackTrace();
        }
        return devicesJson;
    }

}
