package com.example.sedaulusal.hiwijob.monitoring;

public class MonitoringAdapters {

    protected String name;
    protected String unit;
    protected String parameter;
    protected String monitoringID;
    protected String state;
    protected String deviceName;
    protected String deviceId;
    public boolean deployed;




    public MonitoringAdapters() {
    }

    public MonitoringAdapters(String name, String unit,String monitoringID,String state, String deviceName, String deviceId) {
        this.name = name;
        this.unit = unit;
        this.state = state;
        this.monitoringID = monitoringID;
        this.deviceName = deviceName;
        this.deviceId = deviceId;

    }

    public boolean isDeployed() {
        return deployed;
    }

    public void setDeployed(boolean deployed) {
        this.deployed = deployed;
    }

    public String getName() {
        return name;
    }

    public String getUnit() {
        return unit;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public void setMonitoringID(String monitoringID) {
        this.monitoringID = monitoringID;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getParameter() {
        return parameter;
    }

    public String getMonitoringID() {
        return monitoringID;
    }

    public String getState() {
        return state;
    }



    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }


    public String getDeviceName() {
        return deviceName;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}


