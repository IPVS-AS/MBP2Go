package com.example.sedaulusal.hiwijob.ruleEngine;

import com.example.sedaulusal.hiwijob.device.ActuatorInfo;

/**
 * Created by sedaulusal on 21.06.17.
 */

public class RuleEngineActuatorInfo {

    long ruleactuatorid;
    String ruleactuatorname;
    String ruleactuatorgenerateid;
    String rulevalue;
    ActuatorInfo ruleActuatorInfo;

    // String ruleopeator;
    // String rulevalue;


    public RuleEngineActuatorInfo() {

    }

    public RuleEngineActuatorInfo(String ruleactuatorname, String ruleactuatorgenerateid, String rulevalue) {
        //this.ruleactuatorid = ruleactuatorid;
        this.ruleactuatorname = ruleactuatorname;
        this.ruleactuatorgenerateid = ruleactuatorgenerateid;
        //this.ruleopeator= ruleopeator;
        this.rulevalue= rulevalue;
    }

    public RuleEngineActuatorInfo( String ruleactuatorgenerateid, String ruleactuatorname, ActuatorInfo ruleActuatorInfo) {
        //this.ruleactuatorid = ruleactuatorid;
        this.ruleactuatorname = ruleactuatorname;
        this.ruleactuatorgenerateid = ruleactuatorgenerateid;
        //this.ruleopeator= ruleopeator;
        this.ruleActuatorInfo= ruleActuatorInfo;
    }

    public RuleEngineActuatorInfo(long ruleactuatorid, String ruleactuatorname, String ruleactuatorgenerateid, String rulevalue) {
        this.ruleactuatorid = ruleactuatorid;
        this.ruleactuatorname = ruleactuatorname;
        this.ruleactuatorgenerateid = ruleactuatorgenerateid;
        //this.ruleopeator= ruleopeator;
        this.rulevalue= rulevalue;
    }

    public RuleEngineActuatorInfo(long ruleactuatorid,  String ruleactuatorgenerateid, String rulevalue) {
        this.ruleactuatorid = ruleactuatorid;
        //this.ruleactuatorname = ruleactuatorname;
        this.ruleactuatorgenerateid = ruleactuatorgenerateid;
        //this.ruleopeator= ruleopeator;
        this.rulevalue= rulevalue;
    }

    public long getRuleactuatorid() {
        return ruleactuatorid;
    }

    public String getRuleactuatorname() {
        return ruleactuatorname;
    }

    public String getRuleactuatorgenerateid() {
        return ruleactuatorgenerateid;
    }

    public void setRuleactuatorid(long ruleactuatorid) {
        this.ruleactuatorid = ruleactuatorid;
    }

    public void setRuleactuatorname(String ruleactuatorname) {
        this.ruleactuatorname = ruleactuatorname;
    }

    public void setRuleactuatorgenerateid(String ruleactuatorgenerateid) {
        this.ruleactuatorgenerateid = ruleactuatorgenerateid;
    }


    public String getRulevalue() {
        return rulevalue;
    }

    public void setRulevalue(String rulevalue) {
        this.rulevalue = rulevalue;
    }

    public ActuatorInfo getRuleActuatorInfo() {
        return ruleActuatorInfo;
    }

    public void setRuleActuatorInfo(ActuatorInfo ruleActuatorInfo) {
        this.ruleActuatorInfo = ruleActuatorInfo;
    }

}





    /*public RuleEngineActuatorInfo(long ruleactuatorid, String ruleactuatorname, String ruleactuatorgenerateid){
        this.ruleactuatorid = ruleactuatorid;
        this.ruleactuatorname= ruleactuatorname;
        this.ruleactuatorgenerateid = ruleactuatorgenerateid;
        //this.ruleopeator= ruleopeator;
        //this.rulevalue= rulevalue;
    }*/










