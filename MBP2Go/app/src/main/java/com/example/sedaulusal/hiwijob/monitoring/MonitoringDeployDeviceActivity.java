package com.example.sedaulusal.hiwijob.monitoring;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sedaulusal.hiwijob.R;
import com.example.sedaulusal.hiwijob.device.DeviceInfo;
import com.example.sedaulusal.hiwijob.device.RecyclerTouchListener;
import com.example.sedaulusal.hiwijob.device.SQLiteHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.sedaulusal.hiwijob.SettingActivity.mypreference;

public class MonitoringDeployDeviceActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    ArrayList<MonitoringAdapters> compareMonitoringList, monitoringAdaptersArrayList;
    ArrayList<DeviceInfo> devicelist;


    RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    SQLiteHelper db;
    Context context;
    DeviceInfo deviceInfo;
    Cursor cursor;
    int position_stelle;

    SharedPreferences sharedPreferences;
    String url;
    Button deployRequest;
    MonitoringAdapters monitoringAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring_deploy_device);
        context = this;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                 //       .setAction("Action", null).show();
            }
        });

        sharedPreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        url = sharedPreferences.getString("URL", "");


        db = new SQLiteHelper(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_monitoring_deploy);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);


        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        compareMonitoringList = new ArrayList<>();
        monitoringAdaptersArrayList = new ArrayList<>();
        devicelist = new ArrayList<>();



        // specify an adapter (see also next example)
        mAdapter = new MonitoringDeployAdapter(monitoringAdaptersArrayList, cursor, context);
        mRecyclerView.setAdapter(mAdapter);

        String deviceid, deviceinforid = "";
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {

            } else {
                deviceid = extras.getString("deviceid");
                deviceinforid = extras.getString("deviceinfo");


            }
        } else {

        }

        requestMonitoringAdapter(deviceinforid);


        cursor = db.getAllData();
        devicelist.clear();
        while (cursor.moveToNext()) {

            int id = cursor.getInt(cursor.getColumnIndex(SQLiteHelper.KEY_ID));
            String name = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_NAME));
            String macid = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_MACID));
            String plattformid = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_PLATTFORMID));
            byte[] image = cursor.getBlob(cursor.getColumnIndex(SQLiteHelper.KEY_IMAGE));
            String devicetype = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_DEVICETYPE));
            String optinalIP = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_OPTIONALIP));
            String userName = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_USERNAME));
            String password = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_PASSWORD));
            String state = "Loading...";

            //id gelöscht


            deviceInfo = new DeviceInfo(id, plattformid, name, macid, image, devicetype, optinalIP, userName, password, state);
            // Log.d("Hiwi","device info: "+id+" "+name+" "+macid+" "+sensorId);
            Log.d("Hiwi", "device info: " + " " + name + " " + macid + " ");
            devicelist.add(deviceInfo);
        }
        cursor.close();




        mAdapter.notifyDataSetChanged();


        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), mRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                //requestDeviceState();
                //mAdapter.notifyDataSetChanged();

                position_stelle = position;
                Log.d("Position in onClick", "Position ist " + position);

                // Getting todos under "Watchlist" tag name
                Log.d("ToDo", "Get todos under single Tag name");


                //Fehlerbehebung, sonst Array -1 Error
                if (position <= -1) {
                    //dialog.dismiss();
                    return;
                }

                //DeviceInfo deviceInfo = compareMonitoringList.get(position);
                //Toast.makeText(getApplicationContext(), deviceInfo.getName() + " is selected! " + deviceInfo.getId() + " Plattformid" + deviceInfo.getPlattformid() + " sensorcount " + db.getSensorCount() + " actuatorcount " + db.getActuatorCount(), Toast.LENGTH_SHORT).show();
                //Intent intent = new Intent(MonitoringDeviceOverviewActivity.this, MonitoringDeployDeviceActivity.class);
                //startActivity(intent);
            }


            @Override
            public void onLongClick(View view, final int position) {

            }
        }));

        deployRequest = (Button) findViewById(R.id.switch_deploy);


    }

    public void deployrequest(View v) {
       /* Intent intent = new Intent(MonitoringDeviceOverviewActivity.this, MonitoringDeployDeviceActivity.class);
        intent.putExtra("deviceid", compareMonitoringList.get(position_stelle).getPlattformid());
        Bundle bundle = new Bundle();
        intent.putExtras(bundle);
        startActivity(intent);*/
    }

    public void requestMonitoringAdapter(String deviceInfoGenerateId){
        //http://192.168.209.194:8888/deploy/master/api/monitoring-adapters/

        RequestQueue queue = Volley.newRequestQueue(context); // this = context
        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url + "/api/monitoring-adapters/", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("Response", response.toString());

                        try {
                            JSONObject mainObject = response;

                            mainObject = response.getJSONObject("_embedded");

                            Log.d("Response ", mainObject.toString());
                            JSONArray jsonArray = mainObject.getJSONArray("monitoring-adapters");
                            Log.d("Response Array", jsonArray.toString());



                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject explrObject = jsonArray.getJSONObject(i);

                                JSONArray devicetype = explrObject.getJSONArray("deviceTypes");
                                //String id;
                                String deviceName = null;
                                for (int j = 0; j < devicetype.length(); j++){
                                    JSONObject deviceTypeObj = devicetype.getJSONObject(j);
                                    //id = deviceTypeObj.getString("id");
                                    deviceName = deviceTypeObj.getString("name"); //Raspberry Pi
                                }


                                String name = explrObject.getString("name");
                                //String description = explrObject.getString("description");
                                String unit = explrObject.getString("unit");
                                //parameters is a json array
                                //String username = explrObject.getString("username");


                                //TODO device username
                                monitoringAdapter = new MonitoringAdapters(name, unit, deviceName);
                                //neues DeviceInfo weil das Image beim compare nicht übereinstimmt

                                ArrayList<MonitoringAdapters> compareMonitoringList = new ArrayList<>();
                                compareMonitoringList.add(monitoringAdapter);
                                for(MonitoringAdapters monitoringAdapters:compareMonitoringList){
                                    compareTypesofDevices(deviceInfoGenerateId, monitoringAdapters);

                                }


                                mAdapter.notifyDataSetChanged();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("Error Volley requestDeviceState()" + error);
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
        //This method is responsible that the request dont have a timeout
        getRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(getRequest);


    }



    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MonitoringDeployDeviceActivity.this, MonitoringDeviceOverviewActivity.class);
        startActivity(intent);
        //super.onBackPressed();
    }


    public void compareTypesofDevices(String deviceGenerateId, MonitoringAdapters monitoringAdapters) {
        RequestQueue queue = Volley.newRequestQueue(this);  // this = context

        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url + "/api/devices/"+ deviceGenerateId, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("Response", response.toString());
                        JSONObject obj = null;
                        // devicelist.clear();
                        // db.deleteall();


                        try {
                            JSONObject mainObject = response;

                                String componentType = mainObject.getString("componentType");
                                    if (componentType.equals(monitoringAdapters.getDeviceName())){
                                        monitoringAdaptersArrayList.add(monitoringAdapters);
                                }
                                mAdapter.notifyDataSetChanged();


                            } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error.Response", "errorrrrr");
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


// add it to the RequestQueue
        queue.add(getRequest);
        //dialog.dismiss();


    }


        public Map<String, String> getHeaderforAuthentification() {
        String username = "admin";
        String password = "admin";
        // String auth =new String(username + ":" + password);
        String auth = new String("admin:admin");
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