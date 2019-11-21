package com.example.sedaulusal.hiwijob.monitoring;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ekalips.fancybuttonproj.FancyButton;
import com.example.sedaulusal.hiwijob.R;
import com.example.sedaulusal.hiwijob.device.DeployRequestActivity;
import com.example.sedaulusal.hiwijob.device.SensorInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.sedaulusal.hiwijob.SettingActivity.mypreference;

/**
 * Created by sedaulusal on 21.06.17.
 */

public class MonitoringDeployAdapterTestSensorAdapter extends BaseAdapter {

    private static String frequencyString = "";
    private MonitoringDeployDeviceActivity context;
    private int layout;
    private ArrayList<MonitoringAdapters> monitoringAdapterList;
    SharedPreferences sharedPreferences;
    String url;
    private MonitoringDeployDeviceActivity monitoringDeployDeviceActivity;


    public MonitoringDeployAdapterTestSensorAdapter(MonitoringDeployDeviceActivity context, int layout, ArrayList<MonitoringAdapters> monitoringAdapters) {
        this.context = context;
        this.layout = layout;
        this.monitoringAdapterList = monitoringAdapters;
    }

    @Override
    public int getCount() {
        return monitoringAdapterList.size();
    }

    @Override
    public Object getItem(int position) {
        return monitoringAdapterList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder {
        TextView monitoringName;
        TextView unit;
        TextView state;
        FancyButton monitoringDeploy;
        ProgressBar unitProgress;
        ProgressBar stateProgress;


    }

    public MonitoringDeployDeviceActivity getContext() {
        return context;
    }


    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        //View row = view;
        View itemView = view;
        ViewHolder holder = new ViewHolder();

        /*
        SharedPreferences to get the url from setting
         */
        sharedPreferences = context.getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        url = sharedPreferences.getString("URL", "");


        if (itemView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            itemView = inflater.inflate(R.layout.monitoring_deploy_item, viewGroup, false);


            holder.unit = (TextView) itemView.findViewById(R.id.txtMonitoringUnit);
            holder.monitoringName = (TextView) itemView.findViewById(R.id.txtMonitoringName);
            holder.state = (TextView) itemView.findViewById(R.id.txtMonitoringState);
            holder.monitoringDeploy = (FancyButton) itemView.findViewById(R.id.btn_monitoringdeploy);
            holder.unitProgress = (ProgressBar) itemView.findViewById(R.id.monitoring_progressBar_unit);
            holder.stateProgress = (ProgressBar) itemView.findViewById(R.id.monitoring_progressBar_state);
            itemView.setTag(holder);

        } else {
            holder = (ViewHolder) itemView.getTag();
        }


        final MonitoringAdapters monitoringadapters = monitoringAdapterList.get(position);

        //TO DO: set name from spinner and hardcoded Imagins
        holder.monitoringName.setText(monitoringadapters.getName());
        holder.unit.setText(monitoringadapters.getUnit());
        if (!monitoringadapters.getUnit().isEmpty()){
            holder.unitProgress.setVisibility(View.INVISIBLE);
        }
        holder.state.setText(monitoringadapters.getState());


        final ViewHolder finalHolder = holder;
        monitoringDeployDeviceActivity = getContext();

        if (monitoringadapters.getState().toLowerCase().equals("not_ready")) {
            //finalHolder.toggleButton.setSelected(true);
            //finalHolder.toggleButton.setChecked(true);
            finalHolder.stateProgress.setVisibility(View.INVISIBLE);
            finalHolder.monitoringDeploy.expand();
            finalHolder.monitoringDeploy.setText("Not Ready");
           /* if(monitoringadapters.running){
                finalHolder.sensorstartorpause.expand();
                finalHolder.sensorstartorpause.setText("Pause");
            }else{
                finalHolder.sensorstartorpause.expand();
                finalHolder.sensorstartorpause.setText("Start");
            }*/
        } else if (monitoringadapters.getState().toLowerCase().equals("running")) {
            finalHolder.stateProgress.setVisibility(View.INVISIBLE);
            finalHolder.monitoringDeploy.expand();
            finalHolder.monitoringDeploy.setText("Undeploy");
        }else if (monitoringadapters.getState().toLowerCase().equals("deployed")){
            finalHolder.monitoringDeploy.collapse();
            monitoringDeployDeviceActivity.requestMonitoringAdapterState(monitoringadapters.getMonitoringID(), monitoringadapters.getDeviceId());
            notifyDataSetChanged();
        }else if ( monitoringadapters.getState().toLowerCase().equals("ready")) {
            finalHolder.stateProgress.setVisibility(View.INVISIBLE);
            finalHolder.monitoringDeploy.expand();
                finalHolder.monitoringDeploy.setText("Deploy");
                notifyDataSetChanged();


        } else {
            finalHolder.monitoringDeploy.collapse();

        }


        /*
         * Sets up a SwipeRefreshLayout.OnRefreshListener that is invoked when the user
         * performs a swipe-to-refresh gesture.
         */
        final MonitoringDeployDeviceActivity activity = this.context;

        ViewHolder finalHolder1 = holder;
        holder.monitoringDeploy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v instanceof FancyButton) {

                    if (monitoringadapters.getState().toLowerCase().equals("running")) {
                        finalHolder.monitoringDeploy.collapse();
                        deleteMonitoring(monitoringadapters.getMonitoringID(), monitoringadapters.getDeviceId(), finalHolder.monitoringDeploy);
                        monitoringDeployDeviceActivity.requestMonitoringAdapterState(monitoringadapters.getMonitoringID(), monitoringadapters.getDeviceId());
                        //notifyDataSetChanged();
                    }else if (monitoringadapters.getState().toLowerCase().equals("deployed")|| monitoringadapters.getState().toLowerCase().equals("ready")) {
                        finalHolder.monitoringDeploy.collapse();
                        requestMonitoring(monitoringadapters.getMonitoringID(), monitoringadapters.getDeviceId(), finalHolder.monitoringDeploy);
                        monitoringDeployDeviceActivity.requestMonitoringAdapterState(monitoringadapters.getMonitoringID(), monitoringadapters.getDeviceId());
                       // notifyDataSetChanged();
                    }
                    /*if (!sensor.deployed) {

                        //((FancyButton) v).setText("Hi1");
                        //getSensorAdapter(sensor,finalHolder.sensorstartorpause, finalHolder.fancyBtn);
                           // runningSensorwithFrequency(sensor,finalHolder.sensorstartorpause, finalHolder.fancyBtn);
                            deploySensor(sensor,finalHolder.sensorstartorpause, finalHolder.fancyBtn);

                    } else {
                        undeploySensor(sensor,finalHolder.sensorstartorpause,finalHolder.fancyBtn);
                        //((FancyButton) v).expand();
                        //((FancyButton) v).setText("Hi");
                    }*/
                }
            }
        });


        return itemView;
    }


    public void requestMonitoring(String monitoringid, String deviceinforid, FancyButton monitoringButton) {
        RequestQueue queue = Volley.newRequestQueue(context); // this = context
        JSONArray array = new JSONArray();
        //MyJsonObjectRequest request=new MyJsonObjectRequest(Request.Method.POST,"http://192.168.209.194:8888/deploy/master/api/monitoring/5c866db2f8ea1203bc3518e8?adapter=5ce34000f8ea1208475c8b36", array, new Response.Listener<JSONObject>() {
        //http://192.168.209.194:8888/deploy/master/api/monitoring/5c7fd67df8ea1203bcf381b8/valueLogs?adapter=5d8c8465b1c4d32a86a28543&size=200&sort=time,desc
        MyJsonObjectRequest request = new MyJsonObjectRequest(Request.Method.POST, url + "/api/monitoring/" + deviceinforid + "?adapter=" + monitoringid, array, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.print(response);
                Toast.makeText(context, "deployed", Toast.LENGTH_SHORT).show();
                //monitoringButton.expand();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
                System.out.print(error.toString());
                error.printStackTrace();
                Log.i("onErrorResponse", "Error");
            }
        }) {
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


    public void deleteMonitoring(String monitoringid, String deviceinforid, FancyButton monitoringButton) {
        monitoringButton.collapse();
        RequestQueue queue = Volley.newRequestQueue(context); // this = context
        //MyJsonObjectRequest request=new MyJsonObjectRequest(Request.Method.POST,"http://192.168.209.194:8888/deploy/master/api/monitoring/5c866db2f8ea1203bc3518e8?adapter=5ce34000f8ea1208475c8b36", array, new Response.Listener<JSONObject>() {
        //http://192.168.209.194:8888/deploy/master/api/monitoring/5c7fd67df8ea1203bcf381b8/valueLogs?adapter=5d8c8465b1c4d32a86a28543&size=200&sort=time,desc
        StringRequest deleterequest = new StringRequest(Request.Method.DELETE, url + "/api/monitoring/" + deviceinforid + "?adapter=" + monitoringid,
        new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.print(response);
                Toast.makeText(context, "Success! Undeploy Device", Toast.LENGTH_SHORT).show();
                //monitoringButton.expand();
               // notifyDataSetChanged();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "ERROR! Could not undeploy device", Toast.LENGTH_SHORT).show();
                System.out.print(error.toString());
                error.printStackTrace();
                Log.i("onErrorResponse", "Error");
            }
        }) {
            //
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> authentification = getHeaderforAuthentification();
                return authentification;

            }
        };
        //This method is responsible that the request dont have a timeout
        deleterequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(deleterequest);

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

