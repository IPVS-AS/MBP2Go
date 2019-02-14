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
import android.text.TextWatcher;
import android.util.Log;
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
    Spinner sensorspinner, actuatorspinner;
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


        //sensorspinner = (Spinner) findViewById(R.id.spinner);
        btnaddActuator = (Button) findViewById(R.id.btn_addActuator);

        actuatorspinner = (Spinner) findViewById(R.id.spinneractuator);


        actuatorspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                final String selectedItem = parent.getItemAtPosition(position).toString();
                //edtSensorname.setText(selectedItem);


                if (selectedItem.contains("Temperature")) {

                    btnaddActuator.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            actuatorbtnAddclicked(selectedItem, R.drawable.temperature_icon);


                        }
                    });


                } else if (selectedItem.contains("Pressure")) {
                    Toast.makeText(getApplicationContext(), "Pressure wurde gewählt", Toast.LENGTH_SHORT).show();

                    btnaddActuator.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            actuatorbtnAddclicked(selectedItem, R.drawable.pressure_icon);


                        }
                    });
                } else if (selectedItem.contains("GPS")) {

                    btnaddActuator.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            actuatorbtnAddclicked(selectedItem, R.drawable.ic_place_black_24dp);

                        }
                    });
                } else if (selectedItem.contains("Light")) {

                    btnaddActuator.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            actuatorbtnAddclicked(selectedItem, R.drawable.ic_lightbulb_outline_black_24dp);

                        }
                    });
                } else {
                    btnaddActuator.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            actuatorbtnAddclicked(selectedItem, R.mipmap.ic_launcher);

                        }
                    })
                    ;
                }
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

        sensorspinner = (Spinner) findViewById(R.id.spinner);
        btnaddSensor = (Button) findViewById(R.id.btn_addSensor);


        sensorspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                final String selectedItem = parent.getItemAtPosition(position).toString();
                //edtSensorname.setText(selectedItem);
                //Toast.makeText(getApplicationContext(), "Temperatur wurde gewählt", Toast.LENGTH_SHORT).show();


                if (selectedItem.contains("Temperature")) {
                    Toast.makeText(getApplicationContext(), "Temperatur wurde gewählt", Toast.LENGTH_SHORT).show();

                    btnaddSensor.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            sensorbtnAddclicked(selectedItem, R.drawable.temperature_icon);

                        }
                    });


                } else if (selectedItem.contains("Pressure")) {
                    Toast.makeText(getApplicationContext(), "Pressure wurde gewählt", Toast.LENGTH_SHORT).show();

                    btnaddSensor.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            sensorbtnAddclicked(selectedItem, R.drawable.pressure_icon);


                        }
                    });
                } else if (selectedItem.contains("GPS")) {

                    btnaddSensor.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            sensorbtnAddclicked(selectedItem, R.drawable.ic_place_black_24dp);


                        }
                    });
                } else if (selectedItem.contains("Light")) {
                    btnaddSensor.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            sensorbtnAddclicked(selectedItem, R.drawable.ic_lightbulb_outline_black_24dp);


                        }
                    });
                } else {
                    btnaddSensor.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            sensorbtnAddclicked(selectedItem, R.mipmap.ic_launcher);


                        }
                    });
                }
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
                        deviceImage.setImageResource(R.drawable.raspberry_pi);

                    } else if (deviceType.getSelectedItem().equals("Arduino")) {

                        deviceImage.setImageResource(R.drawable.arduino);
                    } else if (deviceType.getSelectedItem().equals("Other")) {
                        deviceImage.setImageResource(R.mipmap.ic_launcher);

                    } else {
                        deviceImage.setImageResource(R.mipmap.ic_launcher);
                    }
                } // to close the onItemSelected


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
                    Toast.makeText(getApplicationContext(), "Raspberry wurde gewählt", Toast.LENGTH_SHORT).show();
                    vf.setDisplayedChild(1);
                    imageView.setImageResource(R.drawable.raspberry_pi);


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
                if (selectedItem.equals("Arduino")) {
                    startHeight = 100;
                    targetHeight = 100;

                    Toast.makeText(getApplicationContext(), "Arduino wurde gewählt", Toast.LENGTH_SHORT).show();
                    vf.setDisplayedChild(2);
                    imageView.setImageResource(R.drawable.arduino);
                    ResizeAnimation resizeAnimation = new ResizeAnimation(
                            relativeLayout,
                            targetHeight,
                            startHeight
                    );
                    resizeAnimation.setDuration(duration);
                    relativeLayout.startAnimation(resizeAnimation);


                }
                if (selectedItem.equals("Other")) {
                    startHeight = 100;
                    targetHeight = 100;

                    Toast.makeText(getApplicationContext(), "Other wurde gewählt", Toast.LENGTH_SHORT).show();
                    vf.setDisplayedChild(2);
                    imageView.setImageResource(R.mipmap.ic_launcher);
                    ResizeAnimation resizeAnimation = new ResizeAnimation(
                            relativeLayout,
                            targetHeight,
                            startHeight
                    );
                    resizeAnimation.setDuration(duration);
                    relativeLayout.startAnimation(resizeAnimation);


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
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url+"/api/types", null,
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
                            JSONArray jsonArray = obj.getJSONArray("types");
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
        );

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
                    myToast.makeText(getApplicationContext(), "is clicked", Toast.LENGTH_SHORT).show();


                    if (getIntent().hasExtra("macid") || getIntent().hasExtra("maciddetail")) {
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
                        String urlUpdate = url+ "/api/devices/" + deviceInfo.getPlattformid();
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
                                    }
                                }
                        ) {


                        };

                        queueUpdate.add(putRequest);


                        //sqLiteHelper.updateSensor(sensorInfo, deviceInfo);

                        ArrayList<SensorInfo> deviceWatchList = sqLiteHelper.getAllSensorsByPlattformId(deviceInfo.getPlattformid());
                        for(SensorInfo sensorInfo: deviceWatchList){
                            RequestQueue queueUpdateSensor = Volley.newRequestQueue(context);
                            JSONObject paramsUpdateSensor = new JSONObject();
                            //String url = "http://192.168.209.189:8080/MBP/api/sensors/";
                            //String typesurl = "http://192.168.209.189:8080/MBP/api/types/";
                            //String deviceurl = "http://192.168.209.189:8080/MBP/api/devices/";
                            String typesurl = url+"/api/types/";
                            String deviceurl = url+"/api/devices/";
                            paramsUpdateSensor.put("name", sensorInfo.getName());
                            paramsUpdateSensor.put("type", typesurl + sensorInfo.getSensorTyp());
                            paramsUpdateSensor.put("device", deviceurl + deviceInfo.getPlattformid());


                            //String urlUpdateSensor = "http://192.168.209.189:8080/MBP/api/sensors/" + sensorInfo.getGeneratesensorid();
                            String urlUpdateSensor = url+ "/api/sensors/" + sensorInfo.getGeneratesensorid();
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
                                        }
                                    }
                            ) {


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
                            String typesurl = url+"/api/types/";
                            String deviceurl = url+ "/api/devices/";
                            JSONObject params_sensor = new JSONObject();
                            params_sensor.put("name", sensori.getName());
                            params_sensor.put("type", typesurl + sensori.getSensorTyp());
                            params_sensor.put("device", deviceurl + deviceInfo.getPlattformid());
                            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                                    urlSensors, params_sensor,
                                    new Response.Listener() {
                                        @Override
                                        public void onResponse(Object response) {
                                            // response
                                            Log.d("Response Sensor", (String) response);
                                            //sqLiteHelper.createSensor(sensori, deviceInfo);

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
                                @Override
                                protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                                    try {
                                        // Sensoren id bekommen im Head
                                        String jsonString2 = new String(response.data, "UTF-8");
                                        //String post = response.allHeaders.get(2).toString();
                                        String location = response.headers.get("Location");
                                        post = location.substring(location.lastIndexOf("/") + 1);
                                        SensorInfo sensorInf = new SensorInfo(post, sensorInfo.getName(), sensorInfo.getImage(), sensorInfo.getSensorPinset(), sensorInfo.getSensorTyp());
                                        sensorid = sqLiteHelper.createSensor(sensorInf, deviceInfo);
                                        //sensorid = sqLiteHelper.createSensor(sensorInfo, deviceInfo);


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
                        testliste.removeAll(sensorlist);

                        for (final ActuatorInfo actuatorInfo : testliste2) {

                            final RequestQueue queue = Volley.newRequestQueue(context); // this = context

                            RequestQueue queue2 = Volley.newRequestQueue(context); // this = context
                            //String urlActuator = "http://192.168.209.189:8080/MBP/api/actuators/";
                            //String typesurl = "http://192.168.209.189:8080/MBP/api/types/";
                            //String deviceurl = "http://192.168.209.189:8080/MBP/api/devices/";
                            String urlActuator = url+"/api/actuators/";
                            String typesurl = url+"/api/types/";
                            String deviceurl = url+"/api/devices/";
                            JSONObject params_actuator = new JSONObject();
                            params_actuator.put("name", actuatorInfo.getName());
                            params_actuator.put("type", typesurl + actuatorInfo.getActuatorTyp());
                            params_actuator.put("device", deviceurl + deviceInfo.getPlattformid());
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
                                @Override
                                protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                                    try {
                                        // Sensoren id bekommen im Head
                                        String jsonString2 = new String(response.data, "UTF-8");
                                        //String post = response.allHeaders.get(2).toString();
                                        String location = response.headers.get("Location");
                                        post = location.substring(location.lastIndexOf("/") + 1);
                                        ActuatorInfo aktuInf = new ActuatorInfo(post, actuatorInfo.getName(), actuatorInfo.getImage(), actuatorInfo.getActuatorPinset(), actuatorInfo.getActuatorTyp());
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

                        //String url = "http://192.168.209.189:8080/MBP/api/devices/";
                        String urlDevices = url+"/api/devices/";
                        final RequestQueue queue = Volley.newRequestQueue(this); // this = context
                        JSONObject params = new JSONObject();
                        params.put("name", name);
                        params.put("macAddress", macid.replace("-", ""));
                        params.put("ipAddress", url);
                        params.put("formattedMacAddress", macid);
                        params.put("username", userName);


                        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                                urlDevices, params,
                                new Response.Listener() {
                                    @Override
                                    public void onResponse(Object response) {

                                    }

                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                        //Failure Callback

                                    }
                                });

                        // Adding the request to the queue along with a unique string tag
                        queue.add(jsonObjReq);


                        testliste.removeAll(sensorlist);



                             /*
                    get request to get the id
                    */
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
                                                String namejson = explrObject.getString("name");
                                                String macidjson = explrObject.getString("macAddress");
                                                String plattformid = explrObject.getString("id");
                                                Log.d("Plattformid", "id:" + plattformid);
                                                pid = plattformid;
                                            }
                                            deviceInfo = new DeviceInfo(name, macid, pid, image, devicetype, optionalIP, userName, password);
                                            deviceid = sqLiteHelper.createDevice(deviceInfo);

                                            final String[] text = {""};


                                            for (final SensorInfo sensorInfo : sensorlist) {

                                                RequestQueue queue2 = Volley.newRequestQueue(context); // this = context
                                               // String urlSensors = "http://192.168.209.189:8080/MBP/api/sensors/";
                                               // String typesurl = "http://192.168.209.189:8080/MBP/api/types/";
                                               // String deviceurl = "http://192.168.209.189:8080/MBP/api/devices/";
                                                String urlSensors = url+ "/api/sensors/";
                                                String typesurl = url+"/api/types/";
                                                String deviceurl = url+"/api/devices/";
                                                JSONObject params_sensor = new JSONObject();
                                                params_sensor.put("name", sensorInfo.getName());
                                                params_sensor.put("type", typesurl + sensorInfo.getSensorTyp());
                                                params_sensor.put("device", deviceurl + pid);

                                                final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                                                        urlSensors, params_sensor,
                                                        new Response.Listener<JSONObject>() {
                                                            @Override
                                                            public void onResponse(JSONObject response) {
                                                                // response
                                                                Log.d("Response Sensor show ", response.toString());
                                                                posttrue = true;
                                                                //post = response.toString();
                                                            }
                                                        },
                                                        new Response.ErrorListener() {
                                                            @Override
                                                            public void onErrorResponse(VolleyError error) {
                                                                // error
                                                                // Log.d("Error.Response", response);
                                                            }
                                                        }
                                                ) {
                                                    @Override
                                                    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                                                        try {
                                                            // Sensoren id bekommen im Head
                                                            String jsonString2 = new String(response.data, "UTF-8");
                                                            //String post = response.allHeaders.get(2).toString();
                                                            String location = response.headers.get("Location");
                                                            post = location.substring(location.lastIndexOf("/") + 1);
                                                            SensorInfo sensorInf = new SensorInfo(post, sensorInfo.getName(), sensorInfo.getImage(), sensorInfo.getSensorPinset(), sensorInfo.getSensorTyp());
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
                                                String urlActuators = url+ "/api/actuators/";
                                                String typesurl = url+"/api/types/";
                                                String deviceurl = url+"/api/devices/";
                                                JSONObject params_actuator = new JSONObject();
                                                params_actuator.put("name", actuatorInfo.getName());
                                                params_actuator.put("type", typesurl + actuatorInfo.getActuatorTyp());
                                                params_actuator.put("device", deviceurl + pid);
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
                                                    @Override
                                                    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                                                        try {
                                                            // Sensoren id bekommen im Head
                                                            String jsonString2 = new String(response.data, "UTF-8");
                                                            //String post = response.allHeaders.get(2).toString();
                                                            String location = response.headers.get("Location");
                                                            post = location.substring(location.lastIndexOf("/") + 1);
                                                            ActuatorInfo aktuInf = new ActuatorInfo(post, actuatorInfo.getName(), actuatorInfo.getImage(), actuatorInfo.getActuatorPinset(), actuatorInfo.getActuatorTyp());
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
                        );
                        queue.add(getRequest);
                        //ToDO isinserted dont catch bugs anymore
                        isInserted[0] = true;

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
        String urlSensor = url+"/api/sensors/" + sensorid;

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
        );
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
        String urlActuator = url+"/api/actuators/" + actuatorid;

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
        );

        new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error.Response", "error");
            }
        };

// add it to the RequestQueue
        queue.add(deleteactuator);
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
                                if (tokens[i].contains("Other")) {
                                    spinner.setSelection(2);

                                }
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

                            } else if (tokens[i].contains("sensors")) {
                                ArrayList<String> allNames = new ArrayList<String>();

                                JSONArray cast = mainObject.getJSONArray("sensors");
                                for (int j = 0; j < cast.length(); j++) {
                                    JSONObject sensor = cast.getJSONObject(j);
                                    String sensorname = sensor.getString("sensorname");
                                    String pinset = sensor.getString("pinset");
                                    edtSensorname.setText(sensorname);
                                    edtPinset.setText(pinset);


                                    if (sensor.getString("sensortype").equals("Temperatur")) {
                                        sensorspinner.setSelection(0);
                                        sensorname = edtSensorname.getText().toString();
                                        pinset = edtPinset.getText().toString();

                                        //String sensorname = sensorspinner.getSelectedItem();
                                        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.temperature_icon);
                                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                        byte[] byteArray = stream.toByteArray();

                                        sensorimage = byteArray;

                                        sensorInfo = new SensorInfo(sensorname, byteArray, pinset);
                                        sensorlist.add(sensorInfo);

                                        sensoradapter.notifyDataSetChanged();

                                    }
                                    if (sensor.getString("sensortype").equals("Pressure")) {
                                        sensorspinner.setSelection(1);
                                        String name = edtSensorname.getText().toString();
                                        pinset = edtPinset.getText().toString();

                                        //String sensorname = sensorspinner.getSelectedItem();
                                        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.pressure_icon);
                                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                        byte[] byteArray = stream.toByteArray();

                                        sensorimage = byteArray;

                                        sensorInfo = new SensorInfo(name, byteArray, pinset);
                                        sensorlist.add(sensorInfo);

                                        sensoradapter.notifyDataSetChanged();

                                    }
                                    if (sensor.getString("sensortype").equals("GPS")) {
                                        sensorspinner.setSelection(2);
                                        sensorname = edtSensorname.getText().toString();
                                        pinset = edtPinset.getText().toString();

                                        //String sensorname = sensorspinner.getSelectedItem();
                                        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_place_black_24dp);
                                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                        byte[] byteArray = stream.toByteArray();

                                        sensorimage = byteArray;

                                        sensorInfo = new SensorInfo(sensorname, byteArray, pinset);
                                        sensorlist.add(sensorInfo);

                                        sensoradapter.notifyDataSetChanged();

                                    }
                                    if (sensor.getString("sensortype").equals("Lights")) {
                                        sensorspinner.setSelection(3);
                                        sensorname = edtSensorname.getText().toString();
                                        pinset = edtPinset.getText().toString();

                                        //String sensorname = sensorspinner.getSelectedItem();
                                        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_lightbulb_outline_black_24dp);
                                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                        byte[] byteArray = stream.toByteArray();

                                        sensorimage = byteArray;

                                        sensorInfo = new SensorInfo(sensorname, byteArray, pinset);
                                        sensorlist.add(sensorInfo);

                                        sensoradapter.notifyDataSetChanged();

                                    }
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
}