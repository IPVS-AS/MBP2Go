package com.example.sedaulusal.hiwijob.ruleEngine;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.sedaulusal.hiwijob.R;
import com.example.sedaulusal.hiwijob.device.ActuatorInfo;
import com.example.sedaulusal.hiwijob.device.DeviceRegistryActivity;
import com.example.sedaulusal.hiwijob.device.RecyclerTouchListener;
import com.example.sedaulusal.hiwijob.device.SQLiteHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class SelectActionActivity extends AppCompatActivity {


    private RecyclerView mRecyclerView;
    ArrayList<ActuatorInfo> actuatorlist, currentactuatorlist;
    ArrayList<ActuatorInfo> actuator, actuaor1;


    //RecyclerView.Adapter mAdapter;
    RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    SQLiteHelper db;
    Context context;
    ActuatorInfo actuatorInfo;
    //DeviceInfo deviceInfo;
    Cursor cursor;
    EditText versuch;
    int position_stelle;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_action);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);




        context = this;
        db = new SQLiteHelper(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.ruleEngine_selectaction_recyclerView);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        actuatorlist = new ArrayList<>();
        actuator  = new ArrayList<>();


        // specify an adapter (see also next example)
        mAdapter = new RuleEngineAcuatorAdapter( actuatorlist, cursor, context);
        mRecyclerView.setAdapter(mAdapter);

        actuaor1  = new ArrayList<>();

        actuaor1 = getArrayListActuator("actuatorlist");


        cursor = db.getAllActuatorData();
        actuatorlist.clear();

        String actuatornameNotification = "Notification";
        String actuatornamegenerateid = "Notification";
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_slide3);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[]  actuatorimageNotification = stream.toByteArray();


        actuatorInfo = new ActuatorInfo(actuatornamegenerateid, actuatornameNotification,actuatorimageNotification );
        actuatorlist.add(actuatorInfo);

        while (cursor.moveToNext()) {

            //int id = cursor.getInt(cursor.getColumnIndex(SQLiteHelper.KEY_ID));
            String actuatorname = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_ActuatorNAME));
            String  generateactuatorid= cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_ActuatorID));
            String plattformid = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_PLATTFORMID));
            byte[] actuatorimage = cursor.getBlob(cursor.getColumnIndex(SQLiteHelper.KEY_ActuatorIMAGE));
            String actuatorPinset = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_ActuatorPINSET));
            String actuatortype = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_ActuatorTYP));
            //String userName = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_USERNAME));
            //String password = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_PASSWORD));

            //id gelöscht
            actuatorInfo = new ActuatorInfo(generateactuatorid, actuatorname, actuatorimage, actuatorPinset, actuatortype);
            // Log.d("Hiwi","device info: "+id+" "+name+" "+macid+" "+sensorId);
            //Log.d("Hiwi", "device info: " + " " + name + " " + macid + " ");

            actuatorlist.add(actuatorInfo);
        }
        cursor.close();



        mAdapter.notifyDataSetChanged();


        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), mRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                position_stelle = position;
                Log.d("Position in onClick", "Position ist " + position);

                // Getting todos under "Watchlist" tag name
                Log.d("ToDo", "Get todos under single Tag name");


                //Fehlerbehebung, sonst Array -1 Error
                if (position <= -1){
                    //dialog.dismiss();
                    return;
                }

                ActuatorInfo actuatorInfo = actuatorlist.get(position);

                Intent intent = new Intent(SelectActionActivity.this, RuleEngineActivity.class);
                actuatorlist.get(position).getName();




                actuator.add(actuatorInfo);
                if(actuaor1!=null){
                    for(ActuatorInfo actuatorInfo1: actuaor1){
                        //if(!test.contains(sensorInfo))
                        actuator.add(actuatorInfo1);
                    }
                }
                //tinydb.putListObject("sesnor", test);
                saveArrayListActuator(actuator, "actuatorlist");
                intent.putExtra("deviceid", actuatorInfo.getName());

                Bundle bundle = new Bundle();
                intent.putExtras(bundle);


                //Intent intent = new Intent(SelectThingActivity.this, DeviceRegistryActivity.class);
                actuatorlist.get(position).getName();

                startActivity(intent);
            }


            @Override
            public void onLongClick(View view, final int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Options")
                        .setItems(R.array.action_dialog_sensors, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        //TODO edit saveURL the item
                                        versuch = (EditText) findViewById(R.id.edtName);

                                        String s = null;

                                        Intent intent = new Intent(SelectActionActivity.this, DeviceRegistryActivity.class);
                                        actuatorlist.get(position).getName();
                                        //Intent intent = new Intent(Current.class, Transfer.class);
                                        //Bundle args = new Bundle();
                                        //args.putSerializable("ARRAYLIST",(Serializable)test);
                                        // intent.putExtra("BUNDLE", test);
                                        //intent.putExtra()
                                        //intent.putExtra("name", devicelist.get(position).getName());

                                        // Bundle extra = new Bundle();
                                        //extra.putSerializable("objects", test);
                                        // intent.putExtra("exa", (Parcelable) sensorlist.get(position));
                                        //Intent intent = new Intent(getBaseContext(), ShowSpread.class);
                                        //intent.putExtra("extra", test);



                                        startActivity(intent);
                                        break;
                                    case 1:

                                        mAdapter.notifyItemRemoved(position);


                                        break;
                                }
                            }
                        }).create().show();

            }
        }));

    }

    public void btn_RuleEngine_addAction(View v){

        Intent intent = new Intent(SelectActionActivity.this, RuleEngineActivity.class);
        intent.putExtra("selectaction_notification", true);

        startActivity(intent);
    }


    public void saveArrayListActuator(ArrayList<ActuatorInfo> list, String key){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();     // This line is IMPORTANT !!!
    }

    public ArrayList<ActuatorInfo> getArrayListActuator(String key){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<ActuatorInfo>>() {}.getType();
        return gson.fromJson(json, type);
    }


}
