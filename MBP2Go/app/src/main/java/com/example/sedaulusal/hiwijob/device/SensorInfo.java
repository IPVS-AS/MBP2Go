package com.example.sedaulusal.hiwijob.device;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.sedaulusal.hiwijob.R;

import static android.R.attr.duration;

/**
 * Created by sedaulusal on 21.06.17.
 */

public class SensorInfo {

    public int sensorid;
    public String generatesensorid;
    public String sensorname;
    public byte[] sensorimage;
    public int sensorCount;
    public String sensorTyp;
    public String sensorPinset;

    public boolean deployed;
    public boolean running;

    protected static final String NAME_PREFIX = "Name_";
    protected static final String EMAIL_PREFIX = "email_";



    public SensorInfo(){
        this.deployed = false;
        this.running = false;
    }

    //byte sensorimage gelöscht fürs testen
    public SensorInfo(String sensorname, byte[] sensorimage, String sensorTyp) {
        this.sensorname = sensorname;
        this.sensorimage = sensorimage;
        //this.generatesensorid = generatesensorid;
        this.sensorTyp = sensorTyp;
    }

    //byte sensorimage gelöscht fürs testen
    public SensorInfo(String sensorname, byte[] sensorimage, String sensorPinset, String sensorTyp) {
        this.sensorname = sensorname;
        this.sensorimage = sensorimage;
        this.sensorTyp = sensorTyp;
        this.sensorPinset = sensorPinset;
    }

    //byte sensorimage gelöscht fürs testen
    public SensorInfo(String generatesensorid, String sensorname, byte[] sensorimage) {
        this.sensorname = sensorname;
        this.sensorimage = sensorimage;
        this.generatesensorid = generatesensorid;
       // this.sensorPinset = sensorPinset;
    }

    //byte sensorimage gelöscht fürs testen
    public SensorInfo(String generatesensorid, String sensorname, byte[] sensorimage, String sensorPinset) {
        this.sensorname = sensorname;
        this.sensorimage = sensorimage;
        this.generatesensorid = generatesensorid;
        this.sensorPinset = sensorPinset;
        this.sensorid = sensorid;
    }

    public SensorInfo(String generatesensorid, String sensorname, byte[] sensorimage, String sensorPinset, String sensortype) {
        this.sensorname = sensorname;
        this.sensorimage = sensorimage;
        this.generatesensorid = generatesensorid;
        this.sensorPinset = sensorPinset;
        this.sensorTyp = sensortype;
    }

    //byte sensorimage gelöscht fürs testen
    public SensorInfo(String generatesensorid, String sensorname) {
        this.sensorname = sensorname;
        this.generatesensorid = generatesensorid;

    }

    public String getGeneratesensorid() {
        return generatesensorid;
    }

    public void setGeneratesensorid(String generatesensorid) {
        this.generatesensorid = generatesensorid;
    }

    public int getSensorCount() {
        return sensorCount;
    }

    public void setSensorCount(int sensorCount) {
        this.sensorCount = sensorCount;
    }

    public void setSensorTyp(String sensorTyp) {
        this.sensorTyp = sensorTyp;
    }

    public void setSensorPinset(String sensorPinset) {
        this.sensorPinset = sensorPinset;
    }

    public String getSensorTyp() {
        return sensorTyp;
    }

    public String getSensorPinset() {
        return sensorPinset;
    }

    public int getId() {
        return sensorid;
    }

    public void setId(int id) {
        this.sensorid = id;
    }

    public String getName() {
        return sensorname;
    }

    public void setName(String name) {
        this.sensorname = name;
    }

    public byte[] getImage() {
        return sensorimage;
    }

    public void setImage(byte[] image) {
        this.sensorimage = image;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public boolean isDeployed() {
        return deployed;
    }

    public void setDeployed(boolean deployed) {
        this.deployed = deployed;
    }
}


