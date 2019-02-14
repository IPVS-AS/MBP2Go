package com.example.sedaulusal.hiwijob.historydiagramm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sedaulusal.hiwijob.MainActivity;
import com.example.sedaulusal.hiwijob.R;
import com.example.sedaulusal.hiwijob.device.DeviceInfo;
import com.example.sedaulusal.hiwijob.device.SQLiteHelper;
import com.example.sedaulusal.hiwijob.device.SensorInfo;
import com.example.sedaulusal.hiwijob.diagramm.DiagrammActivity;
import com.example.sedaulusal.hiwijob.diagramm.DynamicFragmentAdapter;
import com.google.gson.JsonObject;

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
import java.util.ArrayList;

import static com.example.sedaulusal.hiwijob.SettingActivity.mypreference;

public class HistoryDiagrammActivity extends AppCompatActivity {
    int _count = 0;
    ArrayList<DeviceInfo> devicelist;
    boolean booleanwert;

    SharedPreferences sharedPreferences;
    String url;

    ArrayList<Double> sensorValues, sensorValueList;


    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    String value = "0";
    public HistoryDynamicFragmentAdapter mDynamicFragmentAdapter;

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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_diagramm);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        context =this;


        sharedPreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        url = sharedPreferences.getString("URL", "");

        fragmentManager = getSupportFragmentManager();//Get Fragment Manager

        sqLiteHelper = new SQLiteHelper(context);
        devicelist = sqLiteHelper.getAllDevice();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar_historydiagramm);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle( devicelist.get(0).getName().toString());
        lastWord = devicelist.get(0).getPlattformid().toString();


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HistoryDiagrammActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


        try {
            initViews();

        } catch (Exception e) {
            e.printStackTrace();
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
           // menu.add(0,MENU_ADD,Menu.NONE, deviceInfo.getName()+ " "+ deviceInfo.getPlattformid());
            MenuItem mi = menu.add(0,MENU_ADD,Menu.NONE, deviceInfo.getName());

            mi.setContentDescription(deviceInfo.getPlattformid());
        }

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
                item.getTitle();
                item.getItemId();
                getSupportActionBar().setTitle(item.getTitle().toString());
                //TODO: title : id dazugeben?
                System.out.println(  item.getTitle()+ " Id "+ item.getItemId());
                String testString = item.getTitle().toString();
                String[] parts = testString.split(" ");
                //lastWord = parts[parts.length - 1];
                System.out.println(lastWord);
                lastWord = item.getContentDescription().toString();
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
                String serverURI = "tcp://192.168.209.189:1883";
                String topic = sensorInfo.getGeneratesensorid();

                try {
                    //connectToMqtt(serverURI, topic);


                    //getValuesfromSensor(topic);

                    ////////////////////////////




                    ////////////////////









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
       //  String topic = sensorlist.get(mTabLayout.getSelectedTabPosition()).getGeneratesensorid();
        //getValuesfromSensor(topic);
        //  connectToMqtt(serverURI,topic);
        mDynamicFragmentAdapter = new HistoryDynamicFragmentAdapter(getSupportFragmentManager(), mTabLayout.getTabCount());
        viewPager.setAdapter(mDynamicFragmentAdapter);
        viewPager.setCurrentItem(0);

    }


}





