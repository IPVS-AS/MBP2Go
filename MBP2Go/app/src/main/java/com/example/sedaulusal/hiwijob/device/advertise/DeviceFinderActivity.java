package com.example.sedaulusal.hiwijob.device.advertise;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sedaulusal.hiwijob.R;
import com.example.sedaulusal.hiwijob.device.ActuatorInfo;
import com.example.sedaulusal.hiwijob.device.DeviceInfo;
import com.example.sedaulusal.hiwijob.device.DeviceOverviewActivity;
import com.example.sedaulusal.hiwijob.device.DeviceRegistryActivity;
import com.example.sedaulusal.hiwijob.device.RecyclerTouchListener;
import com.example.sedaulusal.hiwijob.device.SQLiteHelper;
import com.example.sedaulusal.hiwijob.device.SensorAdapter;
import com.example.sedaulusal.hiwijob.device.SensorInfo;
import com.example.sedaulusal.hiwijob.ruleEngine.RuleEngineActivity;
import com.example.sedaulusal.hiwijob.ruleEngine.SelectThingActivity;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.NetworkInterface;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.sedaulusal.hiwijob.SettingActivity.mypreference;
import static com.example.sedaulusal.hiwijob.device.advertise.RMPHelper.readJSONFile;

//implements SensorEventListener
public class DeviceFinderActivity extends AppCompatActivity {
    private static final String TAG = "ConndeMain";
    private SensorManager mSensorManager;
    Sensor sensor;
    private AdvertiserService advertiserService;
    Adapter sensorAdapterDeviceFinder;
    ArrayList<SensorInfo> sensorNameList = new ArrayList<>();
    SharedPreferences sharedPreferences;
    String url;
    long deviceid, sensorid, actuatorid;


    String pid = "";
    DeviceInfo deviceInfo;
    public static SQLiteHelper sqLiteHelper;
    String post;


    Context context;
    Cursor cursor;

    RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;


    Button btn_registrymanually, btn_registerSensorDeviceFinder;
    String address, ip;
    String devicename;
    ServiceConnection advertiseConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

            advertiserService = ((AdvertiserService.AdvertiseBinder) iBinder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            advertiserService = null;
            Log.i(TAG, "Disconnected from AdvertiserService");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_finder);

          /*
        SharedPreferences to get the url from setting
         */
        sharedPreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        url = sharedPreferences.getString("URL", "");


