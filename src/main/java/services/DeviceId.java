package services;

public class DeviceId {
    public String value;

    public DeviceId(String value) {
        // \d{1,3}
        this.value = value;
    }
}
