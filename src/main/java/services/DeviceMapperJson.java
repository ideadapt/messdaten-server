package services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.Device;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Created by Nett on 27.05.2017.
 *
 * Die Klasse DeviceMapperJson stellt Methoden für die Konvertierung
 * von Json nach List<Device> und umgekehrt zur Verfügung.
 */
public class DeviceMapperJson {

    private static final String CONFIGPATH = DeviceMapperJson.class.getResource("/rsc/DeviceConfiguration.json").getFile();

    /**
     * Mapped DeviceConfiguration.json nach List<Device>devices und gibt diese Liste zurueck
     * Wirft eine IOException wenn die Liste nicht erstellt werden konnte
     *
     * @return
     */
    public static List<Device> getDeviceListFromConfig() throws ReadWriteException {
        ObjectMapper objectMapper = new ObjectMapper();
        TypeReference<List<Device>> listType = new TypeReference<List<Device>>() {
        };
        List<Device> devices;
        String configPath = CONFIGPATH;
        try {
            devices = objectMapper.readValue(new File(configPath), listType);
        } catch (IOException e) {
            throw new ReadWriteException("Could not find devices in " + CONFIGPATH + "\n" + e.getMessage());
        }
        return devices;
    }

    /**
     * Fuegt der List<Device> einen neuen Device hinzu, falls dieser valid ist.
     * Mapped die Liste ins Json-Config-File
     *
     * Wirft eine ReadWriteException falls das Config-File nicht erstellt werden konnte
     *
     * @param newDevice
     * @return
     */
    public static Device addDeviceToConfig(Device newDevice)throws ReadWriteException{
        ObjectMapper objectMapper = new ObjectMapper();
        List<Device>devices = getDeviceListFromConfig();

        if(DeviceValidator.validateDevice(devices,newDevice)){
            devices.add(newDevice);
        }else{
            throw new  ReadWriteException( "Device " + newDevice.getName() + " not valid to add to configuration");
        }
        try {
            objectMapper.writeValue(new File(CONFIGPATH), devices);
        } catch (IOException e) {
            throw new  ReadWriteException("Could not save new device-list to " + CONFIGPATH
                    + ", after adding " + newDevice.getName() + "  to list"+ "\n" + e.getMessage());
        }
        return newDevice;
    }

    /**
     * Aktualisiert einen Device in der Konfiguration
     *
     * Wirft eine ReadWriteException falls das Config-File nicht erstellt werden konnte
     *
     * @param updDevice
     * @return
     */
    public static Device updateDeviceInConfig(Device updDevice)throws ReadWriteException{
        ObjectMapper objectMapper = new ObjectMapper();
        List<Device>devices = getDeviceListFromConfig();

        if(DeviceValidator.isValidForUpdate(devices,updDevice)){
            for(Device device : devices){
                if(device.getName().equals(updDevice.getName())){
                    device.setHostIp(updDevice.getHostIp());
                    device.setDataSource(updDevice.getDataSource());
                    device.setGroup(updDevice.getGroup());
                    device.setProtocol(updDevice.getProtocol());
                }
            }
        }else{
            throw new  ReadWriteException( "Device " + updDevice.getName() + " not valid to update in configuration");
        }
        try {
            objectMapper.writeValue(new File(CONFIGPATH), devices);
        } catch (IOException e) {
            throw new  ReadWriteException("Could not save new device-list to " + CONFIGPATH
                    + ", after updating " + updDevice.getName() + "  in list"+ "\n" + e.getMessage());
        }
        return updDevice;
    }

    /**
     * Parsed das File DeviceConfiguration.json in eine JsonNode und gibt diese zurueck
     * Wirft eine IOException wenn die Liste nicht erstellt werden konnte
     *
     * @return
     */
    public static JsonNode getJsonNode() throws ReadWriteException{

        File file = new File(CONFIGPATH);
        JsonNode devicesJson;
        try {
            FileInputStream is = new FileInputStream(file);
            devicesJson = new ObjectMapper().createParser(is).readValueAsTree();
        } catch(IOException e){
            throw new  ReadWriteException("Could not find devices in " + CONFIGPATH + "\n" + e.getMessage());
        }
        return devicesJson;
    }

    /**
     * Findet in der aktuellen Konfiguration einen Device ueber seinen Namen und gibt diesen zurueck.
     * Wird kein Device gefunden gibt die Methode null zurueck.
     *
     * @param name
     * @return
     */
    public static Device findDevice(String name){

        List<Device>devices = getDeviceListFromConfig();

        for(Device device : devices){
            if(device.getName().equals(name)){
                return device;
            }
        }
        return null;
    }

    /**
     * Loescht einen Device aus der Konfiguration
     *
     * Wirft eine ReadWriteException falls das Config-File nicht erstellt werden konnte
     *
     * @param delDevice
     * @return
     */
    public static boolean deleteDeviceInConfig(Device delDevice) throws ReadWriteException{
        ObjectMapper objectMapper = new ObjectMapper();
        List<Device>devices = getDeviceListFromConfig();
        boolean deleted = false;

        deleted = devices.remove(delDevice);
        if(!deleted){
            throw new  ReadWriteException("Could not delete device " + delDevice.getName() +" from list");
        }
        try {
            objectMapper.writeValue(new File(CONFIGPATH), devices);
        } catch (IOException e) {
            throw new  ReadWriteException("Could not save new device-list to " + CONFIGPATH
                    + ", after removing " + delDevice.getName() + "  from list"+ "\n" + e.getMessage());
        }
        return deleted;
    }

    /**
     * Gibt den Pfad der Data-Source des gesuchen Devices gemaess Konfiguration zurueck
     *
     * @param deviceName
     * @return
     */
    public static  String getMeasurementValuePath(String deviceName){
        List<Device>devices = getDeviceListFromConfig();

        for(Device device : devices){
            if(device.getName().equals(deviceName)){
                return device.getDataSource();
            }
        }
        return null;
    }

    /**
     * Gibt das Protokoll des gesuchen Devices gemaess Konfiguration zurueck
     *
     * @param deviceName
     * @return
     */
    public static Optional<String> getMeasurementValueProtocol(String deviceName) {
        List<Device> devices = getDeviceListFromConfig();

        for (Device device : devices) {
            if (device.getName().equals(deviceName)) {
                return Optional.of(device.getProtocol());
            }
        }
        return Optional.empty();
    }
}
