package com.example.sedaulusal.hiwijob.device;

import android.content.Context;
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

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.sedaulusal.hiwijob.R;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by sedaulusal on 21.06.17.
 */

public class SensorAdapter extends BaseAdapter {

    private Context context;
    private  int layout;
    private ArrayList<SensorInfo> sensorList;

    public SensorAdapter(Context context, int layout, ArrayList<SensorInfo> foodsList) {
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

    private class ViewHolder{
        ImageView SensorimageView;
        TextView txtSensorName;
        TextView txtSensorPinset;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        View row = view;
        ViewHolder holder = new ViewHolder();

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);

            holder.txtSensorName = (TextView) row.findViewById(R.id.txtSensorName);
            holder.txtSensorPinset = (TextView) row.findViewById(R.id.txtSensorId);
            holder.SensorimageView = (ImageView) row.findViewById(R.id.imgSensor);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        final SensorInfo sensor = sensorList.get(position);

        //TO DO: set name from spinner and hardcoded Imagins
        holder.txtSensorName.setText(sensor.getName());
        holder.txtSensorPinset.setText(sensor.getSensorPinset());

        byte[] sensorImage = sensor.getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(sensorImage, 0, sensorImage.length);
        holder.SensorimageView.setImageBitmap(bitmap);


        return row;
    }
}
