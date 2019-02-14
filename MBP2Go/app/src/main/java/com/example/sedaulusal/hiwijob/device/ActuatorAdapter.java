package com.example.sedaulusal.hiwijob.device;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sedaulusal.hiwijob.R;

import java.util.ArrayList;

/**
 * Created by sedaulusal on 25.10.17.
 */

public class ActuatorAdapter extends BaseAdapter {

    private Context context;
    private  int layout;
    private ArrayList<ActuatorInfo> actuatorList;

    public ActuatorAdapter(Context context, int layout, ArrayList<ActuatorInfo> actuatorList) {
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
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = new ViewHolder();

        if(row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);

            holder.txtActuatorName = (TextView) row.findViewById(R.id.txtAktuatorName);
            holder.txtActuatorId = (TextView) row.findViewById(R.id.txtActuatorId);
            holder.ActuatorimageView = (ImageView) row.findViewById(R.id.imgAktuator);
            row.setTag(holder);
        }
        else {
            holder = (ViewHolder) row.getTag();
        }

        ActuatorInfo actuator = actuatorList.get(position);

        //TO DO: set name from spinner and hardcoded Imagins
        holder.txtActuatorName.setText(actuator.getName());
        holder.txtActuatorId.setText(actuator.getActuatorPinset());

        byte[] actuatorImage = actuator.getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(actuatorImage, 0, actuatorImage.length);
        holder.ActuatorimageView.setImageBitmap(bitmap);

        return row;
    }
}