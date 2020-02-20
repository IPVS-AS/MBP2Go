package com.example.sedaulusal.hiwijob.monitoring;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

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


    private RecyclerView.LayoutManager mLayoutManager;
    SQLiteHelper db;
    Context context;
    DeviceInfo deviceInfo;
    Cursor cursor;
    int position_stelle;

    SharedPreferences sharedPreferences;
    String url;
    MonitoringAdapters monitoringAdapter;
    String deviceinforid = "";
    String state = "";
    SwipeRefreshLayout mSwipeRefreshLayout;

    ListView listView;
    MonitoringDeployAdapterTestSensorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring_deploy_device);
        context = this;
        //requestMonitoring();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        sharedPreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        url = sharedPreferences.getString("URL", "");


        db = new SQLiteHelper(this);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.monitoring_swiperefresh);

        mSwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        requestMonitoringAdapterState(monitoringAdapter.getMonitoringID(), monitoringAdapter.getDeviceId());

                    }
                }
        );
        //mRecyclerView = (RecyclerView) findViewById(R.id.recycler_monitoring_deploy);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        //mRecyclerView.setHasFixedSize(true);


        // use a linear layout manager
        //mLayoutManager = new LinearLayoutManager(this);
        //mRecyclerView.setLayoutManager(mLayoutManager);

        compareMonitoringList = new ArrayList<>();
        monitoringAdaptersArrayList = new ArrayList<>();
        devicelist = new ArrayList<>();

        listView = (ListView) findViewById(R.id.listview_monitoring_deploy);


        //then populate myListItems
        mAdapter = new MonitoringDeployAdapterTestSensorAdapter(this, R.layout.layout_deploy, monitoringAdaptersArrayList);
        //sensorAdapter.notifyDataSetChanged();
        listView.setAdapter(mAdapter);

        // specify an adapter (see also next example)
        //mAdapter = new MonitoringDeployAdapterTestSensorAdapter(context, R.layout.monitoring_deploy_item, monitoringAdaptersArrayList);
        //mRecyclerView.setAdapter(mAdapter);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {

            } else {
                deviceinforid = extras.getString("deviceinfoid");


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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    final int position, long id) {
                //requestDeviceState();
                //mAdapter.notifyDataSetChanged();

                position_stelle = position;

                //Fehlerbehebung, sonst Array -1 Error
                if (position <= -1) {
                    //dialog.dismiss();
                    return;
                }

                //TODO get rigth device! from Overview!

                MonitoringAdapters monitoringAdapters = monitoringAdaptersArrayList.get(position);
                Toast.makeText(getApplicationContext(), deviceInfo.getName() + " is selected! " + deviceInfo.getId() + " Plattformid" + deviceInfo.getPlattformid() + " moniname " + monitoringAdapters.getName() + " denvicename " + monitoringAdapters.getDeviceName() + " optionalip " + monitoringAdapters.getMonitoringID(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MonitoringDeployDeviceActivity.this, MonitoringDiagramm2Activity.class);
                intent.putExtra("monitoringname", monitoringAdapters.getName());
                intent.putExtra("monitoringid", monitoringAdapters.getMonitoringID());
                intent.putExtra("deviceinfoid", deviceinforid);


                startActivity(intent);

            }
        });

    }


    public void requestMonitoringAdapter(String deviceInfoGenerateId) {
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
                                for (int j = 0; j < devicetype.length(); j++) {
                                    JSONObject deviceTypeObj = devicetype.getJSONObject(j);
                                    //id = deviceTypeObj.getString("id");
                                    deviceName = deviceTypeObj.getString("name"); //Raspberry Pi
                                }


                                String name = explrObject.getString("name");
                                //String description = explrObject.getString("description");
                                String unit = explrObject.getString("unit");
                                String monitoringId = explrObject.getString("id");

                                //parameters is a json array
                                //String username = explrObject.getString("username");
                                requestMonitoringAdapterState(monitoringId, deviceInfoGenerateId);

                                //TODO device username
                                monitoringAdapter = new MonitoringAdapters(name, unit, monitoringId, state, deviceName, deviceInfoGenerateId);
                                //neues DeviceInfo weil das Image beim compare nicht übereinstimmt

                                ArrayList<MonitoringAdapters> compareMonitoringList = new ArrayList<>();
                                compareMonitoringList.add(monitoringAdapter);
                                for (MonitoringAdapters monitoringAdapters : compareMonitoringList) {
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


    public void requestMonitoringAdapterState(String monitoringId, String deviceInfoGenerateId) {
        //http://192.168.209.194:8888/deploy/master/api/monitoring-adapters/
        RequestQueue queue = Volley.newRequestQueue(context); // this = context
        // prepare the Request
        //http://192.168.209.194:8888/deploy/master/api/monitoring/state/5c866db2f8ea1203bc3518e8
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url + "/api/monitoring/state/" + deviceInfoGenerateId, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("Response", response.toString());

                        try {
                            JSONObject mainObject = response;
                            state = mainObject.getString(monitoringId + "@" + deviceInfoGenerateId);
                            for (MonitoringAdapters monitoringAdapters : monitoringAdaptersArrayList) {
                                if (monitoringAdapters.getMonitoringID().equals(monitoringId)) {
                                    monitoringAdapters.setState(state);
                                }
                            }
                            mSwipeRefreshLayout.setRefreshing(false);
                            mAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        System.out.println("Error Volley request monitoring state" + error);
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
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url + "/api/devices/" + deviceGenerateId, null,
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
                            if (componentType.equals(monitoringAdapters.getDeviceName())) {
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

    public void requestMonitoring(String monitoringid) {
        RequestQueue queue = Volley.newRequestQueue(context); // this = context
        JSONArray array= new JSONArray();
                MyJsonObjectRequest request=new MyJsonObjectRequest(Request.Method.POST,"http://192.168.209.194:8888/deploy/master/api/monitoring/5c866db2f8ea1203bc3518e8?adapter=5ce34000f8ea1208475c8b36", array, new Response.Listener<JSONObject>() {
       // MyJsonObjectRequest request=new MyJsonObjectRequest(Request.Method.POST,url+"/api/monitoring/"+deviceinforid+"?adapter="+monitoringid, array, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.print(response);
                Toast.makeText(getApplicationContext(),"deployed", Toast.LENGTH_SHORT).show();


            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                System.out.print(error.toString());
                error.printStackTrace();
                Log.i("onErrorResponse", "Error");
            }
        }){
            //
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> authentification = getHeaderforAuthentification();
                return authentification;

            }

        };

        //This method is responsible that the request dont have a timeout
        request.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);

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




}