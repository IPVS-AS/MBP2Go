package com.example.sedaulusal.hiwijob.device;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.sedaulusal.hiwijob.MainActivity;
import com.example.sedaulusal.hiwijob.R;
import com.example.sedaulusal.hiwijob.device.advertise.DeviceFinderActivity;
import com.github.brnunes.swipeablerecyclerview.SwipeableRecyclerViewTouchListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import static com.example.sedaulusal.hiwijob.SettingActivity.mypreference;


public class DeviceOverviewActivity extends AppCompatActivity {


    private RecyclerView mRecyclerView;
    ArrayList<DeviceInfo> devicelist, testliste, results;
    ArrayList<SensorInfo> sensorlist;
    ArrayList<ActuatorInfo> actuatorlist;


    RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    DeviceRegistryActivity registryActivity;
    SQLiteHelper db;
    Context context;
    DeviceInfo deviceInfo;
    Cursor cursor;
    EditText versuch;
    int position1;
    int position_stelle;
    DetailActivity detailActivity;

    ArrayList<DeviceInfo> deviceListWithDevicesFromPlattfrom = new ArrayList<>();
    ArrayList<SensorInfo> senList = new ArrayList<>();
    ArrayList<ActuatorInfo> aktuList = new ArrayList<>();


    DeviceInfo devicein;

    ImageButton synchroRequest;
    Button deployRequest;
    String device_plattformid;


    // ProgressDialog dialog;

