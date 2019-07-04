package com.example.sedaulusal.hiwijob.device;

/**
 * Created by sedaulusal on 18.05.17.
 */

public class DeviceInfo {


    protected String name;
    protected String macid;
    private int id;
    private byte[] image;
    protected String devicetype;
    protected String optionalIP;
    protected String username;
    protected String password;
    protected String plattformid;

    protected String state;


    public DeviceInfo() {
    }

    public DeviceInfo(String name, String macid, byte[] image) {
        this.name = name;
        this.macid = macid;
        this.image = image;
    }

    public DeviceInfo(String name, String macid, String plattformid, byte[] image) {
        this.name = name;
        this.macid = macid;
        this.image = image;
        this.plattformid = plattformid;
    }

    public DeviceInfo(String name, String macid, String plattformid, byte[] image, String username, String optionalIP) {
        this.name = name;
        this.macid = macid;
        this.image = image;
        this.plattformid = plattformid;
        this.username = username;
        this.optionalIP = optionalIP;
    }

    public DeviceInfo(String name, String macid, String plattformid) {
        this.name = name;
        this.macid = macid;
        this.plattformid = plattformid;
    }



    public DeviceInfo(int id, String name, String macid, byte[] image) {
        this.name = name;
        this.macid = macid;
        this.image = image;
        this.id = id;

    }

    public DeviceInfo(String name, String macid, byte[] image, String devicetype) {
        this.name = name;
        this.macid = macid;
        this.image = image;
        this.devicetype = devicetype;

    }

    public DeviceInfo(int id, String name, String macid, byte[] image, String devicetype) {
        this.name = name;
        this.macid = macid;
        this.image = image;
        this.id = id;
        this.devicetype = devicetype;

    }

    public DeviceInfo(int id, String plattformid, String name, String macid, byte[] image, String devicetype, String optionalIP, String username, String password) {
        this.id = id;
        this.name = name;
        this.macid = macid;
        this.image = image;
        this.devicetype = devicetype;
        this.optionalIP = optionalIP;
        this.username = username;
        this.password = password;
        this.plattformid =plattformid;

    }


    public DeviceInfo(int id, String plattformid, String name, String macid, byte[] image, String devicetype, String optionalIP, String username, String password,String state) {
        this.id = id;
        this.name = name;
        this.macid = macid;
        this.image = image;
        this.devicetype = devicetype;
        this.optionalIP = optionalIP;
        this.username = username;
        this.password = password;
        this.plattformid =plattformid;
        this.state = state;

    }

    public DeviceInfo(int id, String name, String macid, byte[] image, String devicetype, String optionalIP, String username, String password) {
        this.id = id;
        this.name = name;
        this.macid = macid;
        this.image = image;
        this.devicetype = devicetype;
        this.optionalIP = optionalIP;
        this.username = username;
        this.password = password;

    }

    public DeviceInfo( String name, String macid,String plattformid, byte[] image, String devicetype, String optionalIP, String username, String password) {
        this.name = name;
        this.macid = macid;
        this.image = image;
        this.devicetype = devicetype;
        this.optionalIP = optionalIP;
        this.username = username;
        this.password = password;
        this.plattformid = plattformid;

    }

    public DeviceInfo(String name, String macid, byte[] image, String devicetype, String optionalIP, String username, String password) {
        this.name = name;
        this.macid = macid;
        this.image = image;
        this.devicetype = devicetype;
        this.optionalIP = optionalIP;
        this.username = username;
        this.password = password;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getMacid() {
        return macid;
    }

    public void setMacid(String macid) {
        this.macid = macid;
    }
    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getDevicetype() {
        return devicetype;
    }

    public String getOptionalIP() {
        return optionalIP;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setDevicetype(String devicetype) {
        this.devicetype = devicetype;
    }

    public void setOptionalIP(String optionalIP) {
        this.optionalIP = optionalIP;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPlattformid() {
        return plattformid;
    }

    public void setPlattformid(String plattformid) {
        this.plattformid = plattformid;
    }

    //for the Device state possible states are available or unavailable
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}


