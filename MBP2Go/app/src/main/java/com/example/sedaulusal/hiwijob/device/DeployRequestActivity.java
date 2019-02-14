package com.example.sedaulusal.hiwijob.device;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.sedaulusal.hiwijob.MainActivity;
import com.example.sedaulusal.hiwijob.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import static com.example.sedaulusal.hiwijob.SettingActivity.mypreference;


public class DeployRequestActivity extends AppCompatActivity {

    //TextView  deployteextview;
    //RecyclerView listView, listView2;
    ListView listView, listView2;

    SQLiteHelper sqLiteHelper;
    ArrayList<SensorInfo> listSensor = new ArrayList<SensorInfo>();
    SensorDeployAdapter sensorAdapter;
    ArrayList<ActuatorInfo> listactuator = new ArrayList<ActuatorInfo>();
    ActuatorDeployAdapter actuatorAdapter;
    Context context = this;
    ArrayList<ActuatorInfo> listActuatorTest = new ArrayList<ActuatorInfo>();
    ArrayList<SensorInfo> listSensorTest = new ArrayList<SensorInfo>();

    ToggleButton deployButton;
    SensorInfo sens;
    View view;
    SwipeRefreshLayout mSwipeRefreshLayout;

    SharedPreferences sharedPreferences;
    String url;



