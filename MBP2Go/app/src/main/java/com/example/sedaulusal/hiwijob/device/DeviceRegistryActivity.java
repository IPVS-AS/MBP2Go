package com.example.sedaulusal.hiwijob.device;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.sedaulusal.hiwijob.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.R.attr.duration;
import static com.example.sedaulusal.hiwijob.SettingActivity.mypreference;

public class DeviceRegistryActivity extends AppCompatActivity {

    EditText edtName, edtMacid, edtSensorname, edtOptionalId, edtUsername, edtPassword, edtPinset, edtPinsetActuator;
    EditText edtActuatorname;

    Button btnaddSensor, btnaddActuator;
    ImageView imageView;
    Toast myToast;
    MyAdapter adapter;
    DeviceInfo deviceInfo;
    Spinner spinner;
    ViewFlipper vf;
    RelativeLayout relativeLayout;
    int targetHeight;
    int startHeight;
    boolean clicked, remove;

    Context context;


    //Actuator liste
    TextView txtActuatorName, txtActuatorId;
    ImageView imageViewActuator;
    GridView gridView2;
    ArrayList<ActuatorInfo> actuatorlist, testliste2;
    ActuatorAdapter actuatoradapter = null;
    ActuatorInfo actuatorInfo;
    String actuatorname;
    byte[] actuatorimage;
    ArrayList<SensorInfo> sensorlistfordelete = new ArrayList<>();
    ArrayList<SensorInfo> sensorlist_for_post = new ArrayList<>();
    ArrayList<ActuatorInfo> actuatorlistfordelete = new ArrayList<>();


    //Sensor liste
    TextView txtSensorName, txtSensorId;
    ImageView imageViewSensor;

    GridView gridView;
    ArrayList<SensorInfo> sensorlist, testliste;
    ArrayList<String> sensorpid = new ArrayList<>();
    SensorAdapter sensoradapter = null;
    SensorInfo sensorInfo;
    Spinner sensorspinner, actuatorspinner, componenttypespinner, actuatorcomponenttypespinner;
    ArrayAdapter<String> spinnerSensorAdapter;
    Map<String, String> sensorTypeIDName = new HashMap<String, String>();
    //RequestQueue queue = Volley.newRequestQueue(context);
    //Map<String,String> actuatorTypeIDName = new HashMap<String,String>();


    String sensorname;
    byte[] sensorimage;
    Cursor cursor;

    long deviceid, sensorid, actuatorid;
    String pid, sensorpid1, actuatorpid1 = "";
    String temperaturid;

    boolean posttrue = false;
    String post;

    final int REQUEST_CODE_GALLERY = 999;
    private static final int REQUEST_CODE_CAMERA = 100;

    public static SQLiteHelper sqLiteHelper;

