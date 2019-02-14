package com.example.sedaulusal.hiwijob.device;

import android.app.assist.AssistStructure;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.sedaulusal.hiwijob.R;

import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.sedaulusal.hiwijob.SettingActivity.mypreference;

/**
 * Created by sedaulusal on 21.06.17.
 */

public class SensorDeployAdapter extends BaseAdapter {

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

    private class ViewHolder {
        ImageView SensorimageView;
        TextView txtSensorName;
        TextView txtSensorPinset;
        ToggleButton toggleButton;

        public ToggleButton getToggleButton() {
            return toggleButton;
        }
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
            holder.toggleButton = (ToggleButton) itemView.findViewById(R.id.toggleButton);

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


        final ViewHolder finalHolder = holder;

        if (sensor.deployed) {
            finalHolder.toggleButton.setSelected(true);
            finalHolder.toggleButton.setChecked(true);
        } else {
            finalHolder.toggleButton.setSelected(false);
            finalHolder.toggleButton.setChecked(false);
        }


        /*
         * Sets up a SwipeRefreshLayout.OnRefreshListener that is invoked when the user
         * performs a swipe-to-refresh gesture.
         */
        final DeployRequestActivity activity = this.context;




        holder.toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean toggleButtonState = finalHolder.toggleButton.isChecked();
                final String urlSensor = url+"/api/deploy/sensor/" + sensor.getGeneratesensorid();


                if (toggleButtonState == true) {


                    RequestQueue queue1 = Volley.newRequestQueue(context); // this = context
                    JsonObjectRequest jsonObjReqPOST = new JsonObjectRequest(Request.Method.POST,
                            urlSensor, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("ResponseAdapterPOST", response.toString());
                            System.out.println("ResponseAdapterPost" + response.toString());

                            if (response.toString().contains("201") || response.toString().contains("true") ) {
                                sensor.deployed = true;
                                notifyDataSetChanged();

                            } else {
                                sensor.deployed = false;
                                notifyDataSetChanged();
                            }

                        }

                    },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    //sensor.deployed = false;
                                    if(error.toString().contains("TimeoutError")){
                                        activity.updateSensorsDeploy();
                                        //sensor.deployed = true;
                                        notifyDataSetChanged();
                                    }else{
                                        sensor.deployed = false;
                                        notifyDataSetChanged();
                                    }
                                    //notifyDataSetChanged();
                                }
                            }
                    );

                    queue1.add(jsonObjReqPOST);


                } else if (toggleButtonState == false) {
                    RequestQueue queue3 = Volley.newRequestQueue(context); // this = context


                    StringRequest jsonObjReqPOST = new StringRequest(Request.Method.DELETE,
                            urlSensor, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // response
                            Log.d("ResponseAdapterPOST", (String) response);

                            if (response.contains("201")) {
                                sensor.deployed = true;
                                notifyDataSetChanged();
                                Toast.makeText(context, "Sucess", Toast.LENGTH_SHORT).show();

                            } else {
                                sensor.deployed = false;
                                notifyDataSetChanged();
                            }


                        }

                    },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    if(error.toString().contains("TimeoutError")){
                                        activity.updateSensorsDeploy();
                                        //sensor.deployed = true;
                                        notifyDataSetChanged();
                                    }else{
                                        sensor.deployed = true;
                                        notifyDataSetChanged();
                                    }

                                }
                            }
                    );
                    queue3.add(jsonObjReqPOST);


                }

            }
        });


        return itemView;
    }


}






