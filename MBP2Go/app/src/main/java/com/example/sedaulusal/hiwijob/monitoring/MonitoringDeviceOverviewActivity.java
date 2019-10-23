package com.example.sedaulusal.hiwijob.monitoring;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.sedaulusal.hiwijob.MainActivity;
import com.example.sedaulusal.hiwijob.R;
import com.example.sedaulusal.hiwijob.device.DeployRequestActivity;
import com.example.sedaulusal.hiwijob.device.DetailActivity;
import com.example.sedaulusal.hiwijob.device.DeviceInfo;
import com.example.sedaulusal.hiwijob.device.DeviceOverviewActivity;
import com.example.sedaulusal.hiwijob.device.DeviceRegistryActivity;
import com.example.sedaulusal.hiwijob.device.MyAdapter;
import com.example.sedaulusal.hiwijob.device.RecyclerTouchListener;
import com.example.sedaulusal.hiwijob.device.SQLiteHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.sedaulusal.hiwijob.SettingActivity.mypreference;

public class MonitoringDeviceOverviewActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    ArrayList<DeviceInfo> devicelist;

    RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    SQLiteHelper db;
    Context context;
    DeviceInfo deviceInfo;
    Cursor cursor;
    int position_stelle;

    SharedPreferences sharedPreferences;
    String url;
    Button deployRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring_device_overview);
        context = this;


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        sharedPreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        url = sharedPreferences.getString("URL", "");


        db = new SQLiteHelper(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);


        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        devicelist = new ArrayList<>();


        // specify an adapter (see also next example)
        mAdapter = new MonitoringDeviceAdapter(devicelist, cursor, context);
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
            String state = "Loading...";

            //id gelöscht
            deviceInfo = new DeviceInfo(id, plattformid, name, macid, image, devicetype, optinalIP, userName, password, state);
            // Log.d("Hiwi","device info: "+id+" "+name+" "+macid+" "+sensorId);
            Log.d("Hiwi", "device info: " + " " + name + " " + macid + " ");
            devicelist.add(deviceInfo);
        }
        cursor.close();


        mAdapter.notifyDataSetChanged();


        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), mRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                //requestDeviceState();
                //mAdapter.notifyDataSetChanged();

                position_stelle = position;
                Log.d("Position in onClick", "Position ist " + position);

                // Getting todos under "Watchlist" tag name
                Log.d("ToDo", "Get todos under single Tag name");


                //Fehlerbehebung, sonst Array -1 Error
                if (position <= -1) {
                    //dialog.dismiss();
                    return;
                }

                DeviceInfo deviceInfo = devicelist.get(position);
                //Toast.makeText(getApplicationContext(), deviceInfo.getName() + " is selected! " + deviceInfo.getId() + " Plattformid" + deviceInfo.getPlattformid() + " sensorcount " + db.getSensorCount() + " actuatorcount " + db.getActuatorCount(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MonitoringDeviceOverviewActivity.this, MonitoringDeployDeviceActivity.class);
                intent.putExtra("deviceinfoid", deviceInfo.getPlattformid());
                Bundle bundle = new Bundle();
                intent.putExtras(bundle);
                startActivity(intent);
            }


            @Override
            public void onLongClick(View view, final int position) {

            }
        }));

        deployRequest = (Button) findViewById(R.id.switch_deploy);


    }

    public void deployrequest(View v) {
        Intent intent = new Intent(MonitoringDeviceOverviewActivity.this, MonitoringDeployDeviceActivity.class);
        intent.putExtra("deviceid", devicelist.get(position_stelle).getPlattformid());
        Bundle bundle = new Bundle();
        intent.putExtras(bundle);
        startActivity(intent);
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MonitoringDeviceOverviewActivity.this, MainActivity.class);
        startActivity(intent);
        //super.onBackPressed();
    }

    public Map<String, String> getHeaderforAuthentification() {
        String username = "admin";
        String password = "admin";
        // String auth =new String(username + ":" + password);
        String auth = new String("admin:admin");
        byte[] data = auth.getBytes();
        String base64 = Base64.encodeToString(data, Base64.NO_WRAP);
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Basic " + base64);
        //headers.put("accept-language","EN");
        headers.put("Content-Type", "application/json");
        //headers.put("Accept","application/json");
        return headers;
    }


}
