package com.example.sedaulusal.hiwijob.ruleEngine;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sedaulusal.hiwijob.R;
import com.example.sedaulusal.hiwijob.device.ActuatorInfo;

import java.util.ArrayList;

/**
 * Created by sedaulusal on 18.05.17.
 */
public class RuleEngineRuleActuatorAdapter extends RecyclerView.Adapter<RuleEngineRuleActuatorAdapter.ActuatorViewHolder> {


    private ArrayList<RuleEngineActuatorInfo> actuatorList;
    private ArrayList<RuleEngineActuatorInfo> arraylist;

    private Cursor mCursor;
    private Context mContext;

    public RuleEngineRuleActuatorAdapter(ArrayList<RuleEngineActuatorInfo> actuatorList, Cursor mCursor, Context mContext) {
        this.mCursor = mCursor;
        this.mContext = mContext;
        this.actuatorList = actuatorList;
        this.arraylist = new ArrayList<>();
        this.arraylist.addAll(actuatorList);
    }

    public interface ToggleListener {
        void onToggleClicked(View v, int position, boolean checked);

        void onItemLongClick(View v, int position);
    }





    @Override
    public void onBindViewHolder(ActuatorViewHolder actuatorViewHolder, int i) {


        RuleEngineActuatorInfo de = actuatorList.get(i);
        actuatorViewHolder.itemView.setTag(R.string.key, de.getRuleactuatorid());

        actuatorViewHolder.vTitle.setText(de.getRuleactuatorname());
        byte[] iconImage = de.getRuleActuatorInfo().getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(iconImage, 0, iconImage.length);
        actuatorViewHolder.vIcon.setImageBitmap(bitmap);


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
        return actuatorList.size();
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
    public ActuatorViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.ruleengine_actuator_list_item, viewGroup, false);

        return new ActuatorViewHolder(itemView);
    }


    public static class ActuatorViewHolder extends RecyclerView.ViewHolder {

        protected TextView vTitle;
        protected ImageView vIcon;

        public ActuatorViewHolder(View v) {
            super(v);

            vTitle = (TextView) v.findViewById(R.id.title);
            vIcon = (ImageView) v.findViewById(R.id.imageIcon);

        }


    }
}


