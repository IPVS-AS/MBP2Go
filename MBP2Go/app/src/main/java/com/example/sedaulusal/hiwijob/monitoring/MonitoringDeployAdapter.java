package com.example.sedaulusal.hiwijob.monitoring;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ekalips.fancybuttonproj.FancyButton;
import com.example.sedaulusal.hiwijob.R;

import java.util.ArrayList;

/**
 * Created by sedaulusal on 18.05.17.
 */
public class MonitoringDeployAdapter extends RecyclerView.Adapter<MonitoringDeployAdapter.DeviceViewHolder>  {


    private ArrayList<MonitoringAdapters> monitoringAdaptersList;
    private ArrayList<MonitoringAdapters> arraylist;

    private Cursor mCursor;
    private Context mContext;



    public MonitoringDeployAdapter(ArrayList<MonitoringAdapters> monitoringAdaptersList, Cursor mCursor, Context mContext) {
        this.mCursor = mCursor;
        this.mContext = mContext;
        this.monitoringAdaptersList = monitoringAdaptersList;
        this.arraylist = new ArrayList<MonitoringAdapters>();
        this.arraylist.addAll(monitoringAdaptersList);

    }


    public interface ToggleListener {
        void onToggleClicked(View v, int position, boolean checked);

        void onItemLongClick(View v, int position);
    }





    @Override
    public void onBindViewHolder(DeviceViewHolder deviceViewHolder, int i) {


        MonitoringAdapters de = monitoringAdaptersList.get(i);
        //deviceViewHolder.itemView.setTag(R.string.key, de.getId());

        deviceViewHolder.vTitle.setText(de.getName());
        deviceViewHolder.vState.setText(de.getState());
        deviceViewHolder.vUnit.setText(de.getUnit());
        deviceViewHolder.vDeployButton.expand();



    }





    /*
    wenn icons rund seinen sollen
     */
    public static Bitmap getBitmapClippedCircle(Bitmap bitmap) {

        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();
        final Bitmap outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        final Path path = new Path();
        path.addCircle(
                (float)(width / 2)
                , (float)(height / 2)
                , (float) Math.min(width, (height / 2))
                , Path.Direction.CCW);

        final Canvas canvas = new Canvas(outputBitmap);
        canvas.clipPath(path);
        canvas.drawBitmap(bitmap, 0, 0, null);
        return outputBitmap;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return monitoringAdaptersList.size();
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
    public DeviceViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.monitoring_deploy_item, viewGroup, false);

        return new DeviceViewHolder(itemView);
    }


    public static class DeviceViewHolder extends RecyclerView.ViewHolder {

        protected TextView vTitle;
        protected TextView vState, vUnit;
        protected ProgressBar vProcessbar;
        protected FancyButton vDeployButton;


        public DeviceViewHolder(View v) {
            super(v);

            vTitle = (TextView) v.findViewById(R.id.txtMonitoringName);
            vState = (TextView) v.findViewById(R.id.txtMonitoringState);
            vUnit = (TextView) v.findViewById(R.id.txtMonitoringUnit);
            vDeployButton = (FancyButton) v.findViewById(R.id.btn_monitoringdeploy);
        }


    }
}