    //String url = "http://192.168.209.189:8080/MBP/api/devices";
    //String url = "http://192.168.209.189:8080/MBP/api/devices";
    //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    //SharedPreferences prefs = getPreferences(MODE_PRIVATE);
    SharedPreferences sharedPreferences;
    String url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_overview);
        context = this;

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) findViewById(R.id.searchId);
        CharSequence query = searchView.getQuery(); // get the query string currently in the text field

        searchView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                newText = newText.toLowerCase();
                ArrayList<DeviceInfo> newList = new ArrayList<DeviceInfo>();
                for (DeviceInfo deviceInfo : devicelist) {
                    String name = deviceInfo.getName().toLowerCase();
                    if (name.contains(newText)) {
                        newList.add(deviceInfo);
                        mAdapter = new MyAdapter(newList, cursor, context);
                        mRecyclerView.setAdapter(mAdapter);

                    }
                }

                mAdapter.notifyDataSetChanged();

                return false;
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DeviceOverviewActivity.this, DeviceFinderActivity.class));

            }
        });


        //sharedPreferences = getSharedPreferences(mypreference,
          //      Context.MODE_PRIVATE);
        //url = sharedPreferences.getString("URL", "");

        //String url = prefs.getString("URL", "");
        //sharedPreferences
        //url = sharedPreferences.getString("URL", "");
        sharedPreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        url = sharedPreferences.getString("URL", "");

        //SettingActivity settingActivity = new SettingActivity();
        //settingActivity.getUrltest();


        synchroRequest = (ImageButton) findViewById(R.id.imgbtn_synchrorequest);
        deployRequest = (Button) findViewById(R.id.switch_deploy);
        getrequest();


        detailActivity = new DetailActivity();


        db = new SQLiteHelper(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);


        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        devicelist = new ArrayList<>();
        testliste = new ArrayList<>();
        results = new ArrayList<>();

        registryActivity = new DeviceRegistryActivity();
        sensorlist = new ArrayList<>();
        actuatorlist = new ArrayList<>();


        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(devicelist, cursor, context);
        mRecyclerView.setAdapter(mAdapter);


        cursor = db.getAllData();
        devicelist.clear();
        while (cursor.moveToNext()) {

            int id = cursor.getInt(cursor.getColumnIndex(SQLiteHelper.KEY_ID));
            String name = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_NAME));
            String macid = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_MACID));
            String plattformid = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_PLATTFORMID));
            byte[] image = cursor.getBlob(cursor.getColumnIndex(SQLiteHelper.KEY_IMAGE));
            String devicetype = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_DEVICETYPE));
            String optinalIP = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_OPTIONALIP));
            String userName = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_USERNAME));
            String password = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_PASSWORD));

            //id gelöscht
            deviceInfo = new DeviceInfo(id, plattformid, name, macid, image, devicetype, optinalIP, userName, password);
            // Log.d("Hiwi","device info: "+id+" "+name+" "+macid+" "+sensorId);
            Log.d("Hiwi", "device info: " + " " + name + " " + macid + " ");
            devicelist.add(deviceInfo);
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

                DeviceInfo deviceInfo = devicelist.get(position);
                //Toast.makeText(getApplicationContext(), deviceInfo.getName() + " is selected! " + deviceInfo.getId() + " Plattformid" + deviceInfo.getPlattformid() + " sensorcount " + db.getSensorCount() + " actuatorcount " + db.getActuatorCount(), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(DeviceOverviewActivity.this, DetailActivity.class);
                devicelist.get(position).getName();
                registryActivity.sensorlist = db.getAllSensor();
                registryActivity.actuatorlist = db.getAllActuator();
                intent.putExtra("name", devicelist.get(position).getName());
                intent.putExtra("position", position);
                intent.putExtra("macid", devicelist.get(position).getMacid());
                intent.putExtra("plattform", devicelist.get(position).getPlattformid());
                intent.putExtra("devicetype", devicelist.get(position).getDevicetype());
                intent.putExtra("optionalid", devicelist.get(position).getOptionalIP());
                intent.putExtra("userName", devicelist.get(position).getUsername());
                intent.putExtra("password", devicelist.get(position).getPassword());
                intent.putExtra("deviceid", devicelist.get(position).getPlattformid());


                Bundle bundle = new Bundle();
                intent.putExtras(bundle);

                Bundle extras = new Bundle();
                extras.putByteArray("imagebitmap", devicelist.get(position).getImage());
                intent.putExtras(extras);
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

                                        Intent intent = new Intent(DeviceOverviewActivity.this, DeviceRegistryActivity.class);
                                        devicelist.get(position).getName();
                                        registryActivity.sensorlist = db.getAllSensorsByPlattformId(devicelist.get(position).getPlattformid());
                                        registryActivity.actuatorlist = db.getAllActuatorsByDevicePlattform(devicelist.get(position).getPlattformid());
                                        intent.putExtra("name", devicelist.get(position).getName());
                                        intent.putExtra("macid", devicelist.get(position).getMacid());
                                        intent.putExtra("plattform", devicelist.get(position).getPlattformid());

                                        intent.putExtra("deviceid", devicelist.get(position).getId());
                                        intent.putExtra("position", position);

                                        Bundle bundle = new Bundle();
                                        intent.putExtras(bundle);

                                        startActivity(intent);
                                        break;
                                    case 1:
                                        //TODO db.delete(deviceInfo.getId()); geht nicht mehr wegen string
                                        //kann das so bleiben??
                                        Log.d("Tag Count",
                                                "Tag Count Before Deleting 'Shopping' Todos: "
                                                        + db.getSensorCount());

                                        deleterequest(devicelist.get(position).getPlattformid());
                                        db.deletesensorplattformid(devicelist.get(position).getPlattformid());
                                        db.deleteActuatorbyPlattform(devicelist.get(position).getPlattformid());
                                        db.deleteDevice(devicelist.get(position));
                                        // Deleting all Todos under "Shopping" tag
                                        Log.d(" Count",
                                                " Count after Deleting 'Shopping' Todos: "
                                                        + db.getSensorCount());

                                        devicelist.remove(position);
                                        mAdapter.notifyItemRemoved(position);


                                        break;
                                }
                            }
                        }).create().show();

            }
        }));


        SwipeableRecyclerViewTouchListener swipeTouchListener =
                new SwipeableRecyclerViewTouchListener(mRecyclerView,
                        new SwipeableRecyclerViewTouchListener.SwipeListener() {
                            @Override
                            public boolean canSwipeLeft(int position) {
                                return true;
                            }

                            @Override
                            public boolean canSwipeRight(int position) {
                                return true;
                            }

                            @Override
                            public void onDismissedBySwipeLeft(RecyclerView recyclerView, int[] reverseSortedPositions) {

                                for (int position : reverseSortedPositions) {
                                    //TODO db.delete(deviceInfo.getId()); geht nicht mehr wegen string

                                    Log.d("Tag Count",
                                            "Tag Count Before Deleting 'Shopping' Todos: "
                                                    + db.getSensorCount());
                                    deleterequest(devicelist.get(position).getPlattformid());
                                    db.deletesensorplattformid(devicelist.get(position).getPlattformid());
                                    db.deleteActuatorbyPlattform(devicelist.get(position).getPlattformid());
                                    db.deleteDevice(devicelist.get(position));

                                    // Deleting all Todos under "Shopping" tag
                                    Log.d(" Count",
                                            " Count after Deleting 'Shopping' Todos: "
                                                    + db.getSensorCount());

                                    devicelist.remove(position);
                                    mAdapter.notifyItemRemoved(position);
                                }
                                mAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    deleterequest(devicelist.get(position).getPlattformid());
                                    db.deletesensorplattformid(devicelist.get(position).getPlattformid());
                                    db.deleteActuatorbyPlattform(devicelist.get(position).getPlattformid());
                                    db.deleteDevice(devicelist.get(position));

                                    devicelist.remove(position);
                                    mAdapter.notifyItemRemoved(position);
                                }
                                mAdapter.notifyDataSetChanged();
                            }
                        });

        mRecyclerView.addOnItemTouchListener(swipeTouchListener);

        if (savedInstanceState == null) {
            Log.d("isclickedtrue", "true");
            Bundle extras = getIntent().getExtras();
            if (extras == null) {

            } else {

                boolean isclick = extras.getBoolean("isclicked");
                position1 = extras.getInt("position1");
                Log.d("position 1 ist", " " + position1);


                if (isclick) {
                    db.deletesensorplattformid(deviceInfo.getMacid());
                    db.deleteActuatorbyPlattform(deviceInfo.getPlattformid());
                    db.deleteDevice(devicelist.get(position1));

                    devicelist.remove(position1);
                    mAdapter.notifyItemRemoved(position1);


                }
            }
        }

    }


    public synchronized void synchrorequest(View v) {
        Toast.makeText(getApplicationContext(), "Please wait, synchronizing Data...", Toast.LENGTH_SHORT).show();

        getrequest();

    }



    private synchronized void getrequest() {
        //dialog = ProgressDialog.show(context, "Synchronizing Data", "Please wait...", true);

        RequestQueue queue = Volley.newRequestQueue(this);  // this = context

        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url+"/api/devices", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("Response", response.toString());
                        JSONObject obj = null;
                        // devicelist.clear();
                        // db.deleteall();


                        try {
                            JSONObject mainObject = response;

                            obj = response.getJSONObject("_embedded");

                            Log.d("Response ", obj.toString());
                            JSONArray jsonArray = obj.getJSONArray("devices");
                            Log.d("Response Array", jsonArray.toString());


                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject explrObject = jsonArray.getJSONObject(i);
                                String name = explrObject.getString("name");
                                String macid = explrObject.getString("macAddress");
                                String plattformid = explrObject.getString("id");
                                String username = explrObject.getString("username");
                                String ipAddress = explrObject.getString("ipAddress");
                                Log.d("Plattformid", "id:" + plattformid);
                                byte[] image;

                                Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                byte[] byteArray = stream.toByteArray();
                                image = byteArray;

                                //TODO device username
                                deviceInfo = new DeviceInfo(name, macid, plattformid, image, username, ipAddress);
                                //neues DeviceInfo weil das Image beim compare nicht übereinstimmt
                                devicein = new DeviceInfo(name, macid, plattformid);

                                deviceListWithDevicesFromPlattfrom.add(devicein);

                                if (!db.checkIsDataAlreadyInDBorNot(deviceInfo.getPlattformid())) {
                                    //deviceListWithDevicesFromPlattfrom.add(deviceInfo);
                                    db.createDevice(deviceInfo);

                                }


                                getrequestSensor();
                                getrequestActuator();

                                mAdapter.notifyDataSetChanged();


                            }
                            synchronizeDeviceEntries(db.getAllDeviceNameMacPlattform(), deviceListWithDevicesFromPlattfrom);
                            deviceListWithDevicesFromPlattfrom.clear();
                            adaptLocalDataModeltoRequest();


                        } catch (JSONException e) {
                            e.printStackTrace();
                          //  dialog.dismiss();

                        }
                        mAdapter.notifyDataSetChanged();
                        //dialog.dismiss();



                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error.Response", "errorrrrr");
                       // dialog.dismiss();

                    }
                }
        );

