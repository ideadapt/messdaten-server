package model;

/**
 * Created by Nett on 27.05.2017.
 *
 * Die Klasse Device beschreibt die für ein Messmittel notwendigen Eigenschaften
 */
public class Device {

    private String name = "";
    private String hostIp = "";
    private String dataSource = "";
    private String group = "";
    private String protocol = "";

    public Device() {
    }

    public Device(String name, String hostIp, String dataSource, String group, String protocol) {
        this.name = name;
        this.hostIp = hostIp;
        this.dataSource = dataSource;
        this.group = group;
        this.protocol = protocol;
    }

    public String getName() {
        return name;
    }

    public String getHostIp() {
        return hostIp;
    }

    public String getDataSource() {
        return dataSource;
    }

    public String getGroup() {
        return group;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof Device)) {
            return false;
        }

        Device device = (Device) obj;

        return device.name.equals(name);
    }
}