    public void doSomething() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deploy_request);

        sqLiteHelper = new SQLiteHelper(this);
  /*
        SharedPreferences to get the url from setting
         */
        sharedPreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        url = sharedPreferences.getString("URL", "");



        DeviceInfo di = sqLiteHelper.getDevice(getIntent().getStringExtra("deviceid"));

        ArrayList<SensorInfo> deviceWatchList = sqLiteHelper.getAllSensorsByPlattformId(di.getPlattformid());
        for (final SensorInfo sensorInfo : deviceWatchList) {
            Log.d("Sensor Watchlist", sensorInfo.getName() + "\n" + sensorInfo.getSensorPinset() + sensorInfo.getGeneratesensorid());
            listSensor.add(sensorInfo);


        }

        ArrayList<ActuatorInfo> actuatorWatchList = sqLiteHelper.getAllActuatorsByDevicePlattform(di.getPlattformid());

        for (ActuatorInfo actuatorInfo : actuatorWatchList) {
            Log.d("Sensor Watchlist", actuatorInfo.getName());
            listactuator.add(actuatorInfo);

        }


        listView = (ListView) findViewById(R.id.deplosy_sensor_list);
        listView2 = (ListView) findViewById(R.id.deplosy_actuator_list);


        //then populate myListItems
        sensorAdapter = new SensorDeployAdapter(this, R.layout.layout_deploy, listSensor);
        actuatorAdapter = new ActuatorDeployAdapter(this, R.layout.layout_deploy_actuator, listactuator);
        sensorAdapter.notifyDataSetChanged();


        listView.setAdapter(sensorAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    final int position, long id) {
                Toast.makeText(getApplicationContext(), "Test", Toast.LENGTH_SHORT).show();

                sens = listSensor.get(position);

            }
        });

        listView2.setAdapter(actuatorAdapter);


        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);

        refresh();
        updateSensorsDeploy();
        updateActuatorsDeploy();

        if (listView.getAdapter() == null || listView2.getAdapter() == null) {
        } else {
            ListUtils.setDynamicHeight(listView);
            ListUtils.setDynamicHeight(listView2);
        }

        //UtilityClass.setListViewHeightBasedOnChildren(listView);
        //UtilityClass.setListViewHeightBasedOnChildren(listView2);
    }


    public void refresh() {
        mSwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        //Log.i(LOG_TAG, "onRefresh called from SwipeRefreshLayout");
                        //Toast.makeText(getApplicationContext(), "Test", Toast.LENGTH_SHORT).show();
                        // This method performs the actual data-refresh operation.
                        //getRequest(url,finalHolder);

                        updateSensorsDeploy();
                        updateActuatorsDeploy();

                    }
                }
        );
    }


    public void updateSensorsDeploy() {
        RequestQueue queue2 = Volley.newRequestQueue(context);
        // this = context
        //Intent intent = getIntent();
        //finish();
        //startActivity(intent);
        for (final SensorInfo sensorInfo : listSensor) {
            listSensorTest.add(sensorInfo);

            //String url = "http://192.168.209.189:8080/MBP/api/deploy/sensor/" + sensorInfo.getGeneratesensorid();
            String urlSensors = url+"/api/deploy/sensor/" + sensorInfo.getGeneratesensorid();

            JSONObject params_sensor = new JSONObject();

            StringRequest jsonObjReq = new StringRequest(Request.Method.GET,
                    urlSensors, new Response.Listener<String>() {
                public DeployRequestActivity context;

                @Override
                public void onResponse(String response) {

                    if (response.contains("true")) {
                        sensorInfo.deployed = true;
                    } else {
                        sensorInfo.deployed = false;
                    }
                    sensorAdapter.notifyDataSetChanged();
                    mSwipeRefreshLayout.setRefreshing(false);


                }
            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                            //Log.d("Error.Response", response);
                            mSwipeRefreshLayout.setRefreshing(false);

                        }
                    }
            );
            queue2.add(jsonObjReq);
            //mSwipeRefreshLayout.setRefreshing(false);
            // The method calls setRefreshing(false) when it's finished.
            /*if (listSensor.size()==listSensorTest.size()) {
                mSwipeRefreshLayout.setRefreshing(false);
            } else if (listSensor.isEmpty() && listactuator.isEmpty()) {
                mSwipeRefreshLayout.setRefreshing(false);

            }*/

        }



    }

    public void updateActuatorsDeploy() {
        RequestQueue queue2 = Volley.newRequestQueue(context);
        for (final ActuatorInfo actuatorInfo : listactuator) {
            listActuatorTest.add(actuatorInfo);
            //String url = "http://192.168.209.189:8080/MBP/api/deploy/actuator/" + actuatorInfo.getGenerateActuatorId();
            String urlActuator = url+"/api/deploy/actuator/" + actuatorInfo.getGenerateActuatorId();

            JSONObject params_sensor = new JSONObject();

            StringRequest jsonObjReq = new StringRequest(Request.Method.GET,
                    urlActuator, new Response.Listener<String>() {
                public DeployRequestActivity context;

                @Override
                public void onResponse(String response) {

                    if (response.contains("true")) {
                        actuatorInfo.deployed = true;
                    } else {
                        actuatorInfo.deployed = false;
                    }
                    actuatorAdapter.notifyDataSetChanged();
                    mSwipeRefreshLayout.setRefreshing(false);


                }
            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                            //Log.d("Error.Response", response);
                            mSwipeRefreshLayout.setRefreshing(false);

                        }
                    }
            );
            queue2.add(jsonObjReq);

            // The method calls setRefreshing(false) when it's finished.

        }


    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(DeployRequestActivity.this, DeviceOverviewActivity.class);
        startActivity(intent);
        //super.onBackPressed();
    }


    public static class ListUtils {
        public static void setDynamicHeight(ListView mListView) {
            ListAdapter mListAdapter = mListView.getAdapter();
            if (mListAdapter == null) {
                // when adapter is null
                //   mListAdapter =
                return;
            }
            int height = 0;
            int desiredWidth = View.MeasureSpec.makeMeasureSpec(mListView.getWidth(), View.MeasureSpec.UNSPECIFIED);
            for (int i = 0; i < mListAdapter.getCount(); i++) {
                View listItem = mListAdapter.getView(i, null, mListView);
                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                height += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = mListView.getLayoutParams();
            if (height == 0) {
                height = 100;
            }
            params.height = height + (mListView.getDividerHeight() * (mListAdapter.getCount() - 1));

            mListView.setLayoutParams(params);
            mListView.requestLayout();
        }
    }


}