// add it to the RequestQueue
        queue.add(getRequest);
        //dialog.dismiss();


    }


    public void adaptLocalDataModeltoRequest() {
        //synchronizeDeviceEntries(db.getAllDeviceNameMacPlattform(), deviceListWithDevicesFromPlattfrom);
        //compareTwoSensorList(db.getAllSensorNameSensorId(), senList);
        deviceListWithDevicesFromPlattfrom.clear();
        senList.clear();
        aktuList.clear();

        cursor = db.getAllData();
        devicelist.clear();
        while (cursor.moveToNext()) {

            int id = cursor.getInt(cursor.getColumnIndex(SQLiteHelper.KEY_ID));
            String name = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_NAME));
            String macid = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_MACID));
            String plattformid = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_PLATTFORMID));
            byte[] image = cursor.getBlob(cursor.getColumnIndex(SQLiteHelper.KEY_IMAGE));
            String devicetype = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_DEVICETYPE));
            String optinalIP = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_OPTIONALIP));
            String userName = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_USERNAME));
            String password = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_PASSWORD));

            //id gelöscht
            deviceInfo = new DeviceInfo(id, plattformid, name, macid, image, devicetype, optinalIP, userName, password);
            devicelist.add(deviceInfo);
        }
        cursor.close();
       // dialog.dismiss();
        mAdapter.notifyDataSetChanged();

    }


    private void synchronizeDeviceEntries(ArrayList<DeviceInfo> listOfDevicesFromDatabase, ArrayList<DeviceInfo> listOfDevicesFromPlattform) {

        // make a copy of the list so the original list is not changed, and remove() is supported
        ArrayList<DeviceInfo> cp = new ArrayList<>();
        for (DeviceInfo d : listOfDevicesFromDatabase) {
            for (DeviceInfo o : listOfDevicesFromPlattform) {
                if (d.getPlattformid().equals(o.getPlattformid())) {
                    System.out.println("object" + o);
                    cp.add(d);
                }
            }
        }

        //System.out.println(List.removeAll(cp));
        listOfDevicesFromDatabase.removeAll(cp);
        for (DeviceInfo p : listOfDevicesFromDatabase) {
            deleteRequestSensor(p.getPlattformid());
            db.deletesensorplattformid(p.getPlattformid());
            db.deleteActuatorbyPlattform(p.getPlattformid());
            db.deleteDevice(p);
            devicelist.remove(p);
            mAdapter.notifyDataSetChanged();
        }
    }


    private void compareTwoSensorList(ArrayList<SensorInfo> listOfSensorFromDatabase, ArrayList<SensorInfo> listOfSensorFromPlattform) {

        // make a copy of the list so the original list is not changed, and remove() is supported
        ArrayList<SensorInfo> cp = new ArrayList<>();
        for (SensorInfo sensorInfoFromDatabase : listOfSensorFromDatabase) {
            for (SensorInfo sensorInfoFromPlattform : listOfSensorFromPlattform) {
                if (sensorInfoFromDatabase.getGeneratesensorid().equals(sensorInfoFromPlattform.getGeneratesensorid())) {
                    System.out.println("object" + sensorInfoFromPlattform);
                    cp.add(sensorInfoFromDatabase);
                }
            }
        }

        listOfSensorFromDatabase.removeAll(cp);
        cp.removeAll(cp);
        for (SensorInfo p : listOfSensorFromDatabase) {
            db.deletesensorId(p.getGeneratesensorid());
            sensorlist.remove(p);
            mAdapter.notifyDataSetChanged();
        }
    }


    private void compareTwoActuatorList(ArrayList<ActuatorInfo> listOfActuatorFromDatabase, ArrayList<ActuatorInfo> listOfActuatorFromPlattform) {
        // make a copy of the list so the original list is not changed, and remove() is supported
        ArrayList<ActuatorInfo> cp = new ArrayList<>();
        for (ActuatorInfo d : listOfActuatorFromDatabase) {
            for (ActuatorInfo o : listOfActuatorFromPlattform) {
                if (d.getGenerateActuatorId().equals(o.getGenerateActuatorId())) {
                    System.out.println("object" + o);
                    cp.add(d);
                }
            }
        }

        listOfActuatorFromDatabase.removeAll(cp);
        for (ActuatorInfo p : listOfActuatorFromDatabase) {
            //deleterequestsınglesensor(p.getGeneratesensorid());
            db.deleteActuatorId(p.getGenerateActuatorId());
            actuatorlist.remove(p);
            mAdapter.notifyDataSetChanged();
        }
    }


    private void getrequestSensor() {
        String urlSensor = url+"/api/sensors/";
        RequestQueue queue = Volley.newRequestQueue(this);  // this = context

        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, urlSensor, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("Response", response.toString());
                        JSONObject obj = null;
                        // devicelist.clear();
                        // db.deleteall();


                        try {
                            JSONObject mainObject = response;

                            obj = response.getJSONObject("_embedded");

                            Log.d("Response ", obj.toString());
                            JSONArray jsonArray = obj.getJSONArray("sensors");
                            Log.d("Response Sensor", jsonArray.toString());

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject explrObject = jsonArray.getJSONObject(i);
                                String name = explrObject.getString("name");
                                String plattformid = explrObject.getString("id");


                                JSONObject objectDetails2 = explrObject.getJSONObject("_embedded");
                                JSONObject objectDetails3 = objectDetails2.getJSONObject("device");

                                //JSONArray jsonArrayData = objectDetails2.getJSONArray("data");


                                    Log.d("Plattformid", "id:" + plattformid);
                                    byte[] image;

                                    //String sensorname = sensorspinner.getSelectedItem();
                                    Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                    byte[] byteArray = stream.toByteArray();
                                    image = byteArray;


                                    SensorInfo sensorInfo = new SensorInfo(plattformid, name, image);
                                    SensorInfo sensorInf = new SensorInfo(plattformid, name);

                                    if (!senList.contains(sensorInf.getGeneratesensorid())) {
                                        senList.add(sensorInf);
                                    }

                                    //for(DeviceInfo deviceInfo: devicelist){
                                    for (int j = 0; j < objectDetails3.length(); j++) {
                                        device_plattformid = objectDetails3.getString("id");
                                        String namedevice = objectDetails3.getString("name");
                                        String macid = objectDetails3.getString("macAddress");

                                        DeviceInfo deviceInfo1 = new DeviceInfo(namedevice, macid, device_plattformid, image);
                                        if (!db.checkIsDataAlreadyInDBorNotSensor(sensorInfo.getGeneratesensorid())) {
                                            if (device_plattformid.equals(deviceInfo1.getPlattformid())) {
                                                db.createSensor(sensorInfo, deviceInfo1);
                                            }
                                        }

                                    }
                                    mAdapter.notifyDataSetChanged();

                                    //compareTwoSensorList(db.getAllSensorNameSensorId(), senList);


                                }

                            compareTwoSensorList(db.getAllSensorNameSensorId(), senList);
                            senList.clear();
                            //dialog.dismiss();




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

// add it to the RequestQueue
        queue.add(getRequest);

    }


    public void deleteRequestSensor(final String deviceplattformid) {


        //String urlSensor = "http://192.168.209.189:8080/MBP/api/sensors/";

        RequestQueue queue = Volley.newRequestQueue(this);  // this = context


        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url+"/api/sensors/", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("ResponseDelete", response.toString());
                        JSONObject obj = null;

                        try {
                            JSONObject mainObject = response;

                            obj = response.getJSONObject("_embedded");

                            Log.d("Response ", obj.toString());
                            JSONArray jsonArray = obj.getJSONArray("sensors");
                            Log.d("Response Sensor", jsonArray.toString());

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject explrObject = jsonArray.getJSONObject(i);
                                String name = explrObject.getString("name");
                                String plattformid = explrObject.getString("id");


                                JSONObject objectDetails2 = explrObject.getJSONObject("_embedded");
                                JSONObject objectDetails3 = objectDetails2.getJSONObject("device");

                                //JSONArray jsonArrayData = objectDetails2.getJSONArray("data");

                                Log.d("Plattformid", "id:" + plattformid);
                                byte[] image;

                                //String sensorname = sensorspinner.getSelectedItem();
                                Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                byte[] byteArray = stream.toByteArray();
                                image = byteArray;


                                SensorInfo sensorInfo = new SensorInfo(plattformid, name, image);
                                //for(DeviceInfo deviceInfo: devicelist){
                                for (int j = 0; j < objectDetails3.length(); j++) {
                                    device_plattformid = objectDetails3.getString("id");
                                    String namedevice = objectDetails3.getString("name");
                                    String macid = objectDetails3.getString("macAddress");

                                    DeviceInfo deviceInfo1 = new DeviceInfo(namedevice, macid, device_plattformid, image);
                                    if (deviceInfo1.getPlattformid().equals(deviceplattformid)) {
                                        RequestQueue queue = Volley.newRequestQueue(context);  // this = context
                                        String urlSensor = url+"/api/sensors/" + plattformid;
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
                                        queue.add(deletesensor);
                                    }
                                }
                                mAdapter.notifyDataSetChanged();


                            }


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
        // add it to the RequestQueue
        queue.add(getRequest);
    }


    private void getrequestActuator() {
       // String urlActuator = "http://192.168.209.189:8080/MBP/api/actuators/";

        RequestQueue queue = Volley.newRequestQueue(this);  // this = context


        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url+"/api/actuators/", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("Response", response.toString());
                        JSONObject obj = null;
                        // devicelist.clear();
                        // db.deleteall();


                        try {
                            JSONObject mainObject = response;

                            obj = response.getJSONObject("_embedded");

                            Log.d("Response ", obj.toString());
                            JSONArray jsonArray = obj.getJSONArray("actuators");
                            Log.d("Response Sensor", jsonArray.toString());

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject explrObject = jsonArray.getJSONObject(i);
                                String name = explrObject.getString("name");
                                String plattformid = explrObject.getString("id");


                                JSONObject objectDetails2 = explrObject.getJSONObject("_embedded");
                                JSONObject objectDetails3 = objectDetails2.getJSONObject("device");

                                //JSONArray jsonArrayData = objectDetails2.getJSONArray("data");

                                Log.d("Plattformid", "id:" + plattformid);
                                byte[] image;

                                //String sensorname = sensorspinner.getSelectedItem();
                                Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.temperature_icon);
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                byte[] byteArray = stream.toByteArray();
                                image = byteArray;


                                //SensorInfo sensorInfo = new SensorInfo(plattformid, name, image);
                                ActuatorInfo actuatorInfo = new ActuatorInfo(plattformid, name, image);

                                //SensorInfo sensorInf = new SensorInfo(plattformid, name);
                                ActuatorInfo actuatorInf = new ActuatorInfo(plattformid, name, image);

                                if (!aktuList.contains(actuatorInf.getGenerateActuatorId())) {
                                    aktuList.add(actuatorInf);
                                }

                                //for(DeviceInfo deviceInfo: devicelist){
                                for (int j = 0; j < objectDetails3.length(); j++) {
                                    device_plattformid = objectDetails3.getString("id");
                                    String namedevice = objectDetails3.getString("name");
                                    String macid = objectDetails3.getString("macAddress");

                                    DeviceInfo deviceInfo1 = new DeviceInfo(namedevice, macid, device_plattformid, image);
                                    if (!db.checkIsDataAlreadyInDBorNotActuator(actuatorInfo.getGenerateActuatorId())) {
                                        if (device_plattformid.equals(deviceInfo1.getPlattformid())) {
                                            db.createActuator(actuatorInfo, deviceInfo1);
                                        }
                                    }

                                }
                                mAdapter.notifyDataSetChanged();

                            }

                            compareTwoActuatorList(db.getAllActuatorNameActuatorId(), aktuList);
                            aktuList.clear();
                            //dialog.dismiss();


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

