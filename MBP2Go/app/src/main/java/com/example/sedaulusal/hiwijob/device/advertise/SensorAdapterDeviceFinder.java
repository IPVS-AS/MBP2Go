package com.example.sedaulusal.hiwijob.device.advertise;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.sedaulusal.hiwijob.R;
import com.example.sedaulusal.hiwijob.device.DeployRequestActivity;
import com.example.sedaulusal.hiwijob.device.SensorInfo;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sedaulusal on 21.06.17.
 */

public class SensorAdapterDeviceFinder extends BaseAdapter {

    private DeviceFinderActivity context;
    private  int layout;
    private ArrayList<SensorInfo> sensorList;

    public SensorAdapterDeviceFinder(DeviceFinderActivity context, int layout, ArrayList<SensorInfo> sensorList) {
        this.context = context;
        this.layout = layout;
        this.sensorList = sensorList;
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

    private class ViewHolder{
        ImageView SensorimageView;
        CheckedTextView txtSensorName;
        TextView txtSensorPinset;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        View row = view;
        ViewHolder holder = new ViewHolder();

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);

            holder.txtSensorName = (CheckedTextView) row.findViewById(R.id.txtSensorNameDeviceFinder);
            //holder.txtSensorPinset = (TextView) row.findViewById(R.id.txtSensorId);
            //holder.SensorimageView = (ImageView) row.findViewById(R.id.imgSensor);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        final SensorInfo sensor = sensorList.get(position);

        //TO DO: set name from spinner and hardcoded Imagins
        holder.txtSensorName.setText(sensor.getName());
       // holder.txtSensorPinset.setText(sensor.getSensorPinset());

       /* byte[] sensorImage = sensor.getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(sensorImage, 0, sensorImage.length);
        holder.SensorimageView.setImageBitmap(bitmap);*/

        final ViewHolder finalHolder = holder;


        if (sensor.deployed) {
            finalHolder.txtSensorName.setSelected(true);
            finalHolder.txtSensorName.setChecked(true);
        } else {

            finalHolder.txtSensorName.setSelected(false);
            finalHolder.txtSensorName.setChecked(false);
        }

        final DeviceFinderActivity activity = this.context;




        holder.txtSensorName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean toggleButtonState = finalHolder.txtSensorName.isChecked();
               // final String urlSensor = url+"/api/deploy/sensor/" + sensor.getGeneratesensorid();


                if (toggleButtonState == true) {
                    sensor.deployed = false;
                    finalHolder.txtSensorName.setTextColor(Color.BLACK);

                    notifyDataSetChanged();


                 /*   RequestQueue queue1 = Volley.newRequestQueue(context); // this = context
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

                    queue1.add(jsonObjReqPOST);*/


                } else if (toggleButtonState == false) {
                    sensor.deployed = true;
                    finalHolder.txtSensorName.setTextColor(context.getResources().getColor(R.color.colorPrimary));

                    notifyDataSetChanged();


                   /* RequestQueue queue3 = Volley.newRequestQueue(context); // this = context


                    StringRequest jsonObjReqPOST = new StringRequest(Request.Method.DELETE,
                            urlSensor, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // response
                            Log.d("ResponseAdapterPOST", (String) response);

                            if (response.contains("201")) {
                                sensor.deployed = true;
                                notifyDataSetChanged();
                                Toast.makeText(context, "Sucess", Toast.LENGTH_SHORT).show();*/

                            } else {
                                sensor.deployed = false;
                                //notifyDataSetChanged();
                            }


                        }

                    }
                    /*,
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
                    queue3.add(jsonObjReqPOST);*/


              //  }

           // }
        );


        return row;
    }
}
