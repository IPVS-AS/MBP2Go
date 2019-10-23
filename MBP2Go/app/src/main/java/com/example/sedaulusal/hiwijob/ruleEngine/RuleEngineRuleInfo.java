package com.example.sedaulusal.hiwijob.ruleEngine;

/**
 * Created by sedaulusal on 18.05.17.
 */

public class RuleEngineRuleInfo {


    long ruleengineruleid;
    String rulename;
    String rulestatus;
    String ruleJSONString;
    String ruleassociation;
    byte[] ruleImage;


    public RuleEngineRuleInfo() {
    }

   public RuleEngineRuleInfo( long ruleengineruleid,String rulename,String rulestatus,byte[] ruleImage,String ruleassociation){
        this.ruleengineruleid = ruleengineruleid;
        this.rulename = rulename;
        this.rulestatus = rulestatus;
        this.ruleImage= ruleImage;
        this.ruleassociation = ruleassociation;
    }

    public long getRuleengineruleid() {
        return ruleengineruleid;
    }

    public String getRulename() {
        return rulename;
    }

    public String getRulestatus() {
        return rulestatus;
    }

    public String getRuleJSONString() {
        return ruleJSONString;
    }

    public String getRuleassociation() {
        return ruleassociation;
    }

    public void setRuleengineruleid(long ruleengineruleid) {
        this.ruleengineruleid = ruleengineruleid;
    }

    public void setRulename(String rulename) {
        this.rulename = rulename;
    }

    public void setRulestatus(String rulestatus) {
        this.rulestatus = rulestatus;
    }

    public void setRuleJSONString(String ruleJSONString) {
        this.ruleJSONString = ruleJSONString;
    }

    public void setRuleassociation(String ruleassociation) {
        this.ruleassociation = ruleassociation;
    }

    public byte[] getRuleImage() {
        return ruleImage;
    }

    public void setRuleImage(byte[] ruleImage) {
        this.ruleImage = ruleImage;
    }


    /*protected String name;
    protected String macid;
    private int id;
    private byte[] image;
    protected String devicetype;
    protected String monitoringID;
    protected String username;
    protected String password;
    protected String plattformid;



    public RuleEngineRuleInfo() {
    }

    public RuleEngineRuleInfo(String name, String macid, byte[] image) {
        this.name = name;
        this.macid = macid;
        this.image = image;
    }

    public RuleEngineRuleInfo(String name, String macid, String plattformid, byte[] image) {
        this.name = name;
        this.macid = macid;
        this.image = image;
        this.plattformid = plattformid;
    }

    public RuleEngineRuleInfo(String name, String macid, String plattformid, byte[] image, String username, String monitoringID) {
        this.name = name;
        this.macid = macid;
        this.image = image;
        this.plattformid = plattformid;
        this.username = username;
        this.monitoringID = monitoringID;
    }

    public RuleEngineRuleInfo(String name, String macid, String plattformid) {
        this.name = name;
        this.macid = macid;
        this.plattformid = plattformid;
    }



    public RuleEngineRuleInfo(int id, String name, String macid, byte[] image) {
        this.name = name;
        this.macid = macid;
        this.image = image;
        this.id = id;

    }

    public RuleEngineRuleInfo(String name, String macid, byte[] image, String devicetype) {
        this.name = name;
        this.macid = macid;
        this.image = image;
        this.devicetype = devicetype;

    }

    public RuleEngineRuleInfo(int id, String name, String macid, byte[] image, String devicetype) {
        this.name = name;
        this.macid = macid;
        this.image = image;
        this.id = id;
        this.devicetype = devicetype;

    }

    public RuleEngineRuleInfo(int id, String plattformid, String name, String macid, byte[] image, String devicetype, String monitoringID, String username, String password) {
        this.id = id;
        this.name = name;
        this.macid = macid;
        this.image = image;
        this.devicetype = devicetype;
        this.monitoringID = monitoringID;
        this.username = username;
        this.password = password;
        this.plattformid =plattformid;

    }

    public RuleEngineRuleInfo(int id, String name, String macid, byte[] image, String devicetype, String monitoringID, String username, String password) {
        this.id = id;
        this.name = name;
        this.macid = macid;
        this.image = image;
        this.devicetype = devicetype;
        this.monitoringID = monitoringID;
        this.username = username;
        this.password = password;

    }

    public RuleEngineRuleInfo(String name, String macid, String plattformid, byte[] image, String devicetype, String monitoringID, String username, String password) {
        this.name = name;
        this.macid = macid;
        this.image = image;
        this.devicetype = devicetype;
        this.monitoringID = monitoringID;
        this.username = username;
        this.password = password;
        this.plattformid = plattformid;

    }

    public RuleEngineRuleInfo(String name, String macid, byte[] image, String devicetype, String monitoringID, String username, String password) {
        this.name = name;
        this.macid = macid;
        this.image = image;
        this.devicetype = devicetype;
        this.monitoringID = monitoringID;
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

    public String getMonitoringID() {
        return monitoringID;
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

    public void setMonitoringID(String monitoringID) {
        this.monitoringID = monitoringID;
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
    }*/
}


