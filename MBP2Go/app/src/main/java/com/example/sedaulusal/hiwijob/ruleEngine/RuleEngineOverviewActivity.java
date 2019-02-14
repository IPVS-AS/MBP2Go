package com.example.sedaulusal.hiwijob.ruleEngine;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sedaulusal.hiwijob.MainActivity;
import com.example.sedaulusal.hiwijob.R;
import com.example.sedaulusal.hiwijob.device.ActuatorInfo;
import com.example.sedaulusal.hiwijob.device.DeviceInfo;
import com.example.sedaulusal.hiwijob.device.DeviceOverviewActivity;
import com.example.sedaulusal.hiwijob.device.DeviceRegistryActivity;
import com.example.sedaulusal.hiwijob.device.MyAdapter;
import com.example.sedaulusal.hiwijob.device.RecyclerTouchListener;
import com.example.sedaulusal.hiwijob.device.SQLiteHelper;
import com.example.sedaulusal.hiwijob.device.SensorInfo;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.highsoft.highcharts.Common.HIChartsClasses.HIOptions;
import com.highsoft.highcharts.Common.HIChartsClasses.HISpline;
import com.highsoft.highcharts.Common.HIChartsClasses.HIXAxis;
import com.highsoft.highcharts.Core.HIChartView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RuleEngineOverviewActivity extends AppCompatActivity {

    //Eclipse code
    static boolean b;
    static ArrayList<Boolean> bo;


    RecyclerView recyclerView;
    RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.LayoutManager mLayoutManagerActuator;

    ArrayList<SensorInfo> currentSensorlist;
    ArrayList<ActuatorInfo> currentActuatorlist;
    ArrayList<String> jsonSensors;

    Context context;
    Cursor cursor;
    SensorInfo sensorInfo;
    int position_stelle;
    RuleEngineSensorInfo ruleEngineSensorInfo;
    RuleEngineActuatorInfo ruleEngineActuatorInfo;

    RuleEngineRuleInfo ruleInfo;

    SQLiteHelper sqLiteHelper;

    ArrayList<RuleEngineSensorInfo> currentRuleSensorInfolist;
    ArrayList<RuleEngineActuatorInfo> currentRuleActuatorInfolist;
    ArrayList<RuleEngineRuleInfo> ruleInfoList;

    ArrayList<Boolean> testlisteboolean;

    String asso;
    int numberrequest;

    ////////////////
    String value = "0";
    ArrayList<Double> listofsensorvaluesfromdb;

    /////////Notification
    private NotificationCompat.Builder notBuilder;

    private static final int MY_NOTIFICATION_ID = 12345;

    private static final int MY_REQUEST_CODE = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rule_engine_overview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //       .setAction("Action", null).show();
                Intent intent = new Intent(RuleEngineOverviewActivity.this, RuleEngineActivity.class);
                startActivity(intent);
            }
        });

        deleteSharendPref("actuatorlist");
        deleteSharendPref("testsens");

        context = this;
        currentSensorlist = new ArrayList<>();
        currentRuleSensorInfolist = new ArrayList<>();
        currentRuleActuatorInfolist = new ArrayList<>();
        testlisteboolean = new ArrayList<Boolean>();


        jsonSensors = new ArrayList<>();
        sqLiteHelper = new SQLiteHelper(this);

        listofsensorvaluesfromdb = new ArrayList<>();

        ruleInfoList = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.ruleengineoverview_recyclerview);


        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new RuleEngineAdapterOverview_RuleInfo(ruleInfoList, cursor, context);
        recyclerView.setAdapter(mAdapter);

        cursor = sqLiteHelper.getAllDataRuleInfo();
        ruleInfoList.clear();
        while (cursor.moveToNext()) {

            int ruleid = cursor.getInt(cursor.getColumnIndex(SQLiteHelper.KEY_RULE_ID));
            String rulename = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_RULE_NAME));
            String status = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_RULE_STATUS));
            String association = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_RULE_ASSOCIATION));
            byte[] image = cursor.getBlob(cursor.getColumnIndex(SQLiteHelper.KEY_RULE_IMAGE));


            //id gelöscht
            //int ruleengineruleid,String rulename,String rulestatus,byte[] ruleImage,String ruleassociation
            ruleInfo = new RuleEngineRuleInfo(ruleid, rulename, status, image, association);
            // Log.d("Hiwi","device info: "+id+" "+name+" "+macid+" "+sensorId);
            //Log.d("Hiwi", "device info: " + " " + name + " " + macid + " ");
            ruleInfoList.add(ruleInfo);
        }
        cursor.close();


        mAdapter = new RuleEngineAdapterOverview_RuleInfo(ruleInfoList, cursor, context);
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();


        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                position_stelle = position;


                //ruleEngineSensorInfo = sqLiteHelper.getRuleSensorInfoRuleID(ruleInfoList.get(position).getRuleengineruleid());
                //ruleEngineSensorInfo.getRulesensorname();

                //sqLiteHelper.onUpgrade(, 1, 2);
                cursor = sqLiteHelper.getAllCursorRuleInfoSensor(ruleInfoList.get(position).getRuleengineruleid());
                jsonSensors.clear();
                while (cursor.moveToNext()) {


                    int ruleid = cursor.getInt(cursor.getColumnIndex(SQLiteHelper.KEY_RULE_ID));
                    String sensoridgenerate = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_SensorID));
                    long rulesensorid = cursor.getLong(cursor.getColumnIndex(SQLiteHelper.KEY_RULE_SensorID));
                    String operation = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_RULE_Sensoroperation));
                    String value = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_RULE_Sensorvalue));

                    ruleEngineSensorInfo = new RuleEngineSensorInfo(rulesensorid, sensoridgenerate, operation, value);
                    ruleEngineSensorInfo.getRulevalue();

                    currentRuleSensorInfolist.add(ruleEngineSensorInfo);

                    Gson gson = new Gson();

                    String json = gson.toJson(ruleEngineSensorInfo);
                    jsonSensors.add(json);


                    //json.contains("ruleid");
                    //id gelöscht
                    //int ruleengineruleid,String rulename,String rulestatus,byte[] ruleImage,String ruleassociation
                    //RuleEngineSensorInfo ruleEngineSensorInfo = new RuleEngineSensorInfo(ruleid, rulename, status, image, association);
                    // Log.d("Hiwi","device info: "+id+" "+name+" "+macid+" "+sensorId);
                    //Log.d("Hiwi", "device info: " + " " + name + " " + macid + " ");
                    //ruleInfoList.add(ruleInfo);

                    /*
                    String message;
                    JSONObject json = new JSONObject();
                    json.put("name", "student");

                    JSONArray array = new JSONArray();
                    JSONObject item = new JSONObject();
                    item.put("information", "test");
                    item.put("id", 3);
                    item.put("name", "course1");
                    array.add(item);

                    json.put("course", array);*/

                    //message = json.toString();
                }
                cursor.close();

                String ruleassosiationpartwithoutRest = ruleInfoList.get(position).getRuleassociation();
                String ruleassosiationpartwithoutRest1 = ruleInfoList.get(position).getRuleassociation();
                String s;
                String rule;

                numberrequest = countIntegers(ruleassosiationpartwithoutRest);
                asso = "";
                for (RuleEngineSensorInfo ruleEngineSensorInfo : currentRuleSensorInfolist) {
                    if (ruleassosiationpartwithoutRest1.contains(Long.toString(ruleEngineSensorInfo.getRulesensorid()))) {
                        ruleassosiationpartwithoutRest = ruleassosiationpartwithoutRest.replace(Long.toString(ruleEngineSensorInfo.getRulesensorid()), Long.toString(ruleEngineSensorInfo.getRulesensorid()) + " " + ruleEngineSensorInfo.getRuleopeator() + " " + ruleEngineSensorInfo.getRulevalue());
                        //ruleassosiationpartwithoutRest.length();
                        asso = ruleassosiationpartwithoutRest;
                        rule = getValuesfromSensor(ruleEngineSensorInfo.getRulesensorgenerateid(), ruleEngineSensorInfo);

                    }
                }
                currentRuleSensorInfolist.clear();

                //asso.length();


                //////////Actuator////////////

               cursor = sqLiteHelper.getAllCursorRuleInfoActuator(ruleInfoList.get(position).getRuleengineruleid());
                //cursor =sqLiteHelper.getAllDataRuleActuatorInfo();
                //jsonSensors.clear();
                while (cursor.moveToNext()) {


                    int ruleid = cursor.getInt(cursor.getColumnIndex(SQLiteHelper.KEY_RULE_ID));
                    String actuatoridgenerate = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_ActuatorID));
                    long ruleactuatorid = cursor.getLong(cursor.getColumnIndex(SQLiteHelper.KEY_RULE_ActuatorID));
                    //String operation= cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_RULE_Sensoroperation));
                    String value = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_RULE_Actuatorvalue));

                    ruleEngineActuatorInfo = new RuleEngineActuatorInfo(ruleactuatorid, actuatoridgenerate, value);
                    //ruleEngineActuatorInfo.getRulevalue();

                    currentRuleActuatorInfolist.add(ruleEngineActuatorInfo);

                    Gson gson = new Gson();

                    //String json = gson.toJson(ruleEngineSensorInfo);
                    //jsonSensors.add(json);


                }
                cursor.close();

                //////////////////////

                StringJoiner joiner = new StringJoiner(", ");
                for (String jsons : jsonSensors) {
                    //String name = ruleEngineSensorInfo.getRulesensorname();
                    joiner.add(jsons);
                    //joiner.add("bar");
                    //joiner.add("baz");
                }
                String asso = ruleInfoList.get(position).getRuleassociation();
                String joined = joiner.toString(); //
                try {
                    JSONObject obj = new JSONObject();
                    obj.put("association", asso);
                   // Toast.makeText(getApplicationContext(), obj.toString() + "condition{[" + joined + "]}", Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // String test = asso.replace()

                String serverURI = "tcp://192.168.209.189:1883";






            }

            @Override
            public void onLongClick(View view, int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Options")
                        .setItems(R.array.action_dialog_rule, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        //TODO edit saveURL the item


                                        //deleterequest(devicelist.get(position).getPlattformid());
                                        sqLiteHelper.deleteRuleSensorInfo(String.valueOf(ruleInfoList.get(position).getRuleengineruleid()));
                                        sqLiteHelper.deleteActuatorbyPlattform(String.valueOf(ruleInfoList.get(position).getRuleengineruleid()));
                                        sqLiteHelper.deleteRuleInfo(ruleInfoList.get(position));


                                        ruleInfoList.remove(position);
                                        mAdapter.notifyItemRemoved(position);


                                        break;
                                    //case 1:
                                        //TODO db.delete(deviceInfo.getId()); geht nicht mehr wegen string
                                        //kann das so bleiben??

                                }
                            }
                        }).create().show();

            }
        }));


        this.notBuilder = new NotificationCompat.Builder(this);

        // The message will automatically be canceled when the user clicks on Panel

        this.notBuilder.setAutoCancel(true);

    }




    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RuleEngineOverviewActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void deleteSharendPref(String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    void makeNotificationChannel(String id, String name, int importance) {
        NotificationChannel channel = new NotificationChannel(id, name, importance);
        channel.setShowBadge(true); // set false to disable badges, Oreo exclusive

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        assert notificationManager != null;
        notificationManager.createNotificationChannel(channel);
    }

    void issueNotification() {
        if(ruleEngineActuatorInfo != null) {
        // make the channel. The method has been discussed before.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            makeNotificationChannel("CHANNEL_1", "Example channel", NotificationManager.IMPORTANCE_DEFAULT);
        }
        // the check ensures that the channel will only be made
        // if the device is running Android 8+

        NotificationCompat.Builder notification =
                new NotificationCompat.Builder(this, "CHANNEL_1");
        // the second parameter is the channel id.
        // it should be the same as passed to the makeNotificationChannel() method


            notification
                    .setSmallIcon(R.mipmap.iconmbp) // can use any other icon
                    .setContentTitle("Notification!")
                    .setContentText(ruleEngineActuatorInfo.getRulevalue())
                    .setNumber(3); // this shows a number in the notification dots

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        assert notificationManager != null;
        notificationManager.notify(1, notification.build());
        // it is better to not use 0 as notification id, so used 1.
    }  }