    SharedPreferences sharedPreferences;
    String url;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_registry);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DeviceRegistryActivity.this, DeviceOverviewActivity.class);
                startActivity(intent);
            }
        });

        /*
        SharedPreferences to get the url from setting
         */
        sharedPreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        url = sharedPreferences.getString("URL", "");


        /*
         * Actuator List
         * */
        gridView2 = (GridView) findViewById(R.id.gridView_aktuator);
        actuatorlist = new ArrayList<>();
        //testliste = new ArrayList<>();
        edtActuatorname = (EditText) findViewById(R.id.edt_aktuator);
        actuatoradapter = new ActuatorAdapter(this, R.layout.aktuator_items, actuatorlist);
        gridView2.setAdapter(actuatoradapter);

        testliste2 = new ArrayList<>();


        /*
        This part is for the componenttype spinner
         */
        componenttypespinner = (Spinner) findViewById(R.id.spinner_componettype);
        ArrayAdapter sensorspinnerArrayAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.componentType));
        componenttypespinner.setAdapter(sensorspinnerArrayAdapter);


        actuatorcomponenttypespinner = (Spinner) findViewById(R.id.spinneractuatortype);
        ArrayAdapter actuatorspinnerArrayAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.actuatorcomponentType));
        actuatorcomponenttypespinner.setAdapter(actuatorspinnerArrayAdapter);


        //sensorspinner = (Spinner) findViewById(R.id.spinner);
        btnaddActuator = (Button) findViewById(R.id.btn_addActuator);

        actuatorspinner = (Spinner) findViewById(R.id.spinneractuator);


        actuatorspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                final String selectedItem = parent.getItemAtPosition(position).toString();
                //edtSensorname.setText(selectedItem);

                btnaddActuator.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (actuatorcomponenttypespinner.getSelectedItem().toString().contains("Speaker")) {

                            actuatorbtnAddclicked(selectedItem, R.drawable.icon_speaker_actuator);


                        } else if (actuatorcomponenttypespinner.getSelectedItem().toString().contains("Light")) {

                            actuatorbtnAddclicked(selectedItem, R.drawable.icon_light_actuator);

                        } else if (actuatorcomponenttypespinner.getSelectedItem().toString().contains("Vibration")) {

                            actuatorbtnAddclicked(selectedItem, R.drawable.icon_vibration_actuator);


                        } else if (actuatorcomponenttypespinner.getSelectedItem().toString().contains("Motor")) {


                            actuatorbtnAddclicked(selectedItem, R.drawable.icon_motor_actuator);


                        } else if (actuatorcomponenttypespinner.getSelectedItem().toString().contains("Heater")) {


                            actuatorbtnAddclicked(selectedItem, R.drawable.icon_heater_actuator);

                        } else if (actuatorcomponenttypespinner.getSelectedItem().toString().contains("Air Conditioner")) {


                            actuatorbtnAddclicked(selectedItem, R.drawable.icon_air_conditioner_actuator);


                        } else if (actuatorcomponenttypespinner.getSelectedItem().toString().contains("Buzzer")) {


                            actuatorbtnAddclicked(selectedItem, R.drawable.icon_buzzer_actuator);

                        } else if (actuatorcomponenttypespinner.getSelectedItem().toString().contains("Switch")) {


                            actuatorbtnAddclicked(selectedItem, R.drawable.icon_switch_actuator);


                        } else if (actuatorcomponenttypespinner.getSelectedItem().toString().contains("LED")) {


                            actuatorbtnAddclicked(selectedItem, R.drawable.icon_led_actuator);


                        } else {

                            actuatorbtnAddclicked(selectedItem, R.drawable.icon_default_actuator);


                        }
                    }
                });
            } // to close the onItemSelected

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
//wenn man on long Click machen soll hilft diese Methode
//gridView.setOnTouchListener();

        gridView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Option")
                        .setItems(R.array.action_dialog_gridview, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        remove = true;
                                        //TODO: Löschen per KEY nicht per PinSet
                                        //sqLiteHelper.deleteActuator(actuatorlist.get(position).getId());
                                        sqLiteHelper.deleteActuatorpin(actuatorlist.get(position).getActuatorPinset());
                                        actuatorlistfordelete.add(actuatorlist.get(position));
                                        actuatorlist.remove(position);
                                        actuatoradapter.notifyDataSetChanged();

                                }
                            }
                        }).create().show();
            }


        });



        /*
         * Sensor List
         * */
        gridView = (GridView) findViewById(R.id.gridView);
        context = this;
        sensorlist = new ArrayList<>();
        testliste = new ArrayList<>();

        //get types in spinner
        spinnerrequest(android.R.layout.simple_spinner_item);


        sensoradapter = new SensorAdapter(this, R.layout.sensor_items, sensorlist);
        gridView.setAdapter(sensoradapter);

        //TODO ist componenttypespinner ok
        sensorspinner = (Spinner) findViewById(R.id.spinner);
        btnaddSensor = (Button) findViewById(R.id.btn_addSensor);


        sensorspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                final String selectedItem = parent.getItemAtPosition(position).toString();
                //edtSensorname.setText(selectedItem);
                //Toast.makeText(getApplicationContext(), "Temperatur wurde gewählt", Toast.LENGTH_SHORT).show();

                btnaddSensor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (componenttypespinner.getSelectedItem().toString().equals("Temperature")) {
                            //Toast.makeText(getApplicationContext(), "Temperatur wurde gewählt", Toast.LENGTH_SHORT).show();


                            String test = sensorspinner.getSelectedItem().toString();
                            test.length();
                            String comp = componenttypespinner.getSelectedItem().toString();
                            comp.length();
                            sensorbtnAddclicked(selectedItem, R.drawable.icon_temperature_sensor);


                        } else if (componenttypespinner.getSelectedItem().toString().equals("Motion")) {

                            sensorbtnAddclicked(selectedItem, R.drawable.icon_motion_sensor);


                        } else if (componenttypespinner.getSelectedItem().toString().equals("Camera")) {


                            sensorbtnAddclicked(selectedItem, R.drawable.icon_camera_sensor);


                        } else if (componenttypespinner.getSelectedItem().toString().equals("Location")) {

                            sensorbtnAddclicked(selectedItem, R.drawable.icon_location_sensor);

                        } else if (componenttypespinner.getSelectedItem().toString().equals("Gas")) {

                            sensorbtnAddclicked(selectedItem, R.drawable.icon_gas_sensor);


                        } else if (componenttypespinner.getSelectedItem().equals("Sound")) {

                            sensorbtnAddclicked(selectedItem, R.drawable.icon_sound_sensor);

                        } else if (componenttypespinner.getSelectedItem().toString().equals("Touch")) {

                            sensorbtnAddclicked(selectedItem, R.drawable.icon_touch_sensor);

                        } else if (componenttypespinner.getSelectedItem().toString().contains("Humidity")) {

                            sensorbtnAddclicked(selectedItem, R.drawable.icon_humidity_sensor);


                        } else if (componenttypespinner.getSelectedItem().toString().contains("Vibration")) {

                            sensorbtnAddclicked(selectedItem, R.drawable.icon_vibration_sensor);

                        } else if (componenttypespinner.getSelectedItem().toString().contains("Gyroscope")) {

                            sensorbtnAddclicked(selectedItem, R.drawable.icon_gyroscope_sensor);


                        } else if (componenttypespinner.getSelectedItem().toString().contains("Proximity")) {

                            sensorbtnAddclicked(selectedItem, R.drawable.icon_proximity_sensor);


                        } else if (componenttypespinner.getSelectedItem().toString().contains("Acceleration")) {

                            sensorbtnAddclicked(selectedItem, R.drawable.icon_default_sensor);


                        } else if (componenttypespinner.getSelectedItem().toString().contains("Light Flicker")) {

                            sensorbtnAddclicked(selectedItem, R.drawable.ic_lightbulb_outline_black_24dp);


                        } else {
                            sensorbtnAddclicked(selectedItem, R.drawable.icon_default_sensor);


                        }
                    }
                });
            } // to close the onItemSelected

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
//wenn man on long Click machen soll hilft diese Methode
//gridView.setOnTouchListener();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Option")
                        .setItems(R.array.action_dialog_gridview, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        remove = true;
                                        //sqLiteHelper.deletesensor(sensorlist.get(position).getId());
                                        //TODO: Löschen per KEY nicht per PinSet
                                        //sqLiteHelper.deletesensorpin(sensorlist.get(position).getGeneratesensorid());
                                        sensorlistfordelete.add(sensorlist.get(position));
                                        sensorlist.remove(position);
                                        sensoradapter.notifyDataSetChanged();
                                }
                            }
                        }).create().show();
            }


        });




        /*
        after click edit button in overview activity
        */
        final String edittextname, edittextmacId, textviewsensorname, edittextmacIddetail, editplattform;
        int devid;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                edittextname = null;
                edittextmacId = null;
            } else {
                sqLiteHelper = new SQLiteHelper(this);

                edittextname = extras.getString("name");
                edittextmacIddetail = extras.getString("maciddetail");
                //edittextmacId = extras.getString("macid");

                editplattform = extras.getString("plattform");


                //sqLiteHelper.getDevice(editplattform).getMacid();

                textviewsensorname = extras.getString("NAME");
                devid = extras.getInt("deviceid");

                EditText name, macId, optionalIP, username, password;
                Spinner deviceType;
                ImageView deviceImage;
                TextView senname;
                name = (EditText) findViewById(R.id.edtName);
                name.setText(sqLiteHelper.getDevice(editplattform).name);
                macId = (EditText) findViewById(R.id.edtSurname);
                //edittextmacId = sqLiteHelper.getDevice(editplattform).getMacid();
                macId.setText(sqLiteHelper.getDevice(editplattform).getMacid());
                optionalIP = (EditText) findViewById(R.id.edtOptional_IP);
                username = (EditText) findViewById(R.id.edt_userName);
                password = (EditText) findViewById(R.id.edit_password);
                deviceType = (Spinner) findViewById(R.id.spinner2);
                deviceImage = (ImageView) findViewById(R.id.imageView);

                optionalIP.setText(sqLiteHelper.getDevice(editplattform).optionalIP);
                username.setText(sqLiteHelper.getDevice(editplattform).getUsername());
                password.setText(sqLiteHelper.getDevice(editplattform).getPassword());
                Bitmap bitmap = BitmapFactory.decodeByteArray(sqLiteHelper.getDevice(editplattform).getImage(), 0, sqLiteHelper.getDevice(editplattform).getImage().length);

                deviceImage.setImageBitmap(bitmap);


                String compareValue = sqLiteHelper.getDevice(editplattform).getDevicetype();
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.devicetype, android.R.layout.simple_spinner_item);
                //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                deviceType.setAdapter(adapter);
                if (compareValue != null) {
                    int spinnerPosition = adapter.getPosition(compareValue);
                    deviceType.setSelection(spinnerPosition);


                    if (deviceType.getSelectedItem().equals("Raspberry Pi")) {
                        deviceImage.setImageResource(R.drawable.icon_raspberry_pi_device);

                    } else if (deviceType.getSelectedItem().equals("Arduino")) {
                        deviceImage.setImageResource(R.drawable.icon_arduino_device);
                    } else if (deviceType.getSelectedItem().equals("Computer")) {
                        deviceImage.setImageResource(R.drawable.icon_computer_device);
                    } else if (deviceType.getSelectedItem().equals("Audio System")) {
                        deviceImage.setImageResource(R.drawable.icon_audio_system_device);
                    } else if (deviceType.getSelectedItem().equals("Camera")) {
                        deviceImage.setImageResource(R.drawable.icon_camera_device);
                    } else if (deviceType.getSelectedItem().equals("Gateway")) {
                        deviceImage.setImageResource(R.drawable.icon_default_device);
                    } else if (deviceType.getSelectedItem().equals("Laptop")) {
                        deviceImage.setImageResource(R.drawable.icon_laptop_device);
                    } else if (deviceType.getSelectedItem().equals("NodeMCU")) {
                        deviceImage.setImageResource(R.drawable.icon_default_device);
                    } else if (deviceType.getSelectedItem().equals("Smartphone")) {
                        deviceImage.setImageResource(R.drawable.icon_smartphone_device);
                    } else if (deviceType.getSelectedItem().equals("Smartwatch")) {
                        deviceImage.setImageResource(R.drawable.icon_smartwatch_device);
                    } else if (deviceType.getSelectedItem().equals("TV")) {
                        deviceImage.setImageResource(R.drawable.icon_tv_device);
                    } else if (deviceType.getSelectedItem().equals("Voice Controller")) {
                        deviceImage.setImageResource(R.drawable.icon_voice_controller_device);
                    } else if (deviceType.getSelectedItem().equals("Other")) {
                        deviceImage.setImageResource(R.drawable.icon_default_device);

                    } else {
                        deviceImage.setImageResource(R.mipmap.ic_launcher);
                    }
                } // to close the onItemSelected

                deviceType.setEnabled(false);
                name.setEnabled(false);
                macId.setEnabled(false);
                optionalIP.setEnabled(false);
                username.setEnabled(false);
                password.setEnabled(false);

                if (getIntent().hasExtra("maciddetail")) {
                    // Do stuff with extra
                    DeviceInfo devicedetail = sqLiteHelper.getDevicemacid(edittextmacIddetail);
                    macId.setText(edittextmacIddetail);


                    cursor = sqLiteHelper.getAllCursorSensorsByDevicesPlattformid(devicedetail.getPlattformid());
                    if (cursor.moveToFirst()) {

                       /* do {
                            String sensorPinset = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_SensorPINSET));
                            String sensorid = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_SensorID));
                            String sensorname = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_SensorNAME));
                            byte[] sensorimage = cursor.getBlob(cursor.getColumnIndex(SQLiteHelper.KEY_SensorIMAGE));
                            String type = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_SensorTYP));

                            sensorInfo = new SensorInfo(sensorname, sensorimage, sensorPinset, type);

                            //
                            Log.d("Anzahl Sen", "Stimmt Anzahl? " + sensorlist.size());

                            sensorlist.add(sensorInfo);
                            sensoradapter.notifyDataSetChanged();

                        } while (cursor.moveToNext());*/
                    }


                    cursor = sqLiteHelper.getAllCursorActuatorByDevicesPlattform(devicedetail.getPlattformid());
                    if (cursor.moveToFirst()) {

                        do {
                            //Table aktuator ansehen
                            String actuatorid = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_ActuatorID));
                            String actuatorpinset = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_ActuatorPINSET)); //4 ist type ist noch null
                            String actuatorname = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_ActuatorNAME));
                            byte[] actuatorimage = cursor.getBlob(cursor.getColumnIndex(SQLiteHelper.KEY_ActuatorIMAGE));

                            actuatorInfo = new ActuatorInfo(actuatorname, actuatorimage, actuatorpinset);

                            //
                            Log.d("Sensor nach Intent", "sensoren: " + sensorid + " " + sensorname + " " + " ");
                            Log.d("Anzahl Sen", "Stimmt Anzahl? " + actuatorlist.size());

                            actuatorlist.add(actuatorInfo);
                            actuatoradapter.notifyDataSetChanged();

                        } while (cursor.moveToNext());
                    }

                }

                if (getIntent().hasExtra("plattform")) {
                    // Do stuff with extra

                    DeviceInfo deviceoverview = sqLiteHelper.getDevice(editplattform);

                    cursor = sqLiteHelper.getAllCursorSensorsByDevicesPlattformid(deviceoverview.getPlattformid());
                    if (cursor.moveToFirst()) {

                        do {
                            String sensorPinset = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_SensorPINSET));
                            String sensorid = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_SensorID));
                            String sensorname = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_SensorNAME));
                            String sensortype = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_SensorTYP));
                            byte[] sensorimage = cursor.getBlob(cursor.getColumnIndex(SQLiteHelper.KEY_SensorIMAGE));

                            sensorInfo = new SensorInfo(sensorid, sensorname, sensorimage, sensorPinset, sensortype);

                            //
                            Log.d("Sensor nach Intent", "sensoren: " + sensorid + " " + sensorname + " " + " ");
                            Log.d("Anzahl Sen", "Stimmt Anzahl? " + sensorlist.size());

                            sensorlist.add(sensorInfo);
                            sensoradapter.notifyDataSetChanged();

                        } while (cursor.moveToNext());
                    }
                    cursor = sqLiteHelper.getAllCursorActuatorByDevicesPlattform(deviceoverview.getPlattformid());
                    if (cursor.moveToFirst()) {

                        do {
                            //int id = cursor.getInt(0);
                            String actuatorid = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_ActuatorID));
                            String actuatorpinset = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_ActuatorPINSET)); //4 ist type ist noch null
                            String actuatorname = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_ActuatorNAME));
                            byte[] actuatorimage = cursor.getBlob(cursor.getColumnIndex(SQLiteHelper.KEY_ActuatorIMAGE));

                            actuatorInfo = new ActuatorInfo(actuatorname, actuatorimage, actuatorid);

                            //
                            Log.d("Sensor nach Intent", "sensoren: " + sensorid + " " + sensorname + " " + " ");
                            Log.d("Anzahl Sen", "Stimmt Anzahl? " + actuatorlist.size());

                            actuatorlist.add(actuatorInfo);
                            actuatoradapter.notifyDataSetChanged();

                        } while (cursor.moveToNext());
                    }

                }



            }

        }
        /**
         * definition of first spinner with raspberry/arduino etc.
         * view with animation
         */
        vf = (ViewFlipper) findViewById(R.id.viewflipper_deviceregistry_spinner);
        relativeLayout = (RelativeLayout) findViewById(R.id.viewspinner);
        relativeLayout.setMinimumHeight(startHeight);

        spinner = (Spinner) findViewById(R.id.spinner2);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String selectedItem = parent.getItemAtPosition(position).toString();
                if (selectedItem.equals("Raspberry Pi")) {
                    //Toast.makeText(getApplicationContext(), "Raspberry wurde gewählt", Toast.LENGTH_SHORT).show();
                    imageView.setImageResource(R.drawable.raspberry_pi);

                    setDisplazAnimation();


                }
                if (selectedItem.equals("Arduino")) {
                 /*   startHeight = 100;
                    targetHeight = 100;

                    //Toast.makeText(getApplicationContext(), "Arduino wurde gewählt", Toast.LENGTH_SHORT).show();
                    vf.setDisplayedChild(1);*/
                    imageView.setImageResource(R.drawable.arduino);
                    setDisplazAnimation();
                   /* ResizeAnimation resizeAnimation = new ResizeAnimation(
                            relativeLayout,
                            targetHeight,
                            startHeight
                    );
                    resizeAnimation.setDuration(duration);
                    relativeLayout.startAnimation(resizeAnimation);*/


                }
                if (selectedItem.equals("Computer")) {
                    imageView.setImageResource(R.drawable.icon_computer_device);
                    setDisplazAnimation();
                }
                if (selectedItem.equals("Audio System")) {
                    imageView.setImageResource(R.drawable.icon_audio_system_device);
                    setDisplazAnimation();
                }
                if (selectedItem.equals("Camera")) {
                    imageView.setImageResource(R.drawable.icon_camera_device);
                    setDisplazAnimation();
                }
                if (selectedItem.equals("Gateway")) {
                    imageView.setImageResource(R.drawable.icon_default_device);
                    setDisplazAnimation();
                }
                if (selectedItem.equals("Laptop")) {
                    imageView.setImageResource(R.drawable.icon_laptop_device);
                    setDisplazAnimation();
                }
                if (selectedItem.equals("NodeMCU")) {
                    imageView.setImageResource(R.drawable.icon_default_device);
                }
                if (selectedItem.equals("Smartphone")) {
                    imageView.setImageResource(R.drawable.icon_smartphone_device);
                    setDisplazAnimation();


                }
                if (selectedItem.equals("Smartwatch")) {
                    imageView.setImageResource(R.drawable.icon_smartwatch_device);
                    setDisplazAnimation();

                }
                if (selectedItem.equals("TV")) {
                    imageView.setImageResource(R.drawable.icon_tv_device);
                    setDisplazAnimation();

                }
                if (selectedItem.equals("Voice Controller")) {
                    imageView.setImageResource(R.drawable.icon_voice_controller_device);
                    setDisplazAnimation();

                }

                if (selectedItem.equals("Other")) {
                  /*  startHeight = 100;
                    targetHeight = 100;

                   // Toast.makeText(getApplicationContext(), "Other wurde gewählt", Toast.LENGTH_SHORT).show();
                    vf.setDisplayedChild(1);*/
                    imageView.setImageResource(R.drawable.icon_default_device);
                    setDisplazAnimation();

                  /*  ResizeAnimation resizeAnimation = new ResizeAnimation(
                            relativeLayout,
                            targetHeight,
                            startHeight
                    );
                    resizeAnimation.setDuration(duration);
                    relativeLayout.startAnimation(resizeAnimation);*/


                }
            } // to close the onItemSelected

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        edtName = (EditText) findViewById(R.id.edtName);
        edtMacid = (EditText) findViewById(R.id.edtSurname);
        imageView = (ImageView) findViewById(R.id.imageView);

        edtOptionalId = (EditText) findViewById(R.id.edtOptional_IP);
        edtUsername = (EditText) findViewById(R.id.edt_userName);
        edtPassword = (EditText) findViewById(R.id.edit_password);


        //Sensor Data
        txtSensorId = (TextView) findViewById(R.id.txtSensorId);
        txtSensorName = (TextView) findViewById(R.id.txtSensorName);
        imageViewSensor = (ImageView) findViewById(R.id.imgSensor);
        edtSensorname = (EditText) findViewById(R.id.edtsensorname);
        edtPinset = (EditText) findViewById(R.id.sensorpin);
        edtPinsetActuator = (EditText) findViewById(R.id.edttext_actuatorpinset);


        edtMacid.addTextChangedListener(new TextWatcher() {
            int prevL = 0;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                prevL = edtMacid.getText().toString().length();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int length = editable.length();
                if ((prevL < length) && (length == 2 || length == 5 || length == 8 || length == 11 || length == 14)) {
                    editable.append("-");
                }
            }
        });

        sqLiteHelper = new SQLiteHelper(this);


    }

    public void setDisplazAnimation() {
        vf.setDisplayedChild(1);

        //TO DO: Look to a other solution to resize view
        startHeight = 700;
        targetHeight = 700;
        ResizeAnimation resizeAnimation = new ResizeAnimation(
                relativeLayout,
                targetHeight,
                startHeight
        );
        resizeAnimation.setDuration(duration);
        relativeLayout.startAnimation(resizeAnimation);
    }

    /*
    Methode wenn button add in Sensor angeklickt wird, wird ein sensorInfo angelegt mit passenden bild
    int id ist dabei res zum bild
     */
    private void sensorbtnAddclicked(String selectedItem, int id) {
        clicked = true;
        String name = edtSensorname.getText().toString();
        String pinset = edtPinset.getText().toString();
        String type = sensorTypeIDName.get(selectedItem.toString());

        sensorname = selectedItem;
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), id);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        // sensorimage = byteArray;


        sensorInfo = new SensorInfo(name, byteArray, pinset, type);
        sensorInfo.setSensoradapter(componenttypespinner.getSelectedItem().toString());
        //sensorspinner.getSelectedItem().toString();
        // SensorInfo sensorInf = new SensorInfo(post, sensorInfo.getName(), sensorInfo.getImage(), sensorInfo.getSensorPinset(), sensorInfo.getSensorTyp(), sensorInfo.getSensoradapter());

        sensorlist.add(sensorInfo);
        testliste.add(sensorInfo);

        sensoradapter.notifyDataSetChanged();
        Log.e("Todo Count", "Todo count: " + sqLiteHelper.getSensorCount());
    }


    /*
   Methode wenn button add in Actuator angeklickt wird, wird ein actuatorInfo angelegt mit passenden bild
   int id ist dabei res zum bild
    */
    private void actuatorbtnAddclicked(String selectedItem, int id) {
        clicked = true;
        String name = edtActuatorname.getText().toString();
        String pinset = edtPinsetActuator.getText().toString();
        String type = sensorTypeIDName.get(selectedItem.toString());

        actuatorname = selectedItem;
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), id);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        actuatorimage = byteArray;

        actuatorInfo = new ActuatorInfo(name, byteArray, pinset, type);
        actuatorInfo.setActuatoradapter(actuatorcomponenttypespinner.getSelectedItem().toString());
        actuatorlist.add(actuatorInfo);
        testliste2.add(actuatorInfo);
        actuatoradapter.notifyDataSetChanged();
    }

    /*
    methode um types in den spinner zu bekommen
     */
    private void spinnerrequest(Integer layout_spinner) {
        spinnerSensorAdapter = new ArrayAdapter(context, layout_spinner);

        //final String url = "http://192.168.209.189:8080/MBP/api/types";
        RequestQueue queue = Volley.newRequestQueue(context); // this = context
        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url + "/api/adapters", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("Response", response.toString());
                        JSONObject obj = null;

                        try {
                            JSONObject mainObject = response;

                            obj = response.getJSONObject("_embedded");

                            Log.d("Response ", obj.toString());
                            JSONArray jsonArray = obj.getJSONArray("adapters");
                            Log.d("Response Array", jsonArray.toString());

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject explrObject = jsonArray.getJSONObject(i);
                                String name = explrObject.getString("name");
                                String description = explrObject.getString("description");
                                temperaturid = explrObject.getString("id");
                                Log.d("Response Tempid", temperaturid);
                                sensorTypeIDName.put(name, temperaturid);
                                spinnerSensorAdapter.add(name);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        sensorspinner.setAdapter(spinnerSensorAdapter);
                        actuatorspinner.setAdapter(spinnerSensorAdapter);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Log.d("Error.Response", response);
                    }
                }
        ) {
            //
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> authentification = getHeaderforAuthentification();
                return authentification;

            }

        };

        // add it to the RequestQueue
        queue.add(getRequest);

    }


    /**
     * Define the action after clicking back on smartphone
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(DeviceRegistryActivity.this, DeviceOverviewActivity.class);
        startActivity(intent);
    }

    /**
     * define the imageview of DeviceOverview
     *
     * @param image
     * @return
     */
    private byte[] imageViewToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }


    /**
     * Toolbar function, with icons
     * action_add insert data in database
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_qrscann:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                sensorlist.clear();
                actuatorlist.clear();
                if (checkSelfPermission(Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            DeviceRegistryActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            REQUEST_CODE_CAMERA
                    );
                }

                Log.d("Ui", "clicked");
                try {
                    IntentIntegrator integrator1 = new IntentIntegrator(DeviceRegistryActivity.this);
                    integrator1.initiateScan();
                } catch (ActivityNotFoundException e) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "No scan data received!", Toast.LENGTH_SHORT);
                    toast.show();
                }

                return true;

            case R.id.action_add:

                try {
                    //myToast.makeText(getApplicationContext(), "is clicked", Toast.LENGTH_SHORT).show();


                    if (getIntent().hasExtra("macid") || getIntent().hasExtra("maciddetail")) {
                        disableInput(edtName);
                        edtMacid.setKeyListener(null);
                        spinner.setOnKeyListener(null);
                        edtOptionalId.setKeyListener(null);
                        edtUsername.setKeyListener(null);
                        edtPassword.setKeyListener(null);

                        Bundle extras = getIntent().getExtras();
                        int position;
                        String plattform;
                        position = extras.getInt("position");
                        plattform = extras.getString("plattform");
                        deviceInfo = sqLiteHelper.getDevice(plattform);
                        final String name = edtName.getText().toString();
                        final String macid = edtMacid.getText().toString();
                        final byte[] image = imageViewToByte(imageView);
                        final String devicetype = spinner.getSelectedItem().toString();
                        final String optionalIP = edtOptionalId.getText().toString();
                        final String userName = edtUsername.getText().toString();
                        final String password = edtPassword.getText().toString();
                        deviceInfo.setName(name);
                        deviceInfo.setMacid(macid);
                        deviceInfo.setImage(image);
                        deviceInfo.setDevicetype(devicetype);
                        deviceInfo.setOptionalIP(optionalIP);
                        deviceInfo.setUsername(userName);
                        deviceInfo.setPassword(password);



                        sqLiteHelper.updateDevice(deviceInfo);

                        RequestQueue queueUpdate = Volley.newRequestQueue(context);
                        JSONObject paramsUpdate = new JSONObject();
                        paramsUpdate.put("name", deviceInfo.getName());
                        //params.put("formattedMacAddress", deviceInfo.getMacid());
                        //TODO formatierte MacID und Optional IP adresse
                        paramsUpdate.put("username", deviceInfo.getUsername());
                        String unformatedMacID = deviceInfo.getMacid().replace("-", "");
                        paramsUpdate.put("macAddress", unformatedMacID);
                        paramsUpdate.put("formattedMacAddress", deviceInfo.getMacid());
                        paramsUpdate.put("ipAddress", deviceInfo.getOptionalIP());


                        //String urlUpdate = "http://192.168.209.189:8080/MBP/api/devices/" + deviceInfo.getPlattformid();
                      /*
                        String urlUpdate = url + "/api/devices/" + deviceInfo.getPlattformid();
                        JsonObjectRequest putRequest = new JsonObjectRequest(Request.Method.PUT, urlUpdate, paramsUpdate,
                                new Response.Listener() {
                                    @Override
                                    public void onResponse(Object response) {
                                        // response
                                        Log.d("Response", response.toString());
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        // error
                                        Log.d("Error.Response", error.toString());
                                        myToast.makeText(getApplicationContext(), "Error please check your configuration ", Toast.LENGTH_SHORT).show();

                                    }
                                }
                        ) {
                            //
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String, String> authentification = getHeaderforAuthentification();
                                return authentification;

                            }

                        };

                        queueUpdate.add(putRequest);*/


                        //sqLiteHelper.updateSensor(sensorInfo, deviceInfo);

                        ArrayList<SensorInfo> deviceWatchList = sqLiteHelper.getAllSensorsByPlattformId(deviceInfo.getPlattformid());
                        for (SensorInfo sensorInfo : deviceWatchList) {
                            RequestQueue queueUpdateSensor = Volley.newRequestQueue(context);
                            JSONObject paramsUpdateSensor = new JSONObject();
                            //String url = "http://192.168.209.189:8080/MBP/api/sensors/";
                            //String typesurl = "http://192.168.209.189:8080/MBP/api/types/";
                            //String deviceurl = "http://192.168.209.189:8080/MBP/api/devices/";
                            String typesurl = url + "/api/adapters/";
                            String deviceurl = url + "/api/devices/";
                            paramsUpdateSensor.put("name", sensorInfo.getName());
                            paramsUpdateSensor.put("adapter", typesurl + sensorInfo.getSensorTyp());
                            paramsUpdateSensor.put("device", deviceurl + deviceInfo.getPlattformid());//pid?


                            //String urlUpdateSensor = "http://192.168.209.189:8080/MBP/api/sensors/" + sensorInfo.getGeneratesensorid();
                            String urlUpdateSensor = url + "/api/sensors/" + sensorInfo.getGeneratesensorid();
                            JsonObjectRequest putRequestSensor = new JsonObjectRequest(Request.Method.PUT, urlUpdateSensor, paramsUpdateSensor,
                                    new Response.Listener() {
                                        @Override
                                        public void onResponse(Object response) {
                                            // response
                                            Log.d("Response", response.toString());
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            // error
                                            Log.d("Error.Response", error.toString());
                                            myToast.makeText(getApplicationContext(), "Error please check your configuration ", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                            ) {
                                //
                                @Override
                                public Map<String, String> getHeaders() throws AuthFailureError {
                                    Map<String, String> authentification = getHeaderforAuthentification();
                                    return authentification;

                                }

                            };

                            queueUpdateSensor.add(putRequestSensor);

                        }


                        if (remove) {
                            for (SensorInfo sensorInfo : sensorlistfordelete) {
                                sqLiteHelper.deletesensorId(sensorInfo.getGeneratesensorid());
                                deleterequestsensor(sensorInfo.getGeneratesensorid());
                                sensoradapter.notifyDataSetChanged();
                                Log.e("Sensor Count", "Sensor count: " + sqLiteHelper.getSensorCount());

                            }
                            sensorlistfordelete.clear();


                            for (ActuatorInfo actuatorInfo : actuatorlistfordelete) {
                                sqLiteHelper.deleteActuatorId(actuatorInfo.getGenerateActuatorId());
                                deleterequestactuator(actuatorInfo.getGenerateActuatorId());
                                actuatoradapter.notifyDataSetChanged();

                            }
                            actuatorlistfordelete.clear();

                        }


                        for (final SensorInfo sensori : testliste) {


                            final RequestQueue queue = Volley.newRequestQueue(context); // this = context

                            RequestQueue queue2 = Volley.newRequestQueue(context); // this = context
                            //String url = "http://192.168.209.189:8080/MBP/api/sensors/";
                            //String typesurl = "http://192.168.209.189:8080/MBP/api/types/";
                            //String deviceurl = "http://192.168.209.189:8080/MBP/api/devices/";
                            String urlSensors = url + "/api/sensors/";
                            String typesurl = url + "/api/adapters/";
                            String deviceurl = url + "/api/devices/";
                            JSONObject params_sensor = new JSONObject();
                            params_sensor.put("name", sensori.getName());
                            params_sensor.put("adapter", typesurl + sensori.getSensorTyp());
                            params_sensor.put("device", deviceurl + deviceInfo.getPlattformid());
                            //TODO
                            // params_sensor.put("componentType", componenttypespinner.getSelectedItem().toString());
                            params_sensor.put("componentType", sensori.getSensoradapter());

                            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                                    urlSensors, params_sensor,
                                    new Response.Listener() {
                                        @Override
                                        public void onResponse(Object response) {
                                            // response
                                            Log.d("Response Sensor", (String) response);

                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            // error
                                            Log.d("Error.Response", error.toString());
                                            myToast.makeText(getApplicationContext(), "Error please check your configuration ", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                            ) {
                                //
                                @Override
                                public Map<String, String> getHeaders() throws AuthFailureError {
                                    Map<String, String> authentification = getHeaderforAuthentification();
                                    return authentification;

                                }


                                @Override
                                protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                                    try {
                                        // Sensoren id bekommen im Head
                                        String jsonString2 = new String(response.data, "UTF-8");
                                        //String post = response.allHeaders.get(2).toString();
                                        String location = response.headers.get("Location");
                                        post = location.substring(location.lastIndexOf("/") + 1);
                                        SensorInfo sensorInf = new SensorInfo(post, sensorInfo.getName(), sensorInfo.getImage(), sensorInfo.getSensorPinset(), sensorInfo.getSensorTyp(), sensorInfo.getSensoradapter());
                                        sensorid = sqLiteHelper.createSensor(sensorInf, deviceInfo);
                                        //sensorid = sqLiteHelper.createSensor(sensorInfo, deviceInfo);


                                        return Response.success(new JSONObject(jsonString2),
                                                HttpHeaderParser.parseCacheHeaders(response));
                                    } catch (UnsupportedEncodingException e) {
                                        myToast.makeText(getApplicationContext(), "Error please check your configuration ", Toast.LENGTH_SHORT).show();
                                        return Response.error(new ParseError(e));
                                    } catch (JSONException je) {
                                        return Response.error(new ParseError(je));
                                    }
                                }
                            };
                            queue2.add(jsonObjReq);


                        }
                        testliste.removeAll(sensorlist);

                        for (final ActuatorInfo actuatorInfo : testliste2) {

                            final RequestQueue queue = Volley.newRequestQueue(context); // this = context

                            RequestQueue queue2 = Volley.newRequestQueue(context); // this = context
                            //String urlActuator = "http://192.168.209.189:8080/MBP/api/actuators/";
                            //String typesurl = "http://192.168.209.189:8080/MBP/api/types/";
                            //String deviceurl = "http://192.168.209.189:8080/MBP/api/devices/";
                            String urlActuator = url + "/api/actuators/";
                            String typesurl = url + "/api/adapters/";
                            String deviceurl = url + "/api/devices/";
                            JSONObject params_actuator = new JSONObject();
                            params_actuator.put("name", actuatorInfo.getName());
                            params_actuator.put("adapter", typesurl + actuatorInfo.getActuatorTyp());
                            params_actuator.put("device", deviceurl + deviceInfo.getPlattformid());
                            params_actuator.put("componentType", actuatorInfo.getActuatoradapter());

                            // params_actuator.put("componentType", actuatorcomponenttypespinner.getSelectedItem().toString());
                            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                                    urlActuator, params_actuator,
                                    new Response.Listener() {
                                        @Override
                                        public void onResponse(Object response) {
                                            // response
                                            Log.d("Response Sensor", (String) response);
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            // error
                                            //Log.d("Error.Response", response);
                                        }
                                    }
                            ) {
                                //
                                @Override
                                public Map<String, String> getHeaders() throws AuthFailureError {
                                    Map<String, String> authentification = getHeaderforAuthentification();
                                    return authentification;

                                }


                                @Override
                                protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                                    try {
                                        // Sensoren id bekommen im Head
                                        String jsonString2 = new String(response.data, "UTF-8");
                                        //String post = response.allHeaders.get(2).toString();
                                        String location = response.headers.get("Location");
                                        post = location.substring(location.lastIndexOf("/") + 1);
                                        ActuatorInfo aktuInf = new ActuatorInfo(post, actuatorInfo.getName(), actuatorInfo.getImage(), actuatorInfo.getActuatorPinset(), actuatorInfo.getActuatorTyp());
                                        aktuInf.setActuatoradapter(actuatorInfo.getActuatoradapter());
                                        actuatorid = sqLiteHelper.createActuator(aktuInf, deviceInfo);

                                        return Response.success(new JSONObject(jsonString2),
                                                HttpHeaderParser.parseCacheHeaders(response));
                                    } catch (UnsupportedEncodingException e) {
                                        return Response.error(new ParseError(e));
                                    } catch (JSONException je) {
                                        return Response.error(new ParseError(je));
                                    }
                                }
                            };
                            queue2.add(jsonObjReq);


                        }
                        testliste2.removeAll(actuatorlist);

                        Intent intent = new Intent(DeviceRegistryActivity.this, DeviceOverviewActivity.class);
                        startActivity(intent);
                        finish();
                        //HERE is the normal regirsty (not Edit version)
                    } else {


                        final boolean[] isInserted = new boolean[1];
                        final String name = edtName.getText().toString();
                        final String macid = edtMacid.getText().toString();
                        final byte[] image = imageViewToByte(imageView);
                        final String devicetype = spinner.getSelectedItem().toString();
                        final String optionalIP = edtOptionalId.getText().toString();
                        final String userName = edtUsername.getText().toString();
                        final String password = edtPassword.getText().toString();

                        if (myToast != null) {
                            myToast.cancel();
                        }
                        if (name.isEmpty() || devicetype.isEmpty() || optionalIP.isEmpty()) {
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "The Devicename and IP address can not be empty", Toast.LENGTH_SHORT);
                            toast.show();
                        } else {
                            /*if (!macid.isEmpty() && macid.length() < 12) {
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "Invalid Mac address", Toast.LENGTH_SHORT);
                                toast.show();
                            }*/
                            //String url = "http://192.168.209.189:8080/MBP/api/devices/";
                            String urlDevices = url + "/api/devices/";
                            final RequestQueue queue = Volley.newRequestQueue(this); // this = context
                            JSONObject params = new JSONObject();
                            params.put("name", name);
                            params.put("macAddress", macid.replace("-", ""));
                            params.put("ipAddress", optionalIP);
                            params.put("formattedMacAddress", macid);
                            params.put("username", userName);
                            params.put("password", password);
                            params.put("componentType", spinner.getSelectedItem().toString());
                            //params.put("rsaKey", "-----BEGIN RSA PRIVATE KEY-----\nMIIEqgIBAAKCAQEA4zzAJS+xXz00YLyO8lvsSfY+6XX1mQs6pz7yioA6lO30mWMx\n95FP3rZiX2stId3VfEPKdPgLot7CoTMcSnQzDBR8bi1ej8c/FXzELb2kTQzZE7dX\nYaONvNfKGL27EMjhqRpL+rQeTGeyGqmr0WH7BeQ9nE6ylfxXzXAMWkTW6dv7go+j\n52xAS6dM5TZER/A2KvCgXisiQFzwqEHuXnoy9lpWHcSZzQL8Xkd9ZbGAr3ex0pEc\n8220d4KT8oATLBDZo/fJyGiQNR5sab8RlpecbGJoh0QJIdnU3Eq02HYSzAQ7a8cB\nYGEm1xtOQOxV2a4+8f/g9FSC3hjobwmSoNu/nQIDAQABAoIBACy3ytRGk3A7mjAj\nSzo0jsZrWCwXU5KfnBZHk/FflKe0QDtjQvUGOqKIX8mJTONqRVXj/VaRbbDKh6Cz\nbzDTtyv8aBRCh2Zh/m8bE3ww4sFq8tknbmG/jugHyzSdOc/uyEG/9A3NHl1I1sra\ncv6MeprJNLqq3ggYFatPDo9BFs4EZoaIxEMD3plHfENfJOu7IS0xRoe5foXYbnM3\nji7n243OBGPAdCZXJkhYNgRoZmwMeMOJWK7EmiiM60ZKpHl8C4jSuzQ132aK/NDH\n3xgr+1nI8i8CfAWBlP8hfCXJJ8EiS5lE94jnP7u49BhjbPgULaNPDDYpVh5/uTlP\nYV5iAcECggCBAO7y4xBuMc8G57lqepoZHtCjSPpXTEC6hE7+pmnwvi6gUZPV7Tx/\nC7JecekilTy3Z+MjU1jwy7Bu0L3EJsJBn2N5aGYVxGFHGqrfA/qhPeyJsIU2w2UZ\nm1BEgNjP7bMZSMCSYd2CU9mP5dt3vGpEU6oZgwe9jm5QghYVjHaB6NUNAoIAgQDz\nc+sqdCn+rIpE6uovtqXJ9l3psaz+egRLcWS0ZVtrdhmMPBz/rZ16KhqmA+aszjiP\n8JqO3uXiB1LR+ACc6tRzeCWXNWipgzJGvLfgZBwGHeFje+uMyd1nYUq3qd0zP81j\ntpZpIAlHlPc+UREqiUhJJkjP+tEwwznP4zaC4wkQ0QKCAIEA3bMRpf73y8P2X/xB\nQJSqGJ5Haa5xm2TyuXBf6s9pRU2OIwJLmOOvcJFcUxi5Kppok0AFZvITquFGX6uM\n4pOMVPkiOgVcLX2RapR81p+gGsUtuIu1AyqdBf5pJcDWJGQDMlke4Cy5q5RtihEw\nCdDXZ21AO4BOlF+yMtdPeezSoEkCggCBAIBzsiolPp8sRIxWcpgYQ+OLBUQvxjpD\nAQ8ZVmxEancJyjMO6LIS1dtGaeccedLFwFxaNAKcIykeehllRFWHJe+C/jqJKJ8A\nJT/jhRV1XL/xdiG6ma8gN5y7XeQIUTkgOeuZxETVbXACbm3H8knCQ4ytEZADI+sZ\npuBEX1eyGO9xAoIAgQCwh2QkYnGPQkY4d9ra+WcrUz8sb7CEu4IS42XNt8SFkyU4\nfkE7aE4bHufPnnTEZ4jIGk0/E1b8AhLh1suRpg3tltYEj5CJfF1UywoUGuHhQkzP\n7jZaNQ1xdB+0woK3IenLVpDjxWGZbZTxviim1v1lpLSJxfr/HfvW1DJc4x/iug==\n-----END RSA PRIVATE KEY-----");
                            //System.out.println("TEST REQUEST DEVICE REGISTRY " + params.get("name").toString() + " " + urlDevices);

                            final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                                    urlDevices, params,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            //response.toString();
                                            System.out.print("RESPONSE von POST in Device Registry 1");
                                            //System.out.print(response.toString());
                                        }

                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            System.out.print("Error Post in Device Registry" + error.getMessage() + error.getCause());
                                            //Failure Callback

                                        }
                                    }) {
                                //
                                @Override
                                public Map<String, String> getHeaders() throws AuthFailureError {
                                    Map<String, String> authentification = getHeaderforAuthentification();
                                    return authentification;

                                }

                                @Override
                                protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                                    try {
                                        // Sensoren id bekommen im Head
                                        JSONObject result = null;

                                        // String jsonStringResponse = new String(response.data, "UTF-8");
                                        String jsonString2 = new String(response.data, "UTF-8");
                                        //String post = response.allHeaders.get(2).toString();
                                        String location = response.headers.get("Location");
                                        post = location.substring(location.lastIndexOf("/") + 1);
                                        deviceInfo = new DeviceInfo(name, macid, post, image, devicetype, optionalIP, userName, password);
                                        deviceid = sqLiteHelper.createDevice(deviceInfo);
                                        pid = post;

                                        System.out.print("RESPONSE von POST in Device Registry 2");


                                        ////////////////////////////


                                        System.out.print("TESSSST DeVICE NAme und Platt");
                                        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, urlDevices, null,
                                                new Response.Listener<JSONObject>() {
                                                    @Override
                                                    public void onResponse(JSONObject response) {
                                                        // display response
                                                        Log.d("Response", response.toString());
                                                        JSONObject obj = null;

                                                        try {
                                                            JSONObject mainObject = response;

                                                            obj = response.getJSONObject("_embedded");

                                                            Log.d("Response ", obj.toString());
                                                            JSONArray jsonArray = obj.getJSONArray("devices");
                                                            Log.d("Response Array", jsonArray.toString());
                                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                                JSONObject explrObject = jsonArray.getJSONObject(i);
                                                                // String namejson = explrObject.getString("name");
                                                                //  String macidjson = explrObject.getString("macAddress");
                                                                String plattformid = explrObject.getString("id");
                                                                // Log.d("Plattformid", "id:" + plattformid);
                                                                //pid = plattformid;
                                                            }
                                                            ArrayList<DeviceInfo> dev = sqLiteHelper.getAllDevice();
                                                            //deviceInfo= dev.get(dev.size());
                                                            deviceInfo = new DeviceInfo(name, macid, pid, image, devicetype, optionalIP, userName, password);
                                                            // deviceid = sqLiteHelper.createDevice(deviceInfo);

                                                            final String[] text = {""};


                                                            for (final SensorInfo sensorInfo : sensorlist) {

                                                                RequestQueue queue2 = Volley.newRequestQueue(context); // this = context
                                                                // String urlSensors = "http://192.168.209.189:8080/MBP/api/sensors/";
                                                                // String typesurl = "http://192.168.209.189:8080/MBP/api/types/";
                                                                // String deviceurl = "http://192.168.209.189:8080/MBP/api/devices/";
                                                                String urlSensors = url + "/api/sensors/";
                                                                String typesurl = url + "/api/adapters/";
                                                                String deviceurl = url + "/api/devices/";
                                                                JSONObject params_sensor = new JSONObject();
                                                                params_sensor.put("name", sensorInfo.getName());
                                                                params_sensor.put("adapter", typesurl + sensorInfo.getSensorTyp());
                                                                params_sensor.put("device", deviceurl + pid);
                                                                //params_sensor.put("device", deviceurl + deviceInfo.getPlattformid());
                                                                //sensorInfo.setSensoradapter(componenttypespinner.getSelectedItem().toString());
                                                                //TODO
                                                                params_sensor.put("componentType", sensorInfo.getSensoradapter());


                                                                final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                                                                        urlSensors, params_sensor,
                                                                        new Response.Listener<JSONObject>() {
                                                                            @Override
                                                                            public void onResponse(JSONObject response) {
                                                                                // response
                                                                                //Log.d("Response Sensor show ", response.toString());
                                                                                posttrue = true;
                                                                                //post = response.toString();
                                                                            }
                                                                        },
                                                                        new Response.ErrorListener() {
                                                                            @Override
                                                                            public void onErrorResponse(VolleyError error) {
                                                                                // error
                                                                                System.out.print("ERROR addind Sensor" + error.getMessage());
                                                                                // Log.d("Error.Response", response);
                                                                            }
                                                                        }
                                                                ) {
                                                                    //
                                                                    @Override
                                                                    public Map<String, String> getHeaders() throws AuthFailureError {
                                                                        Map<String, String> authentification = getHeaderforAuthentification();
                                                                        return authentification;

                                                                    }


                                                                    @Override
                                                                    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                                                                        try {
                                                                            // Sensoren id bekommen im Head
                                                                            String jsonString2 = new String(response.data, "UTF-8");
                                                                            //String post = response.allHeaders.get(2).toString();
                                                                            String location = response.headers.get("Location");
                                                                            post = location.substring(location.lastIndexOf("/") + 1);
                                                                            SensorInfo sensorInf = new SensorInfo(post, sensorInfo.getName(), sensorInfo.getImage(), sensorInfo.getSensorPinset(), sensorInfo.getSensorTyp(), sensorInfo.getSensoradapter());
                                                                            sensorid = sqLiteHelper.createSensor(sensorInf, deviceInfo);

                                                                            return Response.success(new JSONObject(jsonString2),
                                                                                    HttpHeaderParser.parseCacheHeaders(response));
                                                                        } catch (UnsupportedEncodingException e) {
                                                                            return Response.error(new ParseError(e));
                                                                        } catch (JSONException je) {
                                                                            return Response.error(new ParseError(je));
                                                                        }
                                                                    }
                                                                };

                                                                queue2.add(jsonObjReq);


                                                            }

                                                            for (final ActuatorInfo actuatorInfo : actuatorlist) {
                                                                // actuatorid = sqLiteHelper.createActuator(actuatorInfo, deviceInfo);
                                                                // Log.e("Actuator Count", "Actuator count: " + sqLiteHelper.getActuatorCount());

                                                                RequestQueue queue2 = Volley.newRequestQueue(context); // this = context
                                                                // String url = "http://192.168.209.189:8080/MBP/api/actuators/";
                                                                // String typesurl = "http://192.168.209.189:8080/MBP/api/types/";
                                                                // String deviceurl = "http://192.168.209.189:8080/MBP/api/devices/";
                                                                String urlActuators = url + "/api/actuators/";
                                                                String typesurl = url + "/api/adapters/";
                                                                String deviceurl = url + "/api/devices/";
                                                                JSONObject params_actuator = new JSONObject();
                                                                params_actuator.put("name", actuatorInfo.getName());
                                                                params_actuator.put("adapter", typesurl + actuatorInfo.getActuatorTyp());
                                                                params_actuator.put("device", deviceurl + pid);
                                                                //params_actuator.put("componentType", actuatorcomponenttypespinner.getSelectedItem().toString());
                                                                //params_actuator.put("componentType", actuatorInfo.getActuatorTyp());
                                                                params_actuator.put("componentType", actuatorInfo.getActuatoradapter());


                                                                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                                                                        urlActuators, params_actuator,
                                                                        new Response.Listener() {
                                                                            @Override
                                                                            public void onResponse(Object response) {
                                                                                // response
                                                                                Log.d("Response Actuator", (String) response);
                                                                            }
                                                                        },
                                                                        new Response.ErrorListener() {
                                                                            @Override
                                                                            public void onErrorResponse(VolleyError error) {
                                                                                // error
                                                                                //Log.d("Error.Response", response);
                                                                            }
                                                                        }
                                                                ) {
                                                                    //
                                                                    @Override
                                                                    public Map<String, String> getHeaders() throws AuthFailureError {
                                                                        Map<String, String> authentification = getHeaderforAuthentification();
                                                                        return authentification;

                                                                    }


                                                                    @Override
                                                                    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                                                                        try {
                                                                            // Sensoren id bekommen im Head
                                                                            String jsonString2 = new String(response.data, "UTF-8");
                                                                            //String post = response.allHeaders.get(2).toString();
                                                                            String location = response.headers.get("Location");
                                                                            post = location.substring(location.lastIndexOf("/") + 1);
                                                                            ActuatorInfo aktuInf = new ActuatorInfo(post, actuatorInfo.getName(), actuatorInfo.getImage(), actuatorInfo.getActuatorPinset(), actuatorInfo.getActuatorTyp());
                                                                            aktuInf.setActuatoradapter(actuatorInfo.getActuatoradapter());
                                                                            actuatorid = sqLiteHelper.createActuator(aktuInf, deviceInfo);

                                                                            return Response.success(new JSONObject(jsonString2),
                                                                                    HttpHeaderParser.parseCacheHeaders(response));
                                                                        } catch (UnsupportedEncodingException e) {
                                                                            return Response.error(new ParseError(e));
                                                                        } catch (JSONException je) {
                                                                            return Response.error(new ParseError(je));
                                                                        }
                                                                    }
                                                                };
                                                                queue2.add(jsonObjReq);

                                                            }

                                                            startActivity(new Intent(DeviceRegistryActivity.this, DeviceOverviewActivity.class));
                                                            finish();


                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }

                                                    }
                                                },
                                                new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        Log.d("Error.Response", "error");
                                                    }
                                                }
                                        ) {
                                            //
                                            @Override
                                            public Map<String, String> getHeaders() throws AuthFailureError {
                                                Map<String, String> authentification = getHeaderforAuthentification();
                                                return authentification;

                                            }

                                        };


                                        queue.add(getRequest);


                                        ////////////


                                        return Response.success(new JSONObject(jsonString2),
                                                HttpHeaderParser.parseCacheHeaders(response));
                                    } catch (UnsupportedEncodingException e) {
                                        return Response.error(new ParseError(e));
                                    } catch (JSONException je) {
                                        return Response.error(new ParseError(je));
                                    }
                                }

                            };

                            // Adding the request to the queue along with a unique string tag
                            queue.add(jsonObjReq);


                            testliste.removeAll(sensorlist);
                            // JsonObjectRequest getRequest = getJsonObjectRequestForID(name, macid, image, devicetype, optionalIP, userName, password, urlDevices);
                            //JsonObjectRequest getRequest = getJsonObjectRequestForID(name, macid, image, devicetype, optionalIP, userName, password, urlDevices);


                            //queue.add(getRequest);
                            //ToDO isinserted dont catch bugs anymore
                            isInserted[0] = true;

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


    public void deleterequestsensor(final String sensorid) {

        RequestQueue queue = Volley.newRequestQueue(this);  // this = context
        //String urlSensor = "http://192.168.209.189:8080/MBP/api/sensors/" + sensorid;
        String urlSensor = url + "/api/sensors/" + sensorid;

        StringRequest deletesensor = new StringRequest(Request.Method.DELETE, urlSensor,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        //Toast.makeText(, response, Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error.
                    }
                }
        ) {
            //
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> authentification = getHeaderforAuthentification();
                return authentification;

            }

        };
        new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error.Response", "error");
            }
        };

        // add it to the RequestQueue
        queue.add(deletesensor);
    }


    public void deleterequestactuator(final String actuatorid) {

        RequestQueue queue = Volley.newRequestQueue(this);  // this = context
        //String urlActuator = "http://192.168.209.189:8080/MBP/api/actuators/" + actuatorid;
        String urlActuator = url + "/api/actuators/" + actuatorid;

        StringRequest deleteactuator = new StringRequest(Request.Method.DELETE, urlActuator,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        //Toast.makeText(, response, Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error.

                    }
                }
        ) {
            //
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> authentification = getHeaderforAuthentification();
                return authentification;

            }

        };

        new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error.Response", "error");
            }
        };
        //TODO maybe ther must be the method authentification