        context = this;
        sqLiteHelper = new SQLiteHelper(this);


        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * 0.8), (int) (height * 0.6));


        btn_registrymanually = (Button) findViewById(R.id.btn_activitydevicefinder_registrymanually);
        btn_registerSensorDeviceFinder = (Button) findViewById(R.id.btn_activitydevicefinder_registrysensor);

        //showSensorOverview();
        try {
            ensureAutodeployConf();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        recyclerView = (RecyclerView) findViewById(R.id.devicefinder_recyclerview_sensoren);

        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);


        showDeployConf();


    }


    public void btn_registrymanuallyClick(View v) {
        startActivity(new Intent(this, DeviceRegistryActivity.class));
    }

    public void btn_registerSensorDeviceFinder(View v) throws JSONException {
        //registerSmartphone();

        regi();
        startActivity(new Intent(this, DeviceOverviewActivity.class));

        /*for(SensorInfo sensorInfo: sensorNameList){
            sensorInfo.isDeployed();
        }


        for(final SensorInfo sensorInfo:sensorNameList){
            final String urlSensor = url+"/api/deploy/sensor/" + sensorInfo.getGeneratesensorid();

            if(sensorInfo.deployed){
                Toast.makeText(getApplicationContext(), "Test"+ sensorInfo.getName(), Toast.LENGTH_SHORT).show();

                RequestQueue queue1 = Volley.newRequestQueue(this); // this = context
                JSONObject params_sensor = new JSONObject();
                params_sensor.put("name", sensorInfo.getName());
                //params_sensor.put("type", typesurl + sensori.getSensorTyp());
                //params_sensor.put("device", deviceurl + deviceInfo.getPlattformid());
                JsonObjectRequest jsonObjReqPOST = new JsonObjectRequest(Request.Method.POST,
                        urlSensor, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("ResponseAdapterPOST", response.toString());
                        System.out.println("ResponseAdapterPost" + response.toString());

                        if (response.toString().contains("201") || response.toString().contains("true") ) {
                            sensorInfo.deployed = true;
                            sensorAdapterDeviceFinder.notifyDataSetChanged();

                        } else {
                            sensorInfo.deployed = false;
                            sensorAdapterDeviceFinder.notifyDataSetChanged();
                        }

                    }

                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                //sensor.deployed = false;
                                if(error.toString().contains("TimeoutError")){
                                   // activity.updateSensorsDeploy();
                                    //sensor.deployed = true;
                                   // notifyDataSetChanged();
                                }else{
                                    sensorInfo.deployed = false;
                                    sensorAdapterDeviceFinder.notifyDataSetChanged();
                                }
                                //notifyDataSetChanged();
                            }
                        }
                );

                queue1.add(jsonObjReqPOST);
            }
        }*/

    }


    public void showDeployConf() {
        //final ListView lblSensorlist = (ListView) findViewById(R.id.lblSensorlist);
        // lblSensorlist.setText("test");

        String appendText = "\n\n";
        File filesDir = getApplicationContext().getFilesDir();
        File autodeployFile = new File(filesDir, Const.AUTODEPLOY_FILE);
        JSONObject deployConf = readJSONFile(autodeployFile);

        if (deployConf != null) {
            try {
                appendText += deployConf.toString(4);
                System.out.println(deployConf.toString(4));

                JSONObject obj = new JSONObject(appendText);
                // {"self":{"local_id":"falcon_umts","type":"XT1032","adapter_conf":{"timeout":15}},

                JSONObject sys = obj.getJSONObject("self");
                String country = sys.getString("local_id");
                devicename = sys.getString("type");

                //DeviceInfo deviceInfo = new DeviceInfo(country1);

                WifiManager manager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo info = manager.getConnectionInfo();
                // = info.getMacAddress();


                //WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
                ip = Formatter.formatIpAddress(manager.getConnectionInfo().getIpAddress());
                //int imac = Integer.parseInt(manager.getConnectionInfo().getMacAddress());
                address = getMacAddr();


                Log.d("Response ", obj.toString());
                JSONArray jsonArray = obj.getJSONArray("devices");
                Log.d("Response Array", jsonArray.toString());


                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject explrObject = jsonArray.getJSONObject(i);
                    String sensorname = explrObject.getString("local_id");
                    String type = explrObject.getString("type");
                    //String plattformid = explrObject.getString("id");
                    //String username = explrObject.getString("username");
                    byte[] image;

                    Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    image = byteArray;

                    SensorInfo sensorInfo = new SensorInfo(sensorname, byteArray, type);
                    sensorNameList.add(sensorInfo);
                    // SensorInfo sensorInfo = new SensorInfo(name1, );


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        /*File globalIdFile = new File(filesDir, Const.GLOBAL_ID_FILE);
        JSONObject globalIds = readJSONFile(globalIdFile);

        if (globalIds != null) {
            try {
                appendText += "\n-------------------------------------------------\n";
                appendText += globalIds.toString(4);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }*/

            // lblSensorlist.append(name);

            //sensorAdapterDeviceFinder = new SensorAdapterDeviceFinder(this, R.layout.sensor_items_devicefinder, sensorNameList);
            //sensorAdapterDeviceFinder = new DeviceFinderSensorAdapter(sensorNameList, cursor, context);

            sensorAdapterDeviceFinder = new Adapter(sensorNameList);
            //sensorAdapterDeviceFinder.loadItems(sensorNameList);

            // Assign adapter to ListView
            // lblSensorlist.setAdapter(sensorAdapterDeviceFinder);

            recyclerView.setAdapter(sensorAdapterDeviceFinder);
        }

        /*recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
                                                @Override
                                                public void onClick(View view, int position) {
                                                    //position_stelle = position;

                                                    Toast.makeText(getApplicationContext(), "hi", Toast.LENGTH_SHORT).show();
                                                    //Fehlerbehebung, sonst Array -1 Error
                                                    if (position <= -1){
                                                        //dialog.dismiss();
                                                        return;
                                                    }


                                                }

                                                @Override
                                                public void onLongClick(View view, int position) {

                                                }
                                            }));


        // ListView Item Click Listener
           /* lblSensorlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @SuppressLint("ResourceType")
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    // ListView Clicked item index
                    int itemPosition     = position;
                    parent.getItemAtPosition(position);
                    String test = view.getResources().getText(2).toString();
                    Toast.makeText(getApplicationContext(), "hi"+test, Toast.LENGTH_SHORT).show();

                    // ListView Clicked item value
                  //  String  itemValue    = (String) lblSensorlist.getItemAtPosition(position);



                }

            });
        }*/

    }


    @Override
    protected void onResume() {
        super.onResume();
        bindService(new Intent(this, AdvertiserService.class), advertiseConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindFromService();
    }

    private void unbindFromService() {
        if (advertiserService != null) {
            unbindService(advertiseConnection);
            advertiserService = null;
        }
    }


    private void ensureAutodeployConf() throws JSONException {
        File filesDir = getApplicationContext().getFilesDir();

        File autodeployFile = new File(filesDir, Const.AUTODEPLOY_FILE);
        if (!autodeployFile.exists()) {
            Log.i(TAG, "Generating autodeploy file");
            mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);

            JSONObject autodeployConf = new JSONObject();

            JSONObject host = new JSONObject();
            host.put(Const.LOCAL_ID, Build.DEVICE);
            host.put(Const.TYPE, Build.MODEL);

            JSONObject hostAdapterConf = new JSONObject();
            hostAdapterConf.put(Const.TIMEOUT, 15); // default 30 seconds
            host.put(Const.ADAPTER_CONF, hostAdapterConf);

            autodeployConf.put(Const.DEPLOY_SELF, host);

            JSONArray deployDevices = new JSONArray();
            for (Sensor sensor : sensors) {
                JSONObject jsonSensor = new JSONObject();
                jsonSensor.put(Const.LOCAL_ID, sensor.getName());
                // jsonSensor.put(Const.TYPE, RMPHelper.getStringType(sensor.getType()));
                jsonSensor.put(Const.TYPE, sensor.getType());

                JSONObject adapterConf = new JSONObject();
                adapterConf.put(Const.TIMEOUT, 30); // default 30 seconds
                jsonSensor.put(Const.ADAPTER_CONF, adapterConf);
                deployDevices.put(jsonSensor);
            }

            autodeployConf.put(Const.DEPLOY_DEVICES, deployDevices);

            String jsonString = autodeployConf.toString(2);
            try (OutputStream os = new FileOutputStream(autodeployFile)) {
                os.write(jsonString.getBytes(Charset.forName("UTF-8")));
                os.flush();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.i(TAG, "Successfully generated autodeploy file |\n" + autodeployConf.toString(4) + "\n|");
        } else {
            Log.w(TAG, "Autodeploy file exists");
        }
    }


    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(Integer.toHexString(b & 0xFF) + ":");
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
            //handle exception
        }
        return "";
    }


    public void regi() throws JSONException {

        deviceInfo = isTheSmartphoneRegister();

        if (deviceInfo == null) {


            JSONObject params = new JSONObject();


            //String url = "http://192.168.209.189:8080/MBP/api/devices/";
            String urlDevices = url + "/api/devices/";
            final RequestQueue queue = Volley.newRequestQueue(this); // this = context
            // JSONObject params = new JSONObject();
            params.put("name", devicename);
            params.put("macAddress", address.replace(":", ""));
            params.put("ipAddress", ip);
            params.put("formattedMacAddress", address);

            params.put("username", "admin");
            params.put("password", "admin");
            params.put("componentType", "Smartphone");


            final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    urlDevices, params,
                    new Response.Listener() {
                        @Override
                        public void onResponse(Object response) {

                        }

                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            //Failure Callback

                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> authentification = getHeaderforAuthentification();
                    return authentification;
                }

                @Override
                protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                    try {
                        // Sensoren id bekommen im Head
                        JSONObject result = null;

                        // String jsonStringResponse = new String(response.data, "UTF-8");
                        String jsonString2 = new String(response.data, "UTF-8");
                        //String post = response.allHeaders.get(2).toString();
                        String location = response.headers.get("Location");
                        post = location.substring(location.lastIndexOf("/") + 1);

                        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.baseline_smartphone_black_36);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] byteArray = stream.toByteArray();

                        deviceInfo = new DeviceInfo(devicename, address, pid, byteArray, "Smartphone", ip, "admin", "admin");
                        deviceid = sqLiteHelper.createDevice(deviceInfo);
                        pid = post;

                        for (final SensorInfo sensorInfo : sensorNameList) {

                            registerSensors(sensorInfo);
                        }


                        ////////////

                        return Response.success(new JSONObject(jsonString2),
                                HttpHeaderParser.parseCacheHeaders(response));
                    } catch (UnsupportedEncodingException e) {
                        return Response.error(new ParseError(e));
                    } catch (JSONException e) {
                        return Response.error(new ParseError(e));
                    }
                }

            };

            // Adding the request to the queue along with a unique string tag
            queue.add(jsonObjReq);


            //testliste.removeAll(sensorlist);



                             /*
                    get request to get the id
                    */
                             /*
            JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, urlDevices, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // display response
                            Log.d("Response", response.toString());
                            JSONObject obj = null;

                            try {
                                JSONObject mainObject = response;

                                obj = response.getJSONObject("_embedded");

                                Log.d("Response ", obj.toString());
                                JSONArray jsonArray = obj.getJSONArray("devices");
                                Log.d("Response Array", jsonArray.toString());
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject explrObject = jsonArray.getJSONObject(i);
                                    String namejson = explrObject.getString("name");
                                    String macidjson = explrObject.getString("macAddress");
                                    String plattformid = explrObject.getString("id");
                                    Log.d("Plattformid", "id:" + plattformid);
                                    pid = plattformid;
                                }

                                Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.baseline_smartphone_black_36);
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                byte[] byteArray = stream.toByteArray();


                                deviceInfo = new DeviceInfo(devicename, address, pid, byteArray);
                                //deviceid = sqLiteHelper.createDevice(deviceInfo);

                                final String[] text = {""};


                                for (final SensorInfo sensorInfo : sensorNameList) {

                                    registerSensors(sensorInfo);
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("Error.Response", "error");
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> authentification = getHeaderforAuthentification();
                    return authentification;
                }
            };

            queue.add(getRequest);*/


            //ToDO isinserted dont catch bugs anymore
        } else {
            for (final SensorInfo sensorInfo : sensorNameList) {

                ArrayList<SensorInfo> sensorInfosAll = sqLiteHelper.getAllSensor();
                for (SensorInfo sen : sensorInfosAll) {
                    if (sensorInfo.isDeployed() && sensorInfo.getName().equals(sen.getName())) {

                    } else {
                        registerSensors(sensorInfo);
                    }

                }
            }
        }
    }

    public void registerSensors(SensorInfo sensorInfo) throws JSONException {
        if (sensorInfo.isDeployed()) {

            RequestQueue queue2 = Volley.newRequestQueue(context); // this = context
            // String urlSensors = "http://192.168.209.189:8080/MBP/api/sensors/";
            // String typesurl = "http://192.168.209.189:8080/MBP/api/types/";
            // String deviceurl = "http://192.168.209.189:8080/MBP/api/devices/";
            String urlSensors = url + "/api/sensors/";
            String typesurl = url + "/api/adapters/";
            String deviceurl = url + "/api/devices/";
            JSONObject params_sensor = new JSONObject();
            params_sensor.put("name", sensorInfo.getName());
            //params_sensor.put("adapter", typesurl + sensorInfo.getSensorTyp());
            params_sensor.put("adapter", typesurl + "5e313a474edc8d01d2081ed3");

            params_sensor.put("device", deviceurl + pid);
            //params_sensor.put("device", deviceurl + deviceInfo.getPlattformid());
            //sensorInfo.setSensoradapter(componenttypespinner.getSelectedItem().toString());
            //TODO
            params_sensor.put("componentType", "Temperature");


            final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    urlSensors, params_sensor,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // response
                            //Log.d("Response Sensor show ", response.toString());
                           // posttrue = true;
                            //post = response.toString();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                            System.out.print("ERROR addind Sensor" + error.getMessage());
                            // Log.d("Error.Response", response);
                        }
                    }
            ) {
                //
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> authentification = getHeaderforAuthentification();
                    return authentification;

                }


                @Override
                protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                    try {
                        // Sensoren id bekommen im Head
                        String jsonString2 = new String(response.data, "UTF-8");
                        //String post = response.allHeaders.get(2).toString();
                        String location = response.headers.get("Location");
                        post = location.substring(location.lastIndexOf("/") + 1);
                        SensorInfo sensorInf = new SensorInfo(post, sensorInfo.getName(), sensorInfo.getImage(), sensorInfo.getSensorPinset(), sensorInfo.getSensorTyp(), sensorInfo.getSensoradapter());
                        sensorid = sqLiteHelper.createSensor(sensorInf, deviceInfo);

                        return Response.success(new JSONObject(jsonString2),
                                HttpHeaderParser.parseCacheHeaders(response));
                    } catch (UnsupportedEncodingException e) {
                        return Response.error(new ParseError(e));
                    } catch (JSONException je) {
                        return Response.error(new ParseError(je));
                    }
                }
            };

            queue2.add(jsonObjReq);


        }
    }

    /*
    This method test is the smartphone regisert
     */
    public DeviceInfo isTheSmartphoneRegister() {
        ArrayList<DeviceInfo> deviceInfoAll = sqLiteHelper.getAllDevice();
        for (DeviceInfo deviceInfo1 : deviceInfoAll) {
            if (deviceInfo1.getMacid().equals(address)) {
                deviceInfo = deviceInfo1;
            } else {
                deviceInfo = null;
            }
        }
        return deviceInfo;
    }

    public Map<String, String> getHeaderforAuthentification() {
        SharedPreferences sp1 = context.getSharedPreferences("Login", 0);
        String usernameSharedpref = sp1.getString("Username", null);
        String passwordSharedpref = sp1.getString("Password", null);
        String auth = new String(usernameSharedpref + ":" + passwordSharedpref);

        byte[] data = auth.getBytes();
        String base64 = Base64.encodeToString(data, Base64.NO_WRAP);
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Basic " + base64);
        //headers.put("accept-language","EN");
        headers.put("Content-Type", "application/json");
        //headers.put("Accept","application/json");
        return headers;
    }

}
