package com.example.sedaulusal.hiwijob.device;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.sedaulusal.hiwijob.R;

import java.util.ArrayList;

import static android.R.attr.duration;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_PARAM_ID = "device_id";
    TextView titletoolbar, textViewmacId, textViewdevicetype, textoptionalIP, textuserName, textPassword, textViewdetailsensosorliste, textViewdetailactuatorliste;
    DeviceInfo deviceInfo;
    ImageView image;
    int position;
    boolean isclicked=false;

    SQLiteHelper sqLiteHelper;

    ViewFlipper vf;
    RelativeLayout relativeLayout;
    int targetHeight;
    int startHeight;
    String plattform;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * 0.9) , (int) (height * 0.7));

        textViewdetailsensosorliste = (TextView) findViewById(R.id.textViewdetailsensosorliste);
        textViewdetailactuatorliste = (TextView) findViewById(R.id.detailactuatorliste);



        sqLiteHelper = new SQLiteHelper(this);
        DeviceInfo di = sqLiteHelper.getDevice(getIntent().getStringExtra("deviceid"));

       // ArrayList<SensorInfo> sensors = sqLiteHelper.getAllSensorsByPlattformId(di.getName());
       // Log.d("Sensors get", ""+sensors.get(1));
        ArrayList<SensorInfo> deviceWatchList = sqLiteHelper.getAllSensorsByPlattformId(di.getPlattformid());
        for (SensorInfo sensorInfo : deviceWatchList) {
            Log.d("Sensor Watchlist", sensorInfo.getName()+"\n"+ sensorInfo.getSensorPinset()+sensorInfo.getGeneratesensorid());
            textViewdetailsensosorliste.append(sensorInfo.getName()+"\n"+ sensorInfo.getGeneratesensorid()+"\n"+ " ");

        }

        //ArrayList<ActuatorInfo> actuatorWatchList = sqLiteHelper.getAllActuator();

        ArrayList<ActuatorInfo> actuatorWatchList = sqLiteHelper.getAllActuatorsByDevicePlattform(di.getPlattformid());

        for (ActuatorInfo actuatorInfo : actuatorWatchList) {
            Log.d("Sensor Watchlist", actuatorInfo.getName());
            textViewdetailactuatorliste.append(actuatorInfo.getName()+"\n"+ actuatorInfo.getActuatorPinset()+"\n"+ " ");

        }

        titletoolbar = (TextView) findViewById(R.id.title_toolbar);
        textViewmacId = (TextView) findViewById(R.id.textViewmacID);
        textViewdevicetype = (TextView) findViewById(R.id.textViewdevicetype);
        textoptionalIP= (TextView) findViewById(R.id.textoptionalid);
        textuserName= (TextView) findViewById(R.id.textuserName);
        textPassword= (TextView) findViewById(R.id.textPassword);


        image = (ImageView) findViewById(R.id.imageview_toolbar_icon);

        /*
        after click edit button in overview activity
        */
        final String edittextname, edittextmacId, textviewsensorname, devicetype, optionalid, userName, password;
        //final int position;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                edittextname = null;
                edittextmacId = null;
                devicetype = null;
                optionalid= null; userName= null; password= null;
            } else {
                edittextname = extras.getString("name");
                edittextmacId = extras.getString("macid");
                devicetype = extras.getString("devicetype");
                optionalid = extras.getString("optionalid");
               userName = extras.getString("userName");
               password = extras.getString("password");

                plattform = extras.getString("plattform");



                textviewsensorname = extras.getString("NAME");
                position = extras.getInt("position");

                Log.d("was ist position", "position ist in detail"+position);

//                getSupportActionBar().setTitle("  "+edittextname);
                titletoolbar.setText(edittextname);
                textViewmacId.setText(edittextmacId);
                textViewdevicetype.setText(devicetype);
                textoptionalIP.setText(optionalid);
                textuserName.setText(userName);
                textPassword.setText(password);



                /**
                 * definition of first spinner with raspberry/arduino etc.
                 * view with animation
                 */
                vf = (ViewFlipper) findViewById(R.id.viewflipper_detailactivity);
                relativeLayout = (RelativeLayout) findViewById(R.id.viewspinnerdetailactiviy);
                relativeLayout.setMinimumHeight(startHeight);
                if(devicetype == null){

                }
               else {
                    Log.d("rasp", "rasp");
                                Toast.makeText(getApplicationContext(), "Raspberry wurde gewählt", Toast.LENGTH_SHORT).show();
                                vf.setDisplayedChild(1);
                                //TO DO: Look to a other solution to resize view
                                startHeight = 140;
                                targetHeight = 140;
                                DeviceRegistryActivity.ResizeAnimation resizeAnimation = new DeviceRegistryActivity.ResizeAnimation(
                                        relativeLayout,
                                        targetHeight,
                                        startHeight
                                );
                                resizeAnimation.setDuration(duration);
                                relativeLayout.startAnimation(resizeAnimation);

                }
               /* else {
                    startHeight = 30;
                    targetHeight = 30;

                    vf.setDisplayedChild(2);
                    DeviceRegistryActivity.ResizeAnimation resizeAnimation = new DeviceRegistryActivity.ResizeAnimation(
                            relativeLayout,
                            targetHeight,
                            startHeight
                    );
                    resizeAnimation.setDuration(duration);
                    relativeLayout.startAnimation(resizeAnimation);
                }*/


                byte[] iconImage =  extras.getByteArray("imagebitmap");
                Bitmap bitmap = BitmapFactory.decodeByteArray(iconImage, 0, iconImage.length);
                bitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, false);
                //image.setImageBitmap(bitmap);
                Resources res = getResources();
                BitmapDrawable icon = new BitmapDrawable(res,bitmap);
                //getSupportActionBar().setIcon(icon);
                image.setImageBitmap(bitmap);
                //myToolbar.setLogo(icon);


                /*EditText name, macId;
                TextView senname;
                name = (EditText) findViewById(R.id.edtName);
                name.setText(edittextname);
                macId = (EditText) findViewById(R.id.edtMacid);
                macId.setText(edittextmacId);*/

            }
        } else {
            edittextname = (String) savedInstanceState.getSerializable("name");
            edittextmacId = (String) savedInstanceState.getSerializable("macid");
        }
    }

    public void imgbtn_edit(View v){
        Intent intent = new Intent(DetailActivity.this, DeviceRegistryActivity.class);
        intent.putExtra("name", titletoolbar.getText());
        intent.putExtra("maciddetail", textViewmacId.getText());
        intent.putExtra("plattform", plattform);

        Bundle bundle=new Bundle();
        //bundle.putInt("IMAGE", sensorInfo.getImage());
        //bundle.putString("NAME", sensorInfo.getName());
        intent.putExtras(bundle);

        //intent.putExtra("sensors", String.valueOf(registryActivity.sensoradapter.getItem(registryActivity.sensorlist.iterator()));
        startActivity(intent);

    }

    public void imgbtn_delete(View v){
        isclicked = true;
        DeviceOverviewActivity deleteDevice = new DeviceOverviewActivity();
        SQLiteHelper db = deleteDevice.db;


        Log.d(" Position",
                " Count position "+ position);
        if(isclicked){

            Intent intent = new Intent(DetailActivity.this, DeviceOverviewActivity.class);
            intent.putExtra("isclicked", true);
            intent.putExtra("position1", position);
            startActivity(intent);
        }


    }
}
