package com.example.sedaulusal.hiwijob.device.advertise;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import com.example.sedaulusal.hiwijob.R;
import com.example.sedaulusal.hiwijob.device.SensorInfo;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by SQ-OGBE PC on 06/08/2017.
 */

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private ArrayList<SensorInfo> items = new ArrayList<>();
    SparseBooleanArray itemStateArray= new SparseBooleanArray();
    Adapter(ArrayList sensorList) {
        this.items = sensorList;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutForItem = R.layout.devicefinder_sensor_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutForItem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(position);
        SensorInfo sensorInfo = items.get(position);
        holder.itemView.setTag(R.string.key, sensorInfo.getId());

        holder.mCheckedTextView.setText(sensorInfo.getName());

    }

    @Override
    public int getItemCount() {
        if (items == null) {
            return 0;
        }
        return items.size();
    }

    void loadItems(ArrayList<SensorInfo> tournaments) {
        this.items = tournaments;
        notifyDataSetChanged();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CheckedTextView mCheckedTextView;

        ViewHolder(View itemView) {
            super(itemView);
            mCheckedTextView = (CheckedTextView) itemView.findViewById(R.id.title);
            itemView.setOnClickListener(this);
        }

        void bind(int position) {
            // use the sparse boolean array to check
            if (!itemStateArray.get(position, false)) {
                mCheckedTextView.setChecked(false);
            }
            else {
                mCheckedTextView.setChecked(true);
            }
            mCheckedTextView.setText(String.valueOf(items.get(position).getId()));
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            if (!itemStateArray.get(adapterPosition, false)) {
                mCheckedTextView.setChecked(true);
                //items.get(adapterPosition).getName();
                items.get(adapterPosition).setDeployed(true);
                itemStateArray.put(adapterPosition, true);
            }
            else  {
                mCheckedTextView.setChecked(false);
                itemStateArray.put(adapterPosition, false);

            }
        }

    }
}
