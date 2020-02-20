package com.example.sedaulusal.hiwijob.device;

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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ekalips.fancybuttonproj.FancyButton;
import com.example.sedaulusal.hiwijob.R;

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

public class SensorDeployAdapter extends BaseAdapter {

    private static String frequencyString = "";
    private DeployRequestActivity context;
    private int layout;
    private ArrayList<SensorInfo> sensorList;
    SharedPreferences sharedPreferences;
    String url;


    public SensorDeployAdapter(DeployRequestActivity context, int layout, ArrayList<SensorInfo> foodsList) {
        this.context = context;
        this.layout = layout;
        this.sensorList = foodsList;
    }

    @Override
    public int getCount() {
        return sensorList.size();
    }

    @Override
    public Object getItem(int position) {
        return sensorList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder {
        ImageView SensorimageView;
        TextView txtSensorName;
        TextView txtSensorPinset;
        FancyButton sensorstartorpause;
        FancyButton fancyBtn;

    }

    public DeployRequestActivity getContext() {
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
            itemView = inflater.inflate(R.layout.layout_deploy, viewGroup, false);


            holder.txtSensorName = (TextView) itemView.findViewById(R.id.txtSensorName);
            holder.txtSensorPinset = (TextView) itemView.findViewById(R.id.txtSensorId);
            holder.SensorimageView = (ImageView) itemView.findViewById(R.id.imgSensor);
            //holder.toggleButton = (ToggleButton) itemView.findViewById(R.id.toggleButton);
            holder.sensorstartorpause = (FancyButton) itemView.findViewById(R.id.btn_tgl_sensorstartpause);
            //holder.circularProgressButton = (CircularProgressButton) itemView.findViewById(R.id.btn_animatedeploy);
            holder.fancyBtn = (FancyButton) itemView.findViewById(R.id.btn1);
            itemView.setTag(holder);

        } else {
            holder = (ViewHolder) itemView.getTag();
        }


        final SensorInfo sensor = sensorList.get(position);

        //TO DO: set name from spinner and hardcoded Imagins
        holder.txtSensorName.setText(sensor.getName());
        holder.txtSensorPinset.setText(sensor.getSensorPinset());

        byte[] sensorImage = sensor.getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(sensorImage, 0, sensorImage.length);
        holder.SensorimageView.setImageBitmap(bitmap);
        //holder.fancyBtn.collapse();

        final ViewHolder finalHolder = holder;

        if (sensor.deployed) {
            //finalHolder.toggleButton.setSelected(true);
            //finalHolder.toggleButton.setChecked(true);
            finalHolder.fancyBtn.expand();
            finalHolder.fancyBtn.setText("Undeploy sensor");
            finalHolder.sensorstartorpause.setVisibility(View.VISIBLE);
            if(sensor.running){
                finalHolder.sensorstartorpause.expand();
                finalHolder.sensorstartorpause.setText("Pause");
            }else{
                finalHolder.sensorstartorpause.expand();
                finalHolder.sensorstartorpause.setText("Start");
            }


        } else {
            //finalHolder.toggleButton.setSelected(false);
            //finalHolder.toggleButton.setChecked(false);
            finalHolder.fancyBtn.expand();
            finalHolder.fancyBtn.setText("Deploy sensor");
            finalHolder.sensorstartorpause.setVisibility(View.GONE);

        }


        /*
         * Sets up a SwipeRefreshLayout.OnRefreshListener that is invoked when the user
         * performs a swipe-to-refresh gesture.
         */
        final DeployRequestActivity activity = this.context;

        holder.fancyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v instanceof  FancyButton)
                {
                    if (!sensor.deployed) {

                        //((FancyButton) v).setText("Hi1");
                        //getSensorAdapter(sensor,finalHolder.sensorstartorpause, finalHolder.fancyBtn);
                           // runningSensorwithFrequency(sensor,finalHolder.sensorstartorpause, finalHolder.fancyBtn);
                            deploySensor(sensor,finalHolder.sensorstartorpause, finalHolder.fancyBtn);

                    } else {
                        undeploySensor(sensor,finalHolder.sensorstartorpause,finalHolder.fancyBtn);
                        //((FancyButton) v).expand();
                        //((FancyButton) v).setText("Hi");
                    }
                }
            }
        });

        holder.sensorstartorpause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v instanceof  FancyButton) {
                    if (!sensor.running) {

                        //((FancyButton) v).setText("Hi1");
                        getSensorAdapter(sensor, finalHolder.sensorstartorpause);
                        //startRunningSensor(sensor, finalHolder.sensorstartorpause);

                    } else {
                        //undeploySensor(sensor, finalHolder.sensorstartorpause, finalHolder.fancyBtn);
                        stopRunningSensor(sensor, finalHolder.sensorstartorpause);

                        //((FancyButton) v).expand();
                        //((FancyButton) v).setText("Hi");
                    }
                }
            }
        });

        /*holder.toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean toggleButtonState = finalHolder.toggleButton.isChecked();

                final String urlSensor = url+"/api/deploy/sensor/" + sensor.getGeneratesensorid();
                if (toggleButtonState == true) {
                    getSensorAdapter(sensor,finalHolder.toggleButton, finalHolder.sensorstartorpause, finalHolder.fancyBtn);

                    //frequenzcy();
                    //YouEditTextValue1.length();
                }else if(toggleButtonState == false){
                    undeploySensor(sensor,finalHolder.toggleButton,finalHolder.fancyBtn);
                }

            }
        });*/


        return itemView;
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

    public void frequenzcy(SensorInfo sensor,FancyButton sensorstartorpause){
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        final EditText edittext = new EditText(context);
        edittext.setInputType(InputType.TYPE_CLASS_NUMBER);

        alert.setMessage("Frequency:");
        alert.setTitle("Option");

        alert.setView(edittext);
        //frequencyString="";
        edittext.setText(frequencyString);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //What ever you want to do with the value
               // Editable YouEditTextValue = edittext.getText();
                //OR
                frequencyString = edittext.getText().toString();
                    if(!(frequencyString.length() == 0) || !(frequencyString == null) ||!frequencyString.contains("")){

                        //toogleButonstate.setChecked(true);
                        try {
                            runningSensorwithFrequency(sensor, sensorstartorpause);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                }

            }
        }
        );

        if(frequencyString.length() == 0 || frequencyString== null||frequencyString.contains("")){
            //fancyButton.expand();
        }
        alert.show();
    }

    public void getSensorAdapter(SensorInfo sensor,  FancyButton sensorstartorpause){
        final String urlSensor = url+"/api/sensors/" + sensor.getGeneratesensorid();

        RequestQueue queue = Volley.newRequestQueue(context);  // this = context
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
                            JSONArray parameterObject = adapterObject.getJSONArray("parameters");
                            if(parameterObject.toString().length() > 2){
                                frequenzcy(sensor, sensorstartorpause);

                            }else{
                                System.out.println("Teste else");
                               runningSensorwithoutFrequency(sensor, sensorstartorpause);
                            }



                        } catch (JSONException e) {
                            e.printStackTrace();
                            System.out.println("DEBUG JSON ERROR");

                            //  dialog.dismiss();

                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error.Response", "errorrrrr");
                        // dialog.dismiss();

                    }
                })
        {
            //
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> authentification = getHeaderforAuthentification();
                return authentification;

            }

        };

        queue.add(getRequest);

    }


    public void runningSensorwithFrequency(SensorInfo sensor, FancyButton sensorstartorpause) throws JSONException {
        final String requestBody = "";
        final DeployRequestActivity activity = this.context;
        //fancyButton.collapse();
        //sensorstartorpause.setVisibility(View.GONE);
        sensorstartorpause.collapse();
        final String urlSensor = url+"/api/start/sensor/" + sensor.getGeneratesensorid();
//http://192.168.209.194:8888/deploy/master/api/deploy/sensor/5c7fd6d3f8ea1203bcf381b9
        //if (toggleButton.isChecked()==true) {
            System.out.println("Teste true oder ner");

            RequestQueue queue1 = Volley.newRequestQueue(context); // this = context
            JSONArray arr = new JSONArray();
            JSONObject params_actuator = new JSONObject();
            params_actuator.put("name", "Frequency");
            params_actuator.put("value", Integer.valueOf(frequencyString));
            arr.put(params_actuator);
            JsonArrayRequest jsonObjReqPOST = new JsonArrayRequest(Request.Method.POST,
                    urlSensor, arr, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    Log.d("ResponseAdapterPOST", response.toString());
                    System.out.println("ResponseAdapterPost" + response.toString());

                    if (response.toString().contains("201") || response.toString().contains("true") ) {
                        sensor.running = true;
                        notifyDataSetChanged();

                    } else {
                        sensor.running = false;
                        notifyDataSetChanged();
                    }

                }

            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //sensor.deployed = false;
                            System.out.println("DEBUG TIMEOUT ERROR");
                            if(error.toString().contains("TimeoutError")){
                               // activity.updateSensorsDeploy();
                                //sensor.deployed = true;
                                notifyDataSetChanged();
                                System.out.println("DEBUG TIMEOUT ERROR");
                            }else{
                                System.out.println("ERRRROOOOORRR");
                                //TODO!!!!!!!!!!!!!!!!
                                //I/System.out: com.android.volley.ParseError: org.json.JSONException: Value {"success":true,"globalMessage":"Success","fieldErrors":{}} of type org.json.JSONObject cannot be converted to JSONArray
                                sensor.running = true;
                                notifyDataSetChanged();
                            }
                        }
                    }
            ){
                //
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> authentification = getHeaderforAuthentification();
                    return authentification;

                }
            };
            int socketTimeout = 80000;//30 seconds - change to what you want
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            jsonObjReqPOST.setRetryPolicy(policy);
            queue1.add(jsonObjReqPOST);
       // }
    }


    public void runningSensorwithoutFrequency(SensorInfo sensor,  FancyButton sensorstartorpause) throws JSONException {
        final String requestBody = "";
        final DeployRequestActivity activity = this.context;
        sensorstartorpause.collapse();
        //fancyButton.collapse();
        //sensorstartorpause.setVisibility(View.GONE);
        final String urlSensor = url+"/api/start/sensor/" + sensor.getGeneratesensorid();
        //http://192.168.209.194:8888/deploy/master/api/deploy/sensor/5c7fd6d3f8ea1203bcf381b9
        //if (toggleButton.isChecked()==true) {
        System.out.println("Teste true oder ner");
        JSONObject params_sensor = new JSONObject();

        try {
            params_sensor.put("","[]");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        RequestQueue queue1 = Volley.newRequestQueue(context); // this = context
        JSONArray arr = new JSONArray();
        arr.length();

        JSONObject params_actuator = new JSONObject();
        //params_actuator.put("name", "Frequency");
        //params_actuator.put("value", Integer.valueOf(frequencyString));
        //arr.put(params_actuator);


        JsonArrayRequest jsonObjReqPOST = new JsonArrayRequest(Request.Method.POST,
                urlSensor, arr, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("ResponseAdapterPOST", response.toString());
                System.out.println("ResponseAdapterPost" + response.toString());
                System.out.println("ResponseAdapterPOST");

                if (response.toString().contains("201") || response.toString().contains("true") ) {
                    sensor.running = true;
                    sensor.deployed = true;
                    notifyDataSetChanged();

                } else {
                    sensor.running = false;
                    sensor.deployed = true;
                    notifyDataSetChanged();
                }

            }

        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //sensor.deployed = false;
                        if(error.toString().contains("TimeoutError")){
                            System.out.println("DEBUG TIMEOUT ERROR");
                            // activity.updateSensorsDeploy();
                            //sensor.deployed = true;
                            notifyDataSetChanged();
                        }else{
                            //sensor.running = false;
                            System.out.println("DEBUG ERROR"+error.toString());
                            //TODO
                            //TODO !!!!!!!!!!!!!!!!!!
                            sensor.running = true;
                            sensor.deployed = true;
                            notifyDataSetChanged();
                        }
                    }
                }

        ){
            //
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> authentification = getHeaderforAuthentification();
                return authentification;

            }


        };
        int socketTimeout = 80000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjReqPOST.setRetryPolicy(policy);
        queue1.add(jsonObjReqPOST);
        // }
    }


    public void deploySensor(SensorInfo sensor, FancyButton sensorstartorpause, FancyButton fancyButton){
        final DeployRequestActivity activity = this.context;
        fancyButton.collapse();
        sensorstartorpause.setVisibility(View.GONE);


        final String urlSensor = url+"/api/deploy/sensor/" + sensor.getGeneratesensorid();
        System.out.println("Test mmm");
//http://192.168.209.194:8888/deploy/master/api/deploy/sensor/5c7fd6d3f8ea1203bcf381b9
               // if (toggleButton.isChecked()==true) {
                    System.out.println("Teste true oder ner");
                    JSONObject params_sensor = new JSONObject();

                   /* try {
                        //params_sensor.put("","[]");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }*/


                    RequestQueue queue1 = Volley.newRequestQueue(context); // this = context
                    JsonObjectRequest jsonObjReqPOST = new JsonObjectRequest(Request.Method.POST,
                            urlSensor, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("ResponseAdapterPOST", response.toString());
                            System.out.println("ResponseAdapterPost" + response.toString());

                            if (response.toString().contains("201") || response.toString().contains("true") ) {
                                sensor.deployed = true;
                                //fancyButton.expand();

                                //sensorstartorpause.setVisibility(View.VISIBLE);
                                notifyDataSetChanged();

                            } else {
                                sensor.deployed = false;
                                sensor.running = false;
                                //fancyButton.expand();

                                //sensorstartorpause.setVisibility(View.GONE);
                                notifyDataSetChanged();
                            }

                        }

                    },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    //sensor.deployed = false;
                                    if(error.toString().contains("TimeoutError")){
                                        //activity.updateSensorsDeploy();
                                        //sensor.deployed = true;
                                        notifyDataSetChanged();
                                    }else{
                                        sensor.deployed = false;
                                        sensor.running = false;
                                       // fancyButton.expand();
                                        fancyButton.setText("ErrorListener");

                                        //sensorstartorpause.setVisibility(View.GONE);
                                        notifyDataSetChanged();
                                    }
                                    //notifyDataSetChanged();
                                }
                            }
                    ){
                        //
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> authentification = getHeaderforAuthentification();
                            return authentification;

                        }@Override
                        public byte[] getBody() {
                            return "[]".toString().getBytes();
                        }

                    };
                    int socketTimeout = 30000;//30 seconds - change to what you want
                    RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                    jsonObjReqPOST.setRetryPolicy(policy);
                    queue1.add(jsonObjReqPOST);
                //}
    }

    public void undeploySensor(SensorInfo sensor, FancyButton sensorstartorpause,FancyButton fancyButton){
        final DeployRequestActivity activity = this.context;

        final String urlSensor = url+"/api/deploy/sensor/" + sensor.getGeneratesensorid();
        System.out.println("Test mmm");
        fancyButton.collapse();
        sensorstartorpause.setVisibility(View.GONE);

        //http://192.168.209.194:8888/deploy/master/api/deploy/sensor/5c7fd6d3f8ea1203bcf381b9
        //if (toggleButton.isChecked()==false)  {
            System.out.println("Teste false oohhhh");

            RequestQueue queue3 = Volley.newRequestQueue(context); // this = context


            StringRequest jsonObjReqPOST = new StringRequest(Request.Method.DELETE,
                    urlSensor, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    // response
                    Log.d("ResponseAdapterPOST", (String) response);

                    if (response.contains("201")) {
                        sensor.deployed = true;
                        //sensorstartorpause.setVisibility(View.GONE);

                        notifyDataSetChanged();
                        Toast.makeText(context, "Sucess", Toast.LENGTH_SHORT).show();

                    } else {
                        sensor.deployed = false;
                        sensor.running = false;
                        notifyDataSetChanged();
                    }


                }

            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if(error.toString().contains("TimeoutError")){
                                //activity.updateSensorsDeploy();
                                sensor.deployed = true;
                                notifyDataSetChanged();
                            }else{
                                sensor.deployed = true;
                                notifyDataSetChanged();
                            }

                        }
                    }
            ){
                //
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> authentification = getHeaderforAuthentification();
                    return authentification;

                }
            };
            queue3.add(jsonObjReqPOST);


       // }
    }


    public void stopRunningSensor(SensorInfo sensor, FancyButton sensorstartorpause){
        final DeployRequestActivity activity = this.context;

        final String urlSensor = url+"/api/stop/sensor/" + sensor.getGeneratesensorid();
        sensorstartorpause.collapse();
        RequestQueue queue3 = Volley.newRequestQueue(context); // this = context
        StringRequest jsonObjReqPOST = new StringRequest(Request.Method.POST,
                urlSensor, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // response
                Log.d("ResponseAdapterPOST", (String) response);

                if (response.contains("200")) {
                    sensor.running = true;
                    sensor.deployed = true;

                    //sensorstartorpause.setVisibility(View.GONE);

                    notifyDataSetChanged();

                } else {
                    sensor.running= false;
                    sensor.deployed = true;

                    notifyDataSetChanged();
                }


            }

        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(error.toString().contains("TimeoutError")){
                            //activity.updateSensorsDeploy();
                            sensor.deployed = true;
                            notifyDataSetChanged();
                        }else{
                            sensor.deployed = true;
                            notifyDataSetChanged();
                        }

                    }
                }
        ){
            //
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> authentification = getHeaderforAuthentification();
                return authentification;

            }
        };
        queue3.add(jsonObjReqPOST);


        // }
    }





}






