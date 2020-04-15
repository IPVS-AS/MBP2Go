package com.example.sedaulusal.hiwijob.diagramm;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.Trace;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sedaulusal.hiwijob.MainActivity;
import com.example.sedaulusal.hiwijob.R;
import com.example.sedaulusal.hiwijob.device.DeviceInfo;
import com.example.sedaulusal.hiwijob.device.DeviceOverviewActivity;
import com.example.sedaulusal.hiwijob.device.SQLiteHelper;
import com.example.sedaulusal.hiwijob.device.SensorInfo;
import com.example.sedaulusal.hiwijob.device.advertise.SensorAdapterDeviceFinder;
import com.highsoft.highcharts.Common.HIChartsClasses.HIOptions;
import com.highsoft.highcharts.Common.HIChartsClasses.HISpline;
import com.highsoft.highcharts.Common.HIChartsClasses.HIXAxis;
import com.highsoft.highcharts.Core.HIChartView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

import static com.example.sedaulusal.hiwijob.SettingActivity.mypreference;

public class DiagrammActivity extends AppCompatActivity {
    int _count = 0;
    ArrayList<DeviceInfo> devicelist;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    String value = "0";
    DynamicFragmentAdapter mDynamicFragmentAdapter;

    public ArrayList<SensorInfo> getSensorlist() {
        return sensorlist;
    }

    ArrayList<SensorInfo> sensorlist;
    private ViewPager viewPager;
     TabLayout mTabLayout;

    SQLiteHelper sqLiteHelper;
    Context context;

    String lastWord, sensorId;

    private static final int MENU_ADD = Menu.FIRST;
    private static final int MENU_LIST = Menu.FIRST + 1;
    private static final int MENU_REFRESH = Menu.FIRST + 2;
    private static final int MENU_LOGIN = Menu.FIRST + 3;

    private static FragmentManager fragmentManager;
    double number;

