package com.example.sedaulusal.hiwijob.ruleEngine;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.sedaulusal.hiwijob.R;
import com.example.sedaulusal.hiwijob.device.ActuatorAdapter;
import com.example.sedaulusal.hiwijob.device.ActuatorInfo;
import com.example.sedaulusal.hiwijob.device.DeviceOverviewActivity;
import com.example.sedaulusal.hiwijob.device.DeviceRegistryActivity;
import com.example.sedaulusal.hiwijob.device.RecyclerTouchListener;
import com.example.sedaulusal.hiwijob.device.SQLiteHelper;
import com.example.sedaulusal.hiwijob.device.SensorInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

public class RuleEngineActivity extends AppCompatActivity {

    RecyclerView recyclerView_when, recyclerView_action;
    RecyclerView.Adapter mAdapter, actuatorAdapter, dialogAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.LayoutManager mLayoutManagerActuator;

    ArrayList<SensorInfo> currentSensorlist, sensorlist;
    ArrayList<ActuatorInfo> currentActuatorlist, actuatorlist;
    Context context;
    Cursor cursor;
    SensorInfo sensorInfo;
    ActuatorInfo actuatorInfo;

    int position_stelle, position_stelle_actuator;
    RuleEngineSensorInfo ruleEngineSensorInfo;
    RuleEngineSensorInfo ruleEngineSensorInfori;

    SQLiteHelper sqLiteHelper;

    ArrayList<RuleEngineSensorInfo> currentRuleSensorInfolist;
    ArrayList<RuleEngineActuatorInfo> currentRuleActuatorInfolist;

    //Liste von Listen
    ArrayList<ArrayList<RuleEngineSensorInfo>> listOfSensorInfoList;

    EditText ruleEngine_rulename;

    RuleEngineActuatorInfo ruleEngineActuatorInfo;

    ////For Layout inflater
    LinearLayout linearLayout;
    LayoutInflater inflater;
    View view_when;