// add it to the RequestQueue
        queue.add(deleteactuator);
    }

    public Map<String, String> getHeaderforAuthentification() {
        SharedPreferences sp1 = context.getSharedPreferences("Login", 0);
        String usernameSharedpref = sp1.getString("Username", null);
        String passwordSharedpref = sp1.getString("Password", null);
        String auth = new String(usernameSharedpref + ":" + passwordSharedpref);

        byte[] data = auth.getBytes();
        String base64 = Base64.encodeToString(data, Base64.NO_WRAP);
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Basic " + base64);
        //headers.put("accept-language","EN");
        headers.put("Content-Type", "application/json");
        //headers.put("Accept","application/json");
        return headers;
    }


      /*
    --------------------------Text von Qr Scan wird eingefügt----------------------------------------------
     */

    String barcode;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);

                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imageView.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        try {
            if (resultCode == RESULT_OK) {
                if (scanResult != null) {

                    barcode = scanResult.getContents();
                    JSONObject mainObject = new JSONObject(String.valueOf(barcode));


                    if (barcode.startsWith("{")) {

                        String[] tokens = barcode.split("\n");

                        for (int i = 0; i < tokens.length; i++) {
                            System.out.println("" + tokens[i]);


                            if (tokens[i].contains("type")) {

                                if (tokens[i].contains("RaspberryPi")) {
                                    spinner.setSelection(0);
                                }
                                if (tokens[i].contains("Arduino")) {
                                    spinner.setSelection(1);

                                }
                                if (tokens[i].contains("Computer")) {
                                    spinner.setSelection(2);

                                }
                                if (tokens[i].contains("Audio System")) {
                                    spinner.setSelection(3);

                                }
                                if (tokens[i].contains("Camera")) {
                                    spinner.setSelection(4);

                                }
                                if (tokens[i].contains("Gateway")) {
                                    spinner.setSelection(5);

                                }
                                if (tokens[i].contains("Laptop")) {
                                    spinner.setSelection(6);

                                }
                                if (tokens[i].contains("NodeMCU")) {
                                    spinner.setSelection(7);

                                }
                                if (tokens[i].contains("Smartphone")) {
                                    spinner.setSelection(8);

                                }
                                if (tokens[i].contains("Smartwatch")) {
                                    spinner.setSelection(9);

                                }
                                if (tokens[i].contains("TV")) {
                                    spinner.setSelection(10);

                                }
                                if (tokens[i].contains("Voice Controller")) {
                                    spinner.setSelection(11);

                                }
                               /* else{
                                    spinner.setSelection(12);

                                }*/
                            } else if (tokens[i].contains("name")) {
                                //roomNumber = tokens[i].substring(tokens[i].indexOf(":") + 3, tokens[i].indexOf(",") -1);
                                String devicename = mainObject.getString("name");

                                edtName.setText(devicename);
                            } else if (tokens[i].contains("macAddress")) {
                                //devices = tokens[i].substring(22, tokens[i].indexOf(",") -1);
                                String macAddress = mainObject.getString("macAddress");
                                edtMacid.setText(macAddress);

                            } else if (tokens[i].contains("ipAddress")) {
                                //ipAddress = tokens[i].substring(21,tokens[i].indexOf(",") -1);
                                String ipAddress = mainObject.getString("ipAddress");
                                edtOptionalId.setText(ipAddress);

                            } else if (tokens[i].contains("user")) {
                                //ipAddress = tokens[i].substring(21,tokens[i].indexOf(",") -1);
                                String username = mainObject.getString("user");
                                edtUsername.setText(username);

                            } else if (tokens[i].contains("password")) {
                                //ipAddress = tokens[i].substring(21,tokens[i].indexOf(",") -1);
                                String password = mainObject.getString("password");

                                edtPassword.setText(password);

                            } else if (tokens[i].contains("sensors")) {
                                ArrayList<String> allNames = new ArrayList<String>();

                                JSONArray cast = mainObject.getJSONArray("sensors");
                                for (int j = 0; j < cast.length(); j++) {
                                    JSONObject sensor = cast.getJSONObject(j);
                                    String sensorname = sensor.getString("sensorname");
                                    String pinset = sensor.getString("pinset");
                                    String sensorapt = sensorspinner.getSelectedItem().toString();
                                    edtSensorname.setText(sensorname);
                                    edtPinset.setText(pinset);


                                    for (int x = 0; x < sensorspinner.getCount(); x++) {
                                        String sensorspinnerItem = sensorspinner.getItemAtPosition(x).toString();
                                        if (sensorspinnerItem.toLowerCase().equals(sensor.getString("sensoradapter").toLowerCase())) {
                                            sensorspinner.setSelection(x);
                                            sensorapt = sensorspinnerItem;
                                        }

                                    }

                                    byte[] bytes;
                                    Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_lightbulb_outline_black_24dp);
                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                    bytes = stream.toByteArray();
                                    String sensortypee = "";
                                    int image = R.mipmap.ic_launcher;
                                    image = sensorTypeEquals(sensor, "Temperature", sensortypee, 0, R.drawable.icon_temperature_sensor, bytes, image);
                                    image = sensorTypeEquals(sensor, "Motion", sensortypee, 1, R.drawable.icon_motion_sensor, bytes, image);
                                    image = sensorTypeEquals(sensor, "Camera", sensortypee, 2, R.drawable.icon_camera_sensor, bytes, image);
                                    image = sensorTypeEquals(sensor, "Location", sensortypee, 3, R.drawable.icon_location_sensor, bytes, image);
                                    image = sensorTypeEquals(sensor, "Gas", sensortypee, 4, R.drawable.icon_gas_sensor, bytes, image);
                                    image = sensorTypeEquals(sensor, "Sound", sensortypee, 5, R.drawable.icon_sound_sensor, bytes, image);
                                    image = sensorTypeEquals(sensor, "Touch", sensortypee, 6, R.drawable.icon_touch_sensor, bytes, image);
                                    image = sensorTypeEquals(sensor, "Humidity", sensortypee, 7, R.drawable.icon_humidity_sensor, bytes, image);
                                    image = sensorTypeEquals(sensor, "Vibration", sensortypee, 8, R.drawable.icon_vibration_sensor, bytes, image);
                                    image = sensorTypeEquals(sensor, "Gyroscope", sensortypee, 9, R.drawable.icon_gyroscope_sensor, bytes, image);
                                    image = sensorTypeEquals(sensor, "Proximity", sensortypee, 10, R.drawable.icon_proximity_sensor, bytes, image);
                                    image = sensorTypeEquals(sensor, "Acceleration", sensortypee, 11, R.drawable.icon_default_sensor, bytes, image);
                                    image = sensorTypeEquals(sensor, "Light Flicker", sensortypee, 12, R.drawable.icon_light_sensor, bytes, image);

                                    // sensorInfo = new SensorInfo(sensorname, bytes, pinset, );
                                    //sensorInfo = new SensorInfo(post, sensorname, bytes, pinset, sensortypee, sensorapt);
                                    sensorbtnAddclicked(sensorapt, image);
                                    //sensorlist.add(sensorInfo);

                                    sensoradapter.notifyDataSetChanged();
                                    edtSensorname.setText("");
                                    edtPinset.setText("");
                                }
                            } else if (tokens[i].contains("actuators")) {
                                ArrayList<String> allNames = new ArrayList<String>();

                                JSONArray cast = mainObject.getJSONArray("actuators");
                                for (int j = 0; j < cast.length(); j++) {
                                    JSONObject actuator = cast.getJSONObject(j);
                                    String actuatorname = actuator.getString("actuatorname");
                                    String pinset = actuator.getString("pinset");
                                    String actuatorapt = actuatorspinner.getSelectedItem().toString();
                                    edtActuatorname.setText(actuatorname);
                                    edtPinsetActuator.setText(pinset);


                                    for (int x = 0; x < actuatorspinner.getCount(); x++) {
                                        String actuatorspinnerItem = actuatorspinner.getItemAtPosition(x).toString();
                                        if (actuatorspinnerItem.equals(actuator.getString("actuatoradapter"))) {
                                            actuatorspinner.setSelection(x);
                                            actuatorapt = actuatorspinnerItem;

                                        }

                                    }

                                    byte[] actuatorimageqr;
                                    Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_lightbulb_outline_black_24dp);
                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                    actuatorimageqr = stream.toByteArray();
                                    int image = R.mipmap.ic_launcher;
                                    image = actuatorTypeEqual(actuator, "Speaker", 0, R.drawable.icon_speaker_actuator, actuatorimageqr, image);
                                    image = actuatorTypeEqual(actuator, "Light", 1, R.drawable.icon_light_actuator, actuatorimageqr, image);
                                    image = actuatorTypeEqual(actuator, "Vibration", 2, R.drawable.icon_vibration_actuator, actuatorimageqr, image);
                                    image = actuatorTypeEqual(actuator, "Motor", 3, R.drawable.icon_motor_actuator, actuatorimageqr, image);
                                    image = actuatorTypeEqual(actuator, "Heater", 4, R.drawable.icon_heater_actuator, actuatorimageqr, image);
                                    image = actuatorTypeEqual(actuator, "Air Conditioner", 5, R.drawable.icon_air_conditioner_actuator, actuatorimageqr, image);
                                    image = actuatorTypeEqual(actuator, "Buzer", 6, R.drawable.icon_buzzer_actuator, actuatorimageqr, image);
                                    image = actuatorTypeEqual(actuator, "Switch", 7, R.drawable.icon_switch_actuator, actuatorimageqr, image);
                                    image = actuatorTypeEqual(actuator, "LED", 8, R.drawable.icon_led_actuator, actuatorimageqr, image);

                                    //actuatorInfo = new ActuatorInfo(actuatorname, actuatorimageqr, pinset);
                                    //actuatorlist.add(actuatorInfo);
                                    actuatorbtnAddclicked(actuatorapt, image);

                                    actuatoradapter.notifyDataSetChanged();
                                    edtActuatorname.setText("");
                                    edtPinsetActuator.setText("");
                                }
                            }


                        }
                    } else {
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "No scan data received!", Toast.LENGTH_SHORT);
                        toast.show();
                    }


                } else {
                    // Handle cancel resultCode == RESULT_CANCELED &&
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "No scan data received!", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

        } catch (ActivityNotFoundException e) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private int actuatorTypeEqual(JSONObject actuator, String led, int i2, int p, byte[] bytes, int image) throws JSONException {
        //String actuatorname;
        //String pinset;
        //int image = R.mipmap.ic_launcher;
        if (actuator.getString("actuatortype").equals(led)) {
            componenttypespinner.setSelection(i2);
            //  actuatorname = edtActuatorname.getText().toString();
            //pinset = edtPinset.getText().toString();

            //String actuatorname = actuatorspinner.getSelectedItem();
          /*  Bitmap bmp = BitmapFactory.decodeResource(getResources(), p);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            bytes = stream.toByteArray();

            actuatorimage = bytes;*/
            image = p;

        }
        return image;
    }

    private int sensorTypeEquals(JSONObject sensor, String vibration, String sensortyp, int i2, int p, byte[] bytes, int image) throws JSONException {
        String sensorname;
        String pinset;
        //int image = R.mipmap.ic_launcher;
        if (sensor.getString("sensortype").equals(vibration)) {
            sensortyp = vibration;
            componenttypespinner.setSelection(i2);
            /*sensorname = edtSensorname.getText().toString();
            pinset = edtPinset.getText().toString();

            //String sensorname = sensorspinner.getSelectedItem();

            Bitmap bmp = BitmapFactory.decodeResource(getResources(), p);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            bytes = stream.toByteArray();

            sensorimage = bytes;*/
            image = p;


        }
        return image;
    }


    /**
     * -----------Class for the animation of View for Raspberry/Arduino etc.--------------------------------------
     */
    public static class ResizeAnimation extends Animation {
        final int targetHeight;
        View view;
        int startHeight;

        public ResizeAnimation(View view, int targetHeight, int startHeight) {
            this.view = view;
            this.targetHeight = targetHeight;
            this.startHeight = startHeight;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            int newHeight = (int) (startHeight + targetHeight * interpolatedTime);
            //to support decent animation, change new heigt as Nico S. recommended in comments
            //int newHeight = (int) (startHeight+(targetHeight - startHeight) * interpolatedTime);
            view.getLayoutParams().height = newHeight;
            view.requestLayout();
        }

        @Override
        public void initialize(int width, int height, int parentWidth, int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
        }

        @Override
        public boolean willChangeBounds() {
            return true;
        }
    }

    void disableInput(EditText editText){
        editText.setInputType(InputType.TYPE_NULL);
        editText.setTextIsSelectable(false);
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return true;  // Blocks input from hardware keyboards.
            }
        });
    }
}
