package com.example.sedaulusal.hiwijob.device;

/**
 * Created by sedaulusal on 25.10.17.
 */

public class ActuatorInfo {


        public int actuatorid;
        public String actuatorname;
        public byte[] actuatorimage;
        public String generateactuatorid;
        public boolean deployed;


    public String actuatorTyp;
    public String actuatorPinset;






    public ActuatorInfo(){
        this.deployed = false;
    }


    //byte sensorimage gelöscht fürs testen
        public ActuatorInfo(String actuatorname, byte[] actuatorimage) {
            this.actuatorname = actuatorname;
            this.actuatorimage = actuatorimage;
        }

    //byte sensorimage gelöscht fürs testen
    public ActuatorInfo(String actuatorname, byte[] actuatorimage, String actuatorPinset) {
        this.actuatorname = actuatorname;
        this.actuatorimage = actuatorimage;
        this.actuatorPinset = actuatorPinset;
    }

        //byte sensorimage gelöscht fürs testen
        public ActuatorInfo(int actuatorid, String actuatorname, byte[] actuatorimage) {
            this.actuatorname = actuatorname;
            this.actuatorimage = actuatorimage;
            this.actuatorid = actuatorid;
        }

    //byte sensorimage gelöscht fürs testen
    public ActuatorInfo(String generateactuatorid, String actuatorname, byte[] actuatorimage) {
        this.actuatorname = actuatorname;
        this.actuatorimage = actuatorimage;
        this.generateactuatorid = generateactuatorid;
    }

    //byte sensorimage gelöscht fürs testen
    public ActuatorInfo(String generateactuatorid, String actuatorname, byte[] actuatorimage, String actuatorPinset, String actuatorTyp) {
        this.actuatorname = actuatorname;
        this.actuatorimage = actuatorimage;
        this.generateactuatorid = generateactuatorid;
        this.actuatorPinset = actuatorPinset;
        this.actuatorTyp = actuatorTyp;
    }

    //byte sensorimage gelöscht fürs testen
    public ActuatorInfo(String actuatorname, byte[] actuatorimage, String actuatorPinset, String actuatorTyp) {
        this.actuatorname = actuatorname;
        this.actuatorimage = actuatorimage;
        this.actuatorPinset = actuatorPinset;
        this.actuatorTyp = actuatorTyp;
    }


    public String getGenerateActuatorId() {
        return generateactuatorid;
    }

    public void setGenerateActuatorId(String generatesensorid) {
        this.generateactuatorid = generatesensorid;
    }
        public int getId() {
            return actuatorid;
        }

        public void setId(int id) {
            this.actuatorid = id;
        }

        public String getName() {
            return actuatorname;
        }

        public void setName(String name) {
            this.actuatorname = name;
        }

        public byte[] getImage() {
            return actuatorimage;
        }

        public void setImage(byte[] image) {
            this.actuatorimage = image;
        }

    public String getActuatorTyp() {
        return actuatorTyp;
    }

    public void setActuatorTyp(String actuatorTyp) {
        this.actuatorTyp = actuatorTyp;
    }

    public String getActuatorPinset() {
        return actuatorPinset;
    }

    public void setActuatorPinset(String actuatorPinset) {
        this.actuatorPinset = actuatorPinset;
    }
    }



