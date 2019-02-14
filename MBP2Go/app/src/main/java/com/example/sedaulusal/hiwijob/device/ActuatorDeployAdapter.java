package com.example.sedaulusal.hiwijob.device;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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

public class ActuatorDeployAdapter extends BaseAdapter {

    private Context context;
    private  int layout;
    private ArrayList<ActuatorInfo> actuatorList;

    SharedPreferences sharedPreferences;
    String url;


    public ActuatorDeployAdapter(Context context, int layout, ArrayList<ActuatorInfo> actuatorList) {
        this.context = context;
        this.layout = layout;
        this.actuatorList = actuatorList;

    }


    @Override
    public int getCount() {
        return actuatorList.size();
    }

    @Override
    public Object getItem(int position) {
        return actuatorList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder{
        ImageView ActuatorimageView;
        TextView txtActuatorName;
        TextView txtActuatorId;
        ToggleButton actuatorToggleButton;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = new ViewHolder();
        DeployRequestActivity deployRequestActivity = new DeployRequestActivity();
        //String url = deployRequestActivity.url;

        /*
        SharedPreferences to get the url from setting
         */
        sharedPreferences = context.getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        url = sharedPreferences.getString("URL", "");



        if(row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);

            holder.txtActuatorName = (TextView) row.findViewById(R.id.txtAktuatorName);
            holder.txtActuatorId = (TextView) row.findViewById(R.id.txtActuatorId);
            holder.ActuatorimageView = (ImageView) row.findViewById(R.id.imgAktuator);
            holder.actuatorToggleButton = (ToggleButton) row.findViewById(R.id.toggleButtonactuator);
            row.setTag(holder);
        }
        else {
            holder = (ViewHolder) row.getTag();
        }

        final ActuatorInfo actuator = actuatorList.get(position);

        //TO DO: set name from spinner and hardcoded Imagins
        holder.txtActuatorName.setText(actuator.getName());
        holder.txtActuatorId.setText(actuator.getActuatorPinset());

        byte[] actuatorImage = actuator.getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(actuatorImage, 0, actuatorImage.length);
        holder.ActuatorimageView.setImageBitmap(bitmap);

        final ViewHolder finalHolder = holder;

        if (actuator.deployed) {
            finalHolder.actuatorToggleButton.setSelected(true);
            finalHolder.actuatorToggleButton.setChecked(true);
        } else {
            finalHolder.actuatorToggleButton.setSelected(false);
            finalHolder.actuatorToggleButton.setChecked(false);
        }


        holder.actuatorToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean toggleButtonState = finalHolder.actuatorToggleButton.isChecked();
                final String urlActuator = url+"/api/deploy/actuator/" + actuator.getGenerateActuatorId();


                if (toggleButtonState == true) {


                    RequestQueue queue1 = Volley.newRequestQueue(context); // this = context
                    JsonObjectRequest jsonObjReqPOST = new JsonObjectRequest(Request.Method.POST,
                            urlActuator, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("ResponseAdapterPOST", response.toString());

                            if (response.toString().contains("201")) {
                                actuator.deployed = true;
                                notifyDataSetChanged();

                            } else {
                                actuator.deployed = false;
                                notifyDataSetChanged();
                            }

                        }

                    },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    actuator.deployed = false;
                                    notifyDataSetChanged();
                                }
                            }
                    );

                    queue1.add(jsonObjReqPOST);


                } else if (toggleButtonState == false) {
                    RequestQueue queue3 = Volley.newRequestQueue(context); // this = context


                    StringRequest jsonObjReqPOST = new StringRequest(Request.Method.DELETE,
                            urlActuator, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // response
                            Log.d("ResponseAdapterPOST", (String) response);

                            if (response.contains("201")) {
                                actuator.deployed = true;
                                notifyDataSetChanged();
                                Toast.makeText(context, "Sucess", Toast.LENGTH_SHORT).show();

                            } else {
                                actuator.deployed = false;
                                notifyDataSetChanged();
                            }


                        }

                    },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    actuator.deployed = true;
                                    notifyDataSetChanged();
                                }
                            }
                    );
                    queue3.add(jsonObjReqPOST);


                }

            }
        });

        return row;
    }
}