//public boolean isittrue(){
    //  "(57< 3 & 56 > 4  ) Oder (45 > 3 )"
    //        if a
    //  return boolen
//}


    public String getValuesfromSensor(String sensorId, RuleEngineSensorInfo ruleEngineSensorInfo) {

        String url_forsensorvalues = "http://192.168.209.189:8080/MBP" + "/api/valueLogs/search/findAllByIdref?idref=" + sensorId + "&page=0&size=1&sort=date,desc";
        //final String url = "http://192.168.209.189:8080/MBP/api/types";
        RequestQueue queue = Volley.newRequestQueue(context); // this = context
        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url_forsensorvalues, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("Response", response.toString());
                        JSONObject obj = null;

                        try {
                            String assosciation1 = "";

                            JSONObject mainObject = response;

                            obj = response.getJSONObject("_embedded");

                            Log.d("Response ", obj.toString());
                            JSONArray jsonArray = obj.getJSONArray("valueLogs");
                            Log.d("Response Array", jsonArray.toString());

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject explrObject = jsonArray.getJSONObject(i);
                                String name = explrObject.getString("value");
                                String time = explrObject.getString("date");

                                sensorId.length();
                                Pattern p = Pattern.compile("-?\\d+");
                                Matcher m = p.matcher(asso);
                                while (m.find()) {
                                    System.out.println(m.group());
                                }

                                //TODO wichtig!!!!! replace zum 2 > 2 beide zahlen! suche änderung!
                                asso = asso.replace(" " +Long.toString(ruleEngineSensorInfo.getRulesensorid())+ " ", name);

                                //asso= assosciation1;

                                boolean rulevalue = false;
                                numberrequest = numberrequest - 1;
                                if (numberrequest == 0) {
                                    rulevalue = genetrateruleboolean(asso);
                                    if (rulevalue) {
                                      //  createNotification();
                                        issueNotification();
                                    }
                                }
                                //getValuesfromSensor(ruleEngineSensorInfo.getRulesensorgenerateid(),ruleassosiationpartwithoutRest);
                                // }
                                //}
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Log.d("Error.Response", response);
                    }
                }
        );

        // add it to the RequestQueue
        queue.add(getRequest);
        return asso;
    }

    public boolean genetrateruleboolean(String association) {
        bo = new ArrayList<Boolean>();
        boolean answer = false;
        //String currentString = "(88 < 59 & 86 > 78 & 77 > 88) : (33 < 34 & 55 > 37)";
        String currentString = association;
        //String[] separatedoder = currentString.split(":");
        String[] separatedoder = currentString.split("OR");
        //String e = separated[0]; // this will contain "Fruit"
        //String t = separated[1]; // this will contain " they taste good"

        String currentString1 = "Fruit: they taste good";
        // String[] separated2 = currentString1.split(":");
        String[] separated2 = currentString1.split("OR");

        String tt = separated2[0]; // this will contain "Fruit"
        //separated[1]; // this will contain " they taste good"
        System.out.println(tt);

        for (String s : separatedoder) {
            System.out.println("alles eparatedoder" + s);
            test(s);
            System.out.println(bo);

            // bo = Arrays.asList(3, 4, 6, 12, 20);

            // Check if all elements of stream
            // are divisible by 3 or not using
            // Stream allMatch(Predicate predicate)
            answer = bo.stream().allMatch(n -> n == true);
            if (answer == true) {
                return answer;
                //break;
            }
            bo.clear();

            //if(bo. == true){
            //break;
            //}


            // get element number 0 and 1 and put it in a variable,
            // and the next time get element      1 and 2 and put this in another variable.
        }
        return answer;

    }


    ////////////////Eclipse code

    public static void test(String und) {
        //String[] separ= und.split("&");
        String[] separ = und.split("AND");
        for (String s : separ) {

            test2(s);

            // get element number 0 and 1 and put it in a variable,
            // and the next time get element      1 and 2 and put this in another variable.
        }

    }

    public static void test2(String operator) {
        //String[] separo= operator.split("&");
        String[] separo = operator.split("AND");
        for (String s : separo) {
            System.out.println(s);
            test3(s);


            // get element number 0 and 1 and put it in a variable,
            // and the next time get element      1 and 2 and put this in another variable.
        }

    }

    public static ArrayList<Boolean> test3(String zahel) {
        System.out.println(zahel);
        //String text = "-jaskdh2367sd.27askjdfh23";
        String text = zahel;

        //System.out.println(digits);
        if (zahel.contains("<")) {
            String[] separ = zahel.split("<");

            String digits = separ[0].toString().replaceAll("[^0-9.]", "");
            String digits2 = separ[1].toString().replaceAll("[^0-9.]", "");

            System.out.println("digi" + digits);
            double one = Double.parseDouble(digits);
            double two = Double.parseDouble(digits2);
            System.out.println("zahl 1 ist: " + one);
            System.out.println("zahl 2 ist: " + two);
            if (one < two) {
                b = true;
                bo.add(b);
            } else {
                b = false;
                bo.add(b);
            }


        } else if (zahel.contains(">")) {
            String[] separ = zahel.split(">");

            String digits = separ[0].toString().replaceAll("[^0-9.]", "");
            String digits2 = separ[1].toString().replaceAll("[^0-9.]", "");

            System.out.println("digi " + digits);
            double one = Double.parseDouble(digits);
            double two = Double.parseDouble(digits2);
            System.out.println("zahl 1 ist: " + one);
            System.out.println("zahl 2 ist: " + two);
            if (one > two) {
                b = true;
                bo.add(b);
            } else {
                b = false;
                bo.add(b);
            }
        }else if (zahel.contains("=")) {
            String[] separ = zahel.split("=");

            String digits = separ[0].toString().replaceAll("[^0-9.]", "");
            String digits2 = separ[1].toString().replaceAll("[^0-9.]", "");

            System.out.println("digi " + digits);
            double one = Double.parseDouble(digits);
            double two = Double.parseDouble(digits2);
            System.out.println("zahl 1 ist: " + one);
            System.out.println("zahl 2 ist: " + two);
            if (one == two) {
                b = true;
                bo.add(b);
            } else {
                b = false;
                bo.add(b);
            }
        }else if (zahel.contains("!=")) {
            String[] separ = zahel.split("!=");

            String digits = separ[0].toString().replaceAll("[^0-9.]", "");
            String digits2 = separ[1].toString().replaceAll("[^0-9.]", "");

            System.out.println("digi " + digits);
            double one = Double.parseDouble(digits);
            double two = Double.parseDouble(digits2);
            System.out.println("zahl 1 ist: " + one);
            System.out.println("zahl 2 ist: " + two);
            if (one != two) {
                b = true;
                bo.add(b);
            } else {
                b = false;
                bo.add(b);
            }
        }else if (zahel.contains(">=")) {
            String[] separ = zahel.split(">=");

            String digits = separ[0].toString().replaceAll("[^0-9.]", "");
            String digits2 = separ[1].toString().replaceAll("[^0-9.]", "");

            System.out.println("digi " + digits);
            double one = Double.parseDouble(digits);
            double two = Double.parseDouble(digits2);
            System.out.println("zahl 1 ist: " + one);
            System.out.println("zahl 2 ist: " + two);
            if (one >= two) {
                b = true;
                bo.add(b);
            } else {
                b = false;
                bo.add(b);
            }
        }else if (zahel.contains("<=")) {
            String[] separ = zahel.split("<=");

            String digits = separ[0].toString().replaceAll("[^0-9.]", "");
            String digits2 = separ[1].toString().replaceAll("[^0-9.]", "");

            System.out.println("digi " + digits);
            double one = Double.parseDouble(digits);
            double two = Double.parseDouble(digits2);
            System.out.println("zahl 1 ist: " + one);
            System.out.println("zahl 2 ist: " + two);
            if (one <= two) {
                b = true;
                bo.add(b);
            } else {
                b = false;
                bo.add(b);
            }
        }


        else {
            System.out.println("out");
        }


        return bo;
    }

    public static int countIntegers(String input) {
        int count = 0;
        boolean isPreviousDigit = false;

        for (int i = 0; i < input.length(); i++) {
            if (Character.isDigit(input.charAt(i))) {
                if (!isPreviousDigit) {
                    count++;
                    isPreviousDigit = true;
                }
            } else {
                isPreviousDigit = false;
            }
        }
        return count;
    }


}




