package com.example.sedaulusal.hiwijob.device;

public class OperatorInfo {
    public int operatorid;
    public String unit;
    public String description;
    public String operatorname;
    public String parametername;
    public String parametertype;
    public String parametermandatory;


    public OperatorInfo(int operatorid, String unit, String description, String operatorname, String parametername, String parametertype, String parametermandatory) {
        this.operatorid = operatorid;
        this.unit = unit;
        this.description = description;
        this.operatorname = operatorname;
        this.parametername = parametername;
        this.parametertype = parametertype;
        this.parametermandatory = parametermandatory;
    }

    public int getOperatorid() {
        return operatorid;
    }

    public void setOperatorid(int operatorid) {
        this.operatorid = operatorid;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOperatorname() {
        return operatorname;
    }

    public void setOperatorname(String operatorname) {
        this.operatorname = operatorname;
    }

    public String getParametername() {
        return parametername;
    }

    public void setParametername(String parametername) {
        this.parametername = parametername;
    }

    public String getParametertype() {
        return parametertype;
    }

    public void setParametertype(String parametertype) {
        this.parametertype = parametertype;
    }

    public String getParametermandatory() {
        return parametermandatory;
    }

    public void setParametermandatory(String parametermandatory) {
        this.parametermandatory = parametermandatory;
    }




}
