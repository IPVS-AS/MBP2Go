package com.example.sedaulusal.hiwijob.device.advertise;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sedaulusal.hiwijob.R;
import com.example.sedaulusal.hiwijob.device.SensorInfo;

import java.util.ArrayList;

/**
 * Created by sedaulusal on 18.05.17.
 */
public class DeviceFinderSensorAdapter extends RecyclerView.Adapter<DeviceFinderSensorAdapter.SensorViewHolder> {


    private ArrayList<SensorInfo> sensorList;
    private ArrayList<SensorInfo> arraylist;
    private static SparseBooleanArray selectedItems = new SparseBooleanArray();

    private Cursor mCursor;
    private Context mContext;

    public DeviceFinderSensorAdapter(ArrayList<SensorInfo> sensorList, Cursor mCursor, Context mContext) {
        this.mCursor = mCursor;
        this.mContext = mContext;
        this.sensorList = sensorList;
        this.arraylist = new ArrayList<SensorInfo>();
        this.arraylist.addAll(sensorList);
    }

    public interface ToggleListener {
        void onToggleClicked(View v, int position, boolean checked);

        void onItemLongClick(View v, int position);
    }





    @Override
    public void onBindViewHolder(SensorViewHolder sensorViewHolder, int position) {


        SensorInfo sensorInfo = sensorList.get(position);
        sensorViewHolder.itemView.setTag(R.string.key, sensorInfo.getId());

        sensorViewHolder.vTitle.setText(sensorInfo.getName());


       /* if (sensorInfo.deployed) {
            sensorViewHolder.vTitle.setSelected(true);
            sensorViewHolder.vTitle.setChecked(true);
        } else {

            sensorViewHolder.vTitle.setSelected(false);
            sensorViewHolder.vTitle.setChecked(false);
        }*/



        sensorViewHolder.vTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean toggleButtonState = sensorViewHolder.vTitle.isChecked();
                // final String urlSensor = url+"/api/deploy/sensor/" + sensor.getGeneratesensorid();


                if (toggleButtonState == true) {
                    sensorInfo.deployed = false;
                    //sensorViewHolder.vTitle.setTextColor(Color.BLACK);
                    sensorViewHolder.itemView.setBackgroundColor(Color.BLUE);

                    notifyDataSetChanged();




                } else if (toggleButtonState == false) {
                    sensorInfo.deployed = true;
                    //sensorViewHolder.vTitle.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
                    sensorViewHolder.itemView.setBackgroundColor(Color.RED);

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
                    sensorInfo.deployed = false;
                    //notifyDataSetChanged();
                }
            }
        });


    }





    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return sensorList.size();
    }


    //every time we make a change to our database we need to swap the cursor
    public void swapCursor(Cursor newCursor) {
        // Always close the previous mCursor first
        if (mCursor != null) mCursor.close();
        mCursor = newCursor;
        if (newCursor != null) {
            // Force the RecyclerView to refresh
            this.notifyDataSetChanged();
        }
    }

    @Override
    public SensorViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.devicefinder_sensor_list_item, viewGroup, false);

        return new SensorViewHolder(itemView);
    }


    public static class SensorViewHolder extends RecyclerView.ViewHolder {
        public RecyclerView view;

        protected CheckedTextView vTitle;

        public SensorViewHolder(View v) {
            super(v);
            vTitle = (CheckedTextView) v.findViewById(R.id.title);
            view = (RecyclerView) v.findViewById(R.id.devicefinder_recyclerview_sensoren);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedItems.put(getAdapterPosition(),true);
                    view.getAdapter().notifyDataSetChanged();
                }
            });
        }



    }
}


