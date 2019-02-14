package com.example.sedaulusal.hiwijob.ruleEngine;

import com.example.sedaulusal.hiwijob.device.SensorInfo;

/**
 * Created by sedaulusal on 21.06.17.
 */

public class RuleEngineSensorInfo {

    long rulesensorid;
    String rulesensorname;
    String rulesensorgenerateid;
    String ruleopeator;
    String rulevalue;



    SensorInfo sensorInfo;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean active;




    public RuleEngineSensorInfo(){
        this.active = false;
    }

    public RuleEngineSensorInfo(long rulesensorid, String rulesensorname,String rulesensorgenerateid, String ruleopeator, String rulevalue){
        this.rulesensorid = rulesensorid;
        this.rulesensorname= rulesensorname;
        this.rulesensorgenerateid = rulesensorgenerateid;
        this.ruleopeator= ruleopeator;
        this.rulevalue= rulevalue;
    }

    public RuleEngineSensorInfo(SensorInfo sensorInfo,String rulesensorgenerateid){
        this.rulesensorgenerateid = rulesensorgenerateid;
        this.sensorInfo= sensorInfo;
    }

    public RuleEngineSensorInfo(String rulesensorname,String rulesensorgenerateid, String ruleopeator, String rulevalue){
        this.rulesensorid = rulesensorid;
        this.rulesensorname= rulesensorname;
        this.rulesensorgenerateid = rulesensorgenerateid;
        this.ruleopeator= ruleopeator;
        this.rulevalue= rulevalue;
    }


    public RuleEngineSensorInfo(long rulesensorid, String rulesensorgenerateid, String ruleopeator, String rulevalue){
        this.rulesensorid = rulesensorid;
        //this.rulesensorname= rulesensorname;
        this.rulesensorgenerateid = rulesensorgenerateid;
        this.ruleopeator= ruleopeator;
        this.rulevalue= rulevalue;
    }




    public SensorInfo getSensorInfo() {
        return sensorInfo;
    }

    public void setSensorInfo(SensorInfo sensorInfo) {
        this.sensorInfo = sensorInfo;
    }


    public long getRulesensorid() {
        return rulesensorid;
    }

    public String getRulesensorname() {
        return rulesensorname;
    }

    public String getRuleopeator() {
        return ruleopeator;
    }

    public String getRulevalue() {
        return rulevalue;
    }


    public void setRulesensorid(long rulesensorid) {
        this.rulesensorid = rulesensorid;
    }

    public void setRulesensorname(String rulesensorname) {
        this.rulesensorname = rulesensorname;
    }

    public void setRuleopeator(String ruleopeator) {
        this.ruleopeator = ruleopeator;
    }

    public void setRulevalue(String rulevalue) {
        this.rulevalue = rulevalue;
    }

    public void setRulesensorgenerateid(String rulesensorgenerateid) {
        this.rulesensorgenerateid = rulesensorgenerateid;
    }

    public String getRulesensorgenerateid() {
        return rulesensorgenerateid;
    }



}