    private RecyclerView mRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rule_engine);


        context = this;
        currentSensorlist = new ArrayList<>();

        //Liste von Listen
        listOfSensorInfoList = new ArrayList<>();

        currentRuleSensorInfolist = new ArrayList<>();
        currentRuleActuatorInfolist = new ArrayList<>();
        actuatorlist = new ArrayList<>();


        sqLiteHelper = new SQLiteHelper(context);

        ruleEngine_rulename = (EditText) findViewById(R.id.ruleEngine_edtName2);

        //recyclerView_when = (RecyclerView) findViewById(R.id.ruleEngine_recyclerView_when);

        recyclerView_action = (RecyclerView) findViewById(R.id.ruleEngine_recyclerView_action);


        /////Layout Inflater////

        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        linearLayout = (LinearLayout) findViewById(R.id.rule_engine_layout_for_whenview);

        mLayoutManager = new LinearLayoutManager(this);

        mLayoutManagerActuator = new LinearLayoutManager(this);
        recyclerView_action.setLayoutManager(mLayoutManagerActuator);


        //currentActuatorlist = getArrayListActuator("actuatorlist");

        //if (currentActuatorlist != null) {
        actuatorAdapter = new RuleEngineRuleActuatorAdapter(currentRuleActuatorInfolist, cursor, context);
        recyclerView_action.setAdapter(actuatorAdapter);
        //actuatorAdapter.notifyDataSetChanged();
        //}


        //TODO Action!!
        recyclerView_action.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView_action, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                position_stelle_actuator = position;

                if (currentRuleActuatorInfolist.get(position).getRuleactuatorname().equals("Notification")) {
                    Intent intent = new Intent(RuleEngineActivity.this, RuleEngingeActuatorSettingsActivity.class);

                    intent.putExtra("ruleengine_rulesensorname", currentRuleActuatorInfolist.get(position).getRuleactuatorname());
                    intent.putExtra("ruleengine_rulesensorgenerateid", currentRuleActuatorInfolist.get(position).getRuleactuatorgenerateid());
                    ruleEngineActuatorInfo =currentRuleActuatorInfolist.get(position);
                    // Bundle bundle = new Bundle();
                    // intent.putExtras(bundle);
                    //startActivity(intent);
                    startActivityForResult(intent, 2);

                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        this.addORSensorLayout(true);

    }


    @Override
    protected void onResume() {
        //getCurrentSensorlist();
        super.onResume();
    }

    @Override
    protected void onPause() {
        // getCurrentSensorlist();
        super.onPause();
    }


    //TODO
    public void btn_RuleEngine_addAction(View v) {

        addActionItem();
        //Intent intent = new Intent(RuleEngineActivity.this, SelectActionActivity.class);
        //startActivity(intent);
    }

  /*  public ArrayList<SensorInfo> getArrayList(String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<SensorInfo>>() {
        }.getType();
        return gson.fromJson(json, type);
    }
*/


    public void removeArrayList() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear().clear();
        editor.apply();     // This line is IMPORTANT !!!
    }

    @Override
    public void onBackPressed() {
        removeArrayList();

        Intent intent = new Intent(RuleEngineActivity.this, RuleEngineOverviewActivity.class);
        startActivity(intent);
    }


    public ArrayList<ActuatorInfo> getArrayListActuator(String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<ActuatorInfo>>() {
        }.getType();
        return gson.fromJson(json, type);
    }


    public void ruleEngine_SensorSetting_onClick(View v) {
        Intent intent = new Intent(RuleEngineActivity.this, RuleEngingeSensorSettingsActivity.class);

        //intent.putExtra("ruleengine_rulesensorname", c.get(position_stelle).getName());
        // intent.putExtra("ruleengine_rulesensorgenerateid", currentSensorlist.get(position_stelle).getGeneratesensorid());
        // Bundle bundle = new Bundle();
        // intent.putExtras(bundle);
        startActivity(intent);
    }


    public void ruleEngine_ActuatorSetting_onClick(View vhubhjb) {
        Intent intent = new Intent(RuleEngineActivity.this, RuleEngingeActuatorSettingsActivity.class);

        intent.putExtra("ruleengine_ruleactuatorname", currentActuatorlist.get(position_stelle).getName());
        intent.putExtra("ruleengine_ruleactuatorgenerateid", currentActuatorlist.get(position_stelle).getGenerateActuatorId());
        Bundle bundle = new Bundle();
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void ruleengine_OR_Button(View v) {
        addORSensorLayout(false);
    }

    public void addORSensorLayout(boolean removeOr) {
        view_when = inflater.inflate(R.layout.view_rule_engine_when, null, false);

        if (removeOr) {
            view_when.findViewById(R.id.view_rule_engine_when_text_OR).setVisibility(View.GONE);
        }

        linearLayout.addView(view_when);

        RecyclerView rv = (RecyclerView) view_when.findViewById(R.id.ruleEngine_recyclerView_when_or);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);

        //ArrayList<SensorInfo> csl = new ArrayList<>();
        ArrayList<RuleEngineSensorInfo> csl = new ArrayList<>();
        listOfSensorInfoList.add(csl);

        //RuleEngineSensorAdapter resa = new RuleEngineSensorAdapter(csl, cursor, context);
        RuleEngineAdapterForRuleSensorInfo resa = new RuleEngineAdapterForRuleSensorInfo(csl, cursor, context);
        rv.setAdapter(resa);

        Button viewadd = (Button) view_when.findViewById(R.id.ruleEngine_and);

        viewadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ////////////////DIALOG FUNKTIONERT////////////
                // Created a new Dialog
                Dialog dialog = new Dialog(RuleEngineActivity.this);

                // Set the title
                dialog.setTitle("Sensors");

                // inflate the layout
                dialog.setContentView(R.layout.activity_select_thing);

                // Set the dialog text -- this is better done in the XML
                //TextView text = (TextView)dialog.findViewById(R.id.dialog_text_view);
                //text.setText("This is the text that does in the dialog box");
                sensorlist = new ArrayList<>();

                cursor = sqLiteHelper.getAllSensorData();
                sensorlist.clear();
                while (cursor.moveToNext()) {

                    //int id = cursor.getInt(cursor.getColumnIndex(SQLiteHelper.KEY_ID));
                    String sensorname = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_SensorNAME));
                    String generatesensorid = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_SensorID));
                    String plattformid = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_PLATTFORMID));
                    byte[] sensorimage = cursor.getBlob(cursor.getColumnIndex(SQLiteHelper.KEY_SensorIMAGE));
                    String sensorPinset = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_SensorPINSET));
                    String sensortype = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_SensorTYP));
                    //String userName = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_USERNAME));
                    //String password = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_PASSWORD));

                    //id gelöscht
                    sensorInfo = new SensorInfo(generatesensorid, sensorname, sensorimage, sensorPinset, sensortype);
                    // Log.d("Hiwi","device info: "+id+" "+name+" "+macid+" "+sensorId);
                    //Log.d("Hiwi", "device info: " + " " + name + " " + macid + " ");

                    sensorlist.add(sensorInfo);
                }
                cursor.close();

                mRecyclerView = (RecyclerView) dialog.findViewById(R.id.ruleEngine_selectthing_recyclerView);


                mLayoutManager = new LinearLayoutManager(context);
                mRecyclerView.setLayoutManager(mLayoutManager);

                // specify an adapter (see also next example)
                dialogAdapter = new RuleEngineSensorAdapter(sensorlist, cursor, context);
                mRecyclerView.setAdapter(dialogAdapter);


                mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), mRecyclerView, new RecyclerTouchListener.ClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        position_stelle = position;
                        Log.d("Position in onClick", "Position ist " + position);

                        // Getting todos under "Watchlist" tag name
                        Log.d("ToDo", "Get todos under single Tag name");


                        //Fehlerbehebung, sonst Array -1 Error
                        if (position <= -1) {
                            //dialog.dismiss();
                            return;
                        }


                        sensorlist.get(position).getName();
                        ruleEngineSensorInfori = new RuleEngineSensorInfo(sensorlist.get(position), sensorlist.get(position).getGeneratesensorid());
                        csl.add(ruleEngineSensorInfori);
                        resa.notifyDataSetChanged();
                        dialog.dismiss();

                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }


                }));


                // Display the dialog
                dialog.show();
                //////////////DIALOG FUNKTIONERT///////////////

            }
        });

        ////////reyclerview new click for setting
        rv.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), rv, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(RuleEngineActivity.this, RuleEngingeSensorSettingsActivity.class);

                intent.putExtra("ruleengine_rulesensorname", csl.get(position).getSensorInfo().getName());
                intent.putExtra("ruleengine_rulesensorgenerateid", csl.get(position).getSensorInfo().getGeneratesensorid());
                ruleEngineSensorInfori = csl.get(position);
                // Bundle bundle = new Bundle();
                // intent.putExtras(bundle);
                //startActivity(intent);
                startActivityForResult(intent, 1);

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


    }


    public void ruleengine_addRule(View v) {
        RuleEngineRuleInfo ruleInfo = new RuleEngineRuleInfo();
        ruleInfo.setRulestatus("aktiv");
        ruleInfo.setRulename(ruleEngine_rulename.getText().toString());

        long ruleid = sqLiteHelper.createRuleInfo(ruleInfo);
        ruleInfo.setRuleengineruleid(ruleid);


        // currentRuleSensorInfolist = getArrayListRuleSensorInfo("rulesensorinfo");
        for (ArrayList<RuleEngineSensorInfo> andlists : listOfSensorInfoList) {
            for (RuleEngineSensorInfo ruleEngineSensorInfo : andlists) {
                long sensorid = sqLiteHelper.createRuleSensor(ruleEngineSensorInfo, ruleInfo, ruleEngineSensorInfo.getRulesensorgenerateid());
                ruleEngineSensorInfo.setRulesensorid(sensorid);

            }

        }

        for(RuleEngineActuatorInfo ruleEngineActuatorInfo:currentRuleActuatorInfolist ){
            long actuatorid= sqLiteHelper.createRuleActuator(ruleEngineActuatorInfo,ruleInfo,ruleEngineActuatorInfo.getRuleactuatorgenerateid());
            ruleEngineActuatorInfo.setRuleactuatorid(actuatorid);
        }


        listOfSensorInfoList.size();
        ruleInfo.setRuleassociation(generateRuleAsociation());
        sqLiteHelper.updateRuleInfo(ruleInfo);


        removeArrayList();

        Intent intent = new Intent(RuleEngineActivity.this, RuleEngineOverviewActivity.class);
        startActivity(intent);
    }

    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.N)
    public String generateRuleAsociation() {
        //String ruleassociation;
        //currentRuleSensorInfolist = getArrayListRuleSensorInfo("rulesensorinfo");
        StringJoiner joiner = new StringJoiner(" AND ");
        ArrayList<String> stringListOfOr = new ArrayList<>();
        String klammerend = "";
        String assosiation = "";

        for (ArrayList<RuleEngineSensorInfo> andlists : listOfSensorInfoList) {
            ArrayList<String> stringlistofandsensorid = new ArrayList<>();

            for (RuleEngineSensorInfo ruleEngineSensorInfo : andlists) {
                //String str = String.join("", andlists);
                //String str1 = listOfSensorInfoList.stream().collect(joining());


                String sensoridand = Long.toString(ruleEngineSensorInfo.getRulesensorid());
                //joiner.add(sensoridand);
                stringlistofandsensorid.add(sensoridand);
                //long sensorid = sqLiteHelper.createRuleSensor(ruleEngineSensorInfo, ruleInfo, ruleEngineSensorInfo.getRulesensorgenerateid());
                //ruleEngineSensorInfo.setRulesensorid(sensorid);

            }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                String andSensorids = String.join(" AND ", stringlistofandsensorid);
                String klammerbeginning = "( " + andSensorids;
                klammerend = klammerbeginning + " )";


            } else {
                Toast.makeText(getApplicationContext(), "Error wrong Version SDK need max 26!", Toast.LENGTH_SHORT).show();

            }
            stringListOfOr.add(klammerend);
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            assosiation = String.join(" OR ", stringListOfOr);

        } else {
            Toast.makeText(getApplicationContext(), "Error wrong Version SDK need max 26!", Toast.LENGTH_SHORT).show();
        }

        return assosiation;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                //String result=data.getStringExtra("result");
                String ruleengine_fromsetting_rulesensorname = data.getStringExtra("ruleengine_fromsetting_rulesensorname");
                String ruleengine_fromsetting_rulesensorgenerateid = data.getStringExtra("ruleengine_fromsetting_rulesensorgenerateid");
                String ruleengine_fromsetting_value = data.getStringExtra("ruleengine_fromsetting_value");
                String ruleengine_fromsetting_operator = data.getStringExtra("ruleengine_fromsetting_operator");

                ruleEngineSensorInfori.setRuleopeator(ruleengine_fromsetting_operator);
                ruleEngineSensorInfori.setRulevalue(ruleengine_fromsetting_value);
                listOfSensorInfoList.size();
                String t;


            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }

        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                //String result=data.getStringExtra("result");
                String ruleengine_fromsetting_value = data.getStringExtra("ruleengine_fromsetting_value_actuator");
                ruleEngineActuatorInfo.setRulevalue(ruleengine_fromsetting_value);


            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult

    public void addActionItem() {

        ////////////////DIALOG FUNKTIONERT////////////
        // Created a new Dialog
        Dialog dialog = new Dialog(RuleEngineActivity.this);

        // Set the title
        dialog.setTitle("Sensors");

        // inflate the layout
        dialog.setContentView(R.layout.activity_select_action);

        // Set the dialog text -- this is better done in the XML
        //TextView text = (TextView)dialog.findViewById(R.id.dialog_text_view);
        //text.setText("This is the text that does in the dialog box");
        //sensorlist = new ArrayList<>();

        cursor = sqLiteHelper.getAllActuatorData();
        actuatorlist.clear();

        String actuatornameNotification = "Notification";
        String actuatornamegenerateid = "Notification";
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.baseline_notifications_active_black_48);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] actuatorimageNotification = stream.toByteArray();


        actuatorInfo = new ActuatorInfo(actuatornamegenerateid, actuatornameNotification, actuatorimageNotification);
        actuatorlist.add(actuatorInfo);

        while (cursor.moveToNext()) {

            //int id = cursor.getInt(cursor.getColumnIndex(SQLiteHelper.KEY_ID));
            String actuatorname = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_ActuatorNAME));
            String generateactuatorid = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_ActuatorID));
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

        mRecyclerView = (RecyclerView) dialog.findViewById(R.id.ruleEngine_selectaction_recyclerView);


        mLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        dialogAdapter = new RuleEngineAcuatorAdapter(actuatorlist, cursor, context);
        mRecyclerView.setAdapter(dialogAdapter);


        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), mRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                position_stelle = position;
                Log.d("Position in onClick", "Position ist " + position);

                // Getting todos under "Watchlist" tag name
                Log.d("ToDo", "Get todos under single Tag name");


                //Fehlerbehebung, sonst Array -1 Error
                if (position <= -1) {
                    //dialog.dismiss();
                    return;
                }


                actuatorlist.get(position).getName();
                ruleEngineActuatorInfo = new RuleEngineActuatorInfo(actuatorlist.get(position).getGenerateActuatorId(), actuatorlist.get(position).getName(), actuatorlist.get(position));
                //ruleEngineSensorInfori = new RuleEngineSensorInfo(sensorlist.get(position),sensorlist.get(position).getGeneratesensorid());
                currentRuleActuatorInfolist.add(ruleEngineActuatorInfo);
                actuatorAdapter.notifyDataSetChanged();
                dialog.dismiss();

            }

            @Override
            public void onLongClick(View view, int position) {

            }


        }));


        // Display the dialog
        dialog.show();
        //////////////DIALOG FUNKTIONERT///////////////

    }

}