    SharedPreferences sharedPreferences;
    String url;
    boolean stop = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagramm);

        context =this;

        sharedPreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        url = sharedPreferences.getString("URL", "");

        fragmentManager = getSupportFragmentManager();//Get Fragment Manager

        sqLiteHelper = new SQLiteHelper(context);
        devicelist = sqLiteHelper.getAllDevice();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar_diagramm);
        setSupportActionBar(myToolbar);
        //TODO devicelist null
        getSupportActionBar().setTitle( devicelist.get(0).getName().toString());
        lastWord = devicelist.get(0).getPlattformid().toString();


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DiagrammActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


        try {
            initViews();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void connectToMqtt(String serverURI, final String topic ) throws Exception{
        String clientId = MqttClient.generateClientId();
        final MqttAndroidClient client=
                new MqttAndroidClient(this.getApplicationContext(), serverURI,
                        clientId);

        MqttConnectOptions options = new MqttConnectOptions();
        options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
        options.setUserName("admin");
        options.setPassword("admin".toCharArray());

        try {
            IMqttToken token = client.connect(options);
        } catch (MqttException e) {
            e.printStackTrace();
            //Toast.makeText(getApplicationContext(), "Error connect to Mqtt", Toast.LENGTH_SHORT).show();
            //Toast toast = Toast.makeText(getApplicationContext(), "Error connect to Mqtt", Toast.LENGTH_SHORT);
            //toast.show();
        }


        try {
            IMqttToken token = client.connect();
            if(token!=null){}
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d("MQtt", "onSuccess");
                   // Toast.makeText(getApplicationContext(), "Please wait,mqtt", Toast.LENGTH_SHORT).show();
                  //  Toast toast = Toast.makeText(getApplicationContext(), "Error connect to Mqtt", Toast.LENGTH_SHORT);
                   // toast.show();
                    //pubMqttChannel(client);
                    subscribeMqttChannel(client,topic);
                    //subscribe(client);

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                     Log.d("TEEST", "onFailure");
                   // Toast.makeText(getApplicationContext(), "Please wait,mqtt fail"+ exception, Toast.LENGTH_SHORT).show();
                  //  Toast toast = Toast.makeText(getApplicationContext(), "Error connect to Mqtt", Toast.LENGTH_SHORT);
                   // toast.show();
                    //Intent intent = new Intent(DiagrammActivity.this, DiagrammActivity.class);
                    //startActivity(intent);
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
            Log.e("TEEST", "onFailure");

            // Toast.makeText(getApplicationContext(), "Error connect to Mqtt", Toast.LENGTH_SHORT).show();
            //Toast toast = Toast.makeText(getApplicationContext(), "Error connect to Mqtt", Toast.LENGTH_SHORT);
            //toast.show();
            //Intent intent = new Intent(DiagrammActivity.this, DiagrammActivity.class);
            //startActivity(intent);

        }
    }

    public void subscribeMqttChannel(MqttAndroidClient client, String channelName) {
        try {
            Log.d("tag","mqtt channel name>>>>>>>>" + channelName);
            Log.d("tag","client.isConnected()>>>>>>>>" + client.isConnected());
            if (client.isConnected()) {
                client.subscribe(channelName, 0);
                client.setCallback(new MqttCallback() {
                    @Override
                    public void connectionLost(Throwable cause) {
                        value="0";
                    }

                    @Override
                    public void messageArrived(String topic, MqttMessage message) throws Exception {
                        Log.d("tag","message>>" + new String(message.getPayload()));
                        Log.d("tag","topic>>" + topic);
                       // Toast.makeText(getApplicationContext(), "Please wait,mqtt playload" + message.getPayload() , Toast.LENGTH_SHORT).show();

                        //Toast toast = Toast.makeText(getApplicationContext(), "Error connect to Mqtt", Toast.LENGTH_SHORT);
                        //toast.show();

                        String payload = message.toString();
                        //parseMqttMessage(new String(message.getPayload()));
                        parsePayload(payload);



                    }

                    @Override
                    public void deliveryComplete(IMqttDeliveryToken token) {

                    }
                });
            }
        } catch (Exception e) {
            Log.d("tag","Error :" + e);

        }
    }

    public void parsePayload(String payload) throws JSONException {
        JSONObject mainObject = new JSONObject(String.valueOf(payload));
         value = mainObject.get("value").toString();
       // setValue("mainObject.get(\"value\").toString()");
        String id = mainObject.get("id").toString();
        String component = mainObject.get("component").toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu, this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_diagramm, menu);
        return true;
    }

    /**
     * Gets called every time the user presses the menu button.
     * Use if your menu is dynamic.
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
       // if(enableAdd)
        //lastWord = devicelist.get(0).getPlattformid().toString();

        for(DeviceInfo deviceInfo: devicelist){
           // MenuItem mi = menu.add(0,MENU_ADD,Menu.NONE, deviceInfo.getName()+ " "+ deviceInfo.getPlattformid());
           // MenuItem mi = menu.add(0,MENU_ADD,Menu.NONE, deviceInfo.getName());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                MenuItem mi = menu.add(0,MENU_ADD,Menu.NONE, deviceInfo.getName());
                mi.setContentDescription(deviceInfo.getPlattformid());
            }else{
                MenuItem mi = menu.add(0,MENU_ADD,Menu.NONE, deviceInfo.getName()+ " "+ deviceInfo.getPlattformid());
            }


            //menu.getItem().setContentDescription();
            //menu.add(0,MENU_ADD,Menu.NONE, deviceInfo.getName());
            }
           // menu.add(0, MENU_ADD, Menu.NONE, R.string.your-add-text).setIcon(R.drawable.your-add-icon);
        /*if(enableList)
            menu.add(0, MENU_LIST, Menu.NONE, R.string.your-list-text).setIcon(R.drawable.your-list-icon);
        if(enableRefresh)
            menu.add(0, MENU_REFRESH, Menu.NONE, R.string.your-refresh-text).setIcon(R.drawable.your-refresh-icon);
        if(enableLogin)
            menu.add(0, MENU_LOGIN, Menu.NONE, R.string.your-login-text).setIcon(R.drawable.your-login-icon);*/
        return super.onPrepareOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // ...

       /* View menuItemView = findViewById(R.id.menu_diagramm_device1); // SAME ID AS MENU ID
        PopupMenu popupMenu = new PopupMenu(this, menuItemView);
        popupMenu.inflate(R.menu.menu_diagramm);
        // ...
        popupMenu.show();*/
        // ...

        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case MENU_ADD:
                //doAddStuff();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    item.getContentDescription();
                }
                item.getTitle();
                item.getItemId();
                getSupportActionBar().setTitle(item.getTitle().toString());
                //TODO: title : id dazugeben?
                System.out.println(  item.getTitle()+ " Id "+ item.getItemId());
                String testString = item.getTitle().toString();
                String[] parts = testString.split(" ");
                //lastWord = parts[parts.length - 1];
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    lastWord = item.getContentDescription().toString();
                }else{
                    lastWord = parts[parts.length - 1];
                }

                System.out.println(lastWord);
                try {
                    initViews();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // DynamicFragment dynamicFragment = new DynamicFragment();
               // for(int i = 0; i>= 10 ; i++) {
                 //   dynamicFragment.chartHigh(i);
                //}

                break;
            case MENU_LIST:
               // doListStuff();
                break;
            case MENU_REFRESH:
                //doRefreshStuff();
                break;
            case MENU_LOGIN:
               // doLoginStuff();
                break;
        }
        return false;

       // return true;
    }





    private void initViews() throws Exception{

        viewPager = findViewById(R.id.viewpager);
        mTabLayout =  findViewById(R.id.tabs);
        viewPager.setOffscreenPageLimit(5);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        setDynamicFragmentToTabLayout();


        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                value="0";
                mDynamicFragmentAdapter.notifyDataSetChanged();
                viewPager.setCurrentItem(tab.getPosition());
                SensorInfo sensorInfo = sensorlist.get(tab.getPosition());
                //192.168.209.194:1883

                try {
                    String serverURI = "tcp://"+parseurl();
                    String topic = "sensor/"+sensorInfo.getGeneratesensorid();
                    //connectToMqtt(serverURI, topic);


                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            int i = 0;
                            while (!stop) {
                                try {
                                    Thread.sleep(9000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                i++;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        getValuewithRest(sensorInfo.getGeneratesensorid());
                                    }
                                });

                            }
                        }
                    }).start();



                    /*new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getValuewithRest(sensorInfo.getGeneratesensorid());

                        }
                    }, 800);*/

                    //getValuewithRest(sensorInfo.getGeneratesensorid());
                }catch (Exception e){
                   // e.getMessage();
                   // Toast.makeText(getApplicationContext(), "Error connect to Mqtt", Toast.LENGTH_SHORT).show();
                    //Toast toast = Toast.makeText(getApplicationContext(), "Error connect to Mqtt", Toast.LENGTH_SHORT);
                    //toast.show();
                    //Intent intent = new Intent(DiagrammActivity.this, DeviceOverviewActivity.class);
                    //startActivity(intent);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }



    private void setDynamicFragmentToTabLayout() {
       // for (int i = 0; i < 10; i++) {
        sensorlist= sqLiteHelper.getAllSensorsByPlattformId(lastWord);
        mTabLayout.removeAllTabs();
        for (SensorInfo sensorInfo: sensorlist) {
            mTabLayout.addTab(mTabLayout.newTab().setText(sensorInfo.getName()));


        }
       // String serverURI = "tcp://192.168.209.189:1883";
       // String topic = "sensor/"+sensorlist.get(mTabLayout.getSelectedTabPosition()).getGeneratesensorid();
       //  connectToMqtt(serverURI,topic);
        mDynamicFragmentAdapter = new DynamicFragmentAdapter(getSupportFragmentManager(), mTabLayout.getTabCount());
        viewPager.setAdapter(mDynamicFragmentAdapter);
        viewPager.setCurrentItem(0);

    }

    /*
      Parsing the url form sharedpref to a url for the MQTT connection
      TODO: Port is hardcoded
     */
    public String parseurl() throws MalformedURLException {
        String parseurl = "";
        URL aURL = new URL(url);
        parseurl = aURL.getHost()+":1883";


       return parseurl;

    }

    public void getValuewithRest(String sensorId){
       // Random rand = new Random();

        // Generate random integers in range 0 to 999
        //value = String.valueOf(rand.nextInt(100));

        getSensorParam(sensorId);
    }


    public void getSensorParam(String sensorId) {
        String urlSensor = url + "/api/sensors/" + sensorId;
        //final String url = "http://192.168.209.189:8080/MBP/api/types";
        RequestQueue queue = Volley.newRequestQueue(context); // this = context

        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, urlSensor, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("Response", response.toString());
                        JSONObject obj = null;

                        try {
                            JSONObject mainObject = response;

                            JSONObject embeddedObject = mainObject.getJSONObject("_embedded");
                            JSONObject adapterObject = embeddedObject.getJSONObject("adapter");
                            //JSONArray parameterObject = adapterObject.getJSONArray("parameters");
                            String unit = adapterObject.getString("unit");
                            unit.length();
                            getValuesfromSensor(sensorId, unit);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            //  dialog.dismiss();

                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error.Response", "errorrrrr"+ error);
                        // dialog.dismiss();

                    }
                }) {
            //
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> authentification = getHeaderforAuthentification();
                return authentification;

            }

        };

        queue.add(getRequest);

    }


    public void getValuesfromSensor(String sensorId, String unit) {
        ArrayList<String> sensorValueList = new ArrayList<>();
        String url_forsensorvalues;
        //String url_forsensorvalues = historyDiagrammActivity.url + "/api/valueLogs/search/findAllByIdref?idref=" + sensorId + "&page=0&size=10000&sort=date,desc";
        if(unit.equals("null") || unit.equals("") ) {
            //url_forsensorvalues = historyDiagrammActivity.url + "/api/sensors/5c7fd6d3f8ea1203bcf381b9/valueLogs?size=200&sort=time,desc&unit=%C2%B0C";
            url_forsensorvalues = url + "/api/sensors/" + sensorId + "/valueLogs?size=200&sort=time,desc";
        }else{
            //url_forsensorvalues = historyDiagrammActivity.url + "/api/sensors/5c7fd6d3f8ea1203bcf381b9/valueLogs?size=200&sort=time,desc&unit=%C2%B0C";

            url_forsensorvalues = url + "/api/sensors/" + sensorId + "/valueLogs?size=200&sort=time,desc&unit="+ unit;
        }
        //final String url = "http://192.168.209.189:8080/MBP/api/types";
        //http://192.168.209.194:8888/deploy/master/api/sensors/5da88741b1c4d32a862fadf0/valueLogs?size=200&sort=time,desc
        sensorValueList.clear();
        RequestQueue queue = Volley.newRequestQueue(context); // this = context
        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url_forsensorvalues, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("Response", response.toString());

                        try {
                            JSONObject mainObject = response;

                            // obj = response.getJSONObject("content");

                            JSONArray jsonArray = response.getJSONArray("content");
                            Log.d("Response Array", jsonArray.toString());

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject explrObject = jsonArray.getJSONObject(i);
                                String jsonvalue = explrObject.getString("value");

                                JSONObject time = explrObject.getJSONObject("time");
                                String epochsecound = time.getString("epochSecond");

                                long sec = Long.parseLong(epochsecound) * 1000;
                                Date date = new Date(sec);
                                SimpleDateFormat sdf = new SimpleDateFormat("MMMM d,yyyy h:mm a", Locale.ENGLISH);
                                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                                String formattedDate = sdf.format(date);
                                System.out.println(formattedDate);

                                sensorValueList.add(jsonvalue);
                                //sensorTimeValueList.add(formattedDate);

                            }
                            //value = sensorValueList.get(sensorValueList.size() -1);
                            value = sensorValueList.get(0);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            e.getMessage();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error.
                        //TODO
                        error.printStackTrace();
                        error.getMessage();
                        Log.e("Error.Response", "ERROR"+ error);

                    }
                }
        ) {
            //
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> authentification = getHeaderforAuthentification();
                return authentification;

            }

        };


        queue.add(getRequest);
    }

    public Map<String, String> getHeaderforAuthentification() {
        SharedPreferences sp1= context.getSharedPreferences("Login",0);
        String usernameSharedpref=sp1.getString("Username", null);
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

    @Override
    public void onDestroy() {
        stop = true;
        super.onDestroy();
    }
}