// add it to the RequestQueue
        queue.add(getRequest);

    }


    public void deleterequestactuator(final String deviceplattformid) {


        //final String urlActuator = "http://192.168.209.189:8080/MBP/api/actuators/";

        RequestQueue queue = Volley.newRequestQueue(this);  // this = context


        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url+"/api/actuators/", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("ResponseDelete", response.toString());
                        JSONObject obj = null;

                        try {
                            JSONObject mainObject = response;

                            obj = response.getJSONObject("_embedded");

                            Log.d("Response ", obj.toString());
                            JSONArray jsonArray = obj.getJSONArray("actuators");
                            Log.d("Response Sensor", jsonArray.toString());

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject explrObject = jsonArray.getJSONObject(i);
                                String name = explrObject.getString("name");
                                String plattformid = explrObject.getString("id");


                                JSONObject objectDetails2 = explrObject.getJSONObject("_embedded");
                                JSONObject objectDetails3 = objectDetails2.getJSONObject("device");

                                //JSONArray jsonArrayData = objectDetails2.getJSONArray("data");

                                Log.d("Plattformid", "id:" + plattformid);
                                byte[] image;

                                //String sensorname = sensorspinner.getSelectedItem();
                                Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.temperature_icon);
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                byte[] byteArray = stream.toByteArray();
                                image = byteArray;


                                ActuatorInfo actuatorInfo = new ActuatorInfo(plattformid, name, image);
                                //for(DeviceInfo deviceInfo: devicelist){
                                for (int j = 0; j < objectDetails3.length(); j++) {
                                    device_plattformid = objectDetails3.getString("id");
                                    String namedevice = objectDetails3.getString("name");
                                    String macid = objectDetails3.getString("macAddress");

                                    DeviceInfo deviceInfo1 = new DeviceInfo(namedevice, macid, device_plattformid, image);
                                    if (deviceInfo1.getPlattformid().equals(deviceplattformid)) {
                                        RequestQueue queue = Volley.newRequestQueue(context);  // this = context
                                        String urlActuator = url+"/api/actuators/" + plattformid;
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
                                        queue.add(deleteactuator);
                                    }
                                }
                                mAdapter.notifyDataSetChanged();


                            }


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

// add it to the RequestQueue
        queue.add(getRequest);
    }


    public void deleterequestsınglesensor(String generateId) {


        RequestQueue queue = Volley.newRequestQueue(context);  // this = context
        String urlSensor = url+"/api/sensors/" + generateId;
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
        queue.add(deletesensor);


        mAdapter.notifyDataSetChanged();

    }


    public void deleterequest(final String deviceplattformid) {

        deleteRequestSensor(deviceplattformid);
        deleterequestactuator(deviceplattformid);
        RequestQueue queue = Volley.newRequestQueue(this);  // this = context
        //String url = url+"/api/devices/" + deviceplattformid;
        StringRequest dr = new StringRequest(Request.Method.DELETE, url+"/api/devices/" + deviceplattformid,
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
        queue.add(dr);

    }



    public void deployrequest(View v) {
        Intent intent = new Intent(DeviceOverviewActivity.this, DeployRequestActivity.class);
        intent.putExtra("deviceid", devicelist.get(position_stelle).getPlattformid());
        Bundle bundle = new Bundle();
        intent.putExtras(bundle);
        startActivity(intent);
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(DeviceOverviewActivity.this, MainActivity.class);
        startActivity(intent);
        //super.onBackPressed();
    }


}

