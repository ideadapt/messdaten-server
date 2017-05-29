package model;

/**
 * Created by Nett on 27.05.2017.
 *
 * Die Klasse Device beschreibt die für ein Messmittel notwendigen Eigenschaften
 */
public class Device {

    private String id = "";
    private String hostIp = "";
    private String dataSource = "";
    private int group = 0;
    private String protocol = "";

    public Device() {
    }

    public Device(String id, String hostIp, String dataSource, int group, String protocol) {
        this.id = id;
        this.hostIp = hostIp;
        this.dataSource = dataSource;
        this.group = group;
        this.protocol = protocol;
    }

    public String getId() {
        return id;
    }

    public String getHostIp() {
        return hostIp;
    }

    public String getDataSource() {
        return dataSource;
    }

    public int getGroup() {
        return group;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
}
