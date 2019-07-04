package com.example.sedaulusal.hiwijob.monitoring;

public class MonitoringAdapters {

    protected String name;
    protected String unit;
    protected String parameter;
    protected String optionalIP;
    protected String state;
    protected String deviceName;
    public boolean deployed;




    public MonitoringAdapters() {
    }

    public MonitoringAdapters(String name, String unit, String deviceName) {
        this.name = name;
        this.unit = unit;
        this.deviceName = deviceName;
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

    public void setOptionalIP(String optionalIP) {
        this.optionalIP = optionalIP;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getParameter() {
        return parameter;
    }

    public String getOptionalIP() {
        return optionalIP;
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
}
