package com.example.sedaulusal.hiwijob.device.advertise;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by rosso on 19.08.17.
 */

public class AdvertiseService {

  private Map<String, AdvertiseDevice> devices; // TODO include host in devices?

  private JSONObject autodeploy_data;
  private AdvertiseDevice host;
  private double min_timeout; // timeout in seconds
  private AtomicBoolean keepalive = new AtomicBoolean(true);

  private File filesDir;

  public AdvertiseService(File filesDir) throws JSONException {

    this.filesDir = filesDir;
    devices = new HashMap<>();

    read_autodeploy();
    read_global_ids();

    //log.info("Autodeploy data: " + this.autodeploy_data.toString());

    // calculate smallest timeout
    JSONArray devices = autodeploy_data.getJSONArray(Const.DEPLOY_DEVICES);
    min_timeout = Integer.MAX_VALUE;
    for (int i = 0; i < devices.length(); i++) {
      JSONObject device = devices.getJSONObject(i);
      int timeout = device.getJSONObject(Const.ADAPTER_CONF).getInt(Const.TIMEOUT);
      min_timeout = Math.min(min_timeout, timeout);
    }
    min_timeout = (min_timeout / 2) * 1000;
  }

  private void read_global_ids() throws JSONException {
    File globalIdFile = new File(filesDir, Const.GLOBAL_ID_FILE);
    JSONObject readObject = null;
    if (globalIdFile.exists()) {
     // log.info("Found GLOBAL_ID file. Reading for reconnection...");
      try (InputStream is = new FileInputStream(globalIdFile)) {
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        String json = new String(buffer, "UTF-8");
        readObject = new JSONObject(json);
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
     // log.info("Could not find GLOBAL_ID file.");
      readObject = new JSONObject();
    }

    Iterator<String> keys = readObject.keys();
    while (keys.hasNext()) {
      String localId = keys.next();
      int globalId = readObject.getInt(localId);

      setGlobalId(localId, globalId);
    }
  }

  private void read_autodeploy() throws JSONException {
    File autodeployFile = new File(filesDir, Const.AUTODEPLOY_FILE);
    JSONObject readObject = null;
    if (autodeployFile.exists()) {
      try (InputStream is = new FileInputStream(autodeployFile)) {
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        String json = new String(buffer, "UTF-8");
        readObject = new JSONObject(json);
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    } else {
      readObject = new JSONObject();
    }

    autodeploy_data = readObject;


    if (autodeploy_data.has(Const.DEPLOY_SELF)) {
     // this.host = DeviceHelper.deviceFromJSON(this.autodeploy_data.getJSONObject(Const.DEPLOY_SELF));
    } else {
      this.host = null;
    }

    if (autodeploy_data.has(Const.DEPLOY_DEVICES)) {
      JSONArray deviceArray = autodeploy_data.getJSONArray(Const.DEPLOY_DEVICES);
      for (int i = 0; i < deviceArray.length(); i++) {
        JSONObject jsonDevice = deviceArray.getJSONObject(i);
       // AdvertiseDevice device = DeviceHelper.deviceFromJSON(jsonDevice);
       // this.devices.put(device.getLocalId(), device);
      }
    }
  }






  void setGlobalId(String deviceName, int globalId) {
    AdvertiseDevice device = devices.get(deviceName);
    if (device != null) {
      device.setGlobalId(globalId);
    } else if (host != null && host.getLocalId().equals(deviceName)) {
      host.setGlobalId(globalId);
    }
  }




  Map<String, AdvertiseDevice> getAllDevices() {
    if (host != null) {
      Map<String, AdvertiseDevice> allDevices = new HashMap<>(devices);
      allDevices.put(host.getLocalId(), host);
      return allDevices;
    } else {
      return devices;
    }

  }
}
