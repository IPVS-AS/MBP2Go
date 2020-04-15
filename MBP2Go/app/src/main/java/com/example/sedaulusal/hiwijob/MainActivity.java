package com.example.sedaulusal.hiwijob;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.sedaulusal.hiwijob.device.DeviceInfo;
import com.example.sedaulusal.hiwijob.device.DeviceOverviewActivity;
import com.example.sedaulusal.hiwijob.device.SQLiteHelper;
import com.example.sedaulusal.hiwijob.device.SensorInfo;
import com.example.sedaulusal.hiwijob.diagramm.DiagrammActivity;
import com.example.sedaulusal.hiwijob.historydiagramm.HistoryDiagrammActivity;
import com.example.sedaulusal.hiwijob.monitoring.MonitoringDeviceOverviewActivity;
import com.example.sedaulusal.hiwijob.ruleEngine.RuleEngineActivity;
import com.example.sedaulusal.hiwijob.ruleEngine.RuleEngineOverviewActivity;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



public class MainActivity extends AppCompatActivity  {


    private Button btn_Device;
    String address;
    SQLiteHelper sqLiteHelper;
    Cursor cursor;
    ArrayList<SensorInfo> sensorlist;
    private SensorManager mSensorManager;
    SensorInfo sensorInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_Device = (Button) findViewById(R.id.btn_Main_device);

        sqLiteHelper = new SQLiteHelper(this);
        sensorlist = new ArrayList<>();
        //sensorInfo = new SensorInfo();
    }

    public void btn_Main_DeviceClick(View v){
        startActivity(new Intent(MainActivity.this, DeviceOverviewActivity.class));
        //finish();
    }

    public void btn_Main_Diagramm(View v){
        startActivity(new Intent(MainActivity.this, DiagrammActivity.class));
        //finish();
    }

    public void btn_Main_Setting(View v){
        startActivity(new Intent(MainActivity.this, SettingActivity.class));
        //finish();
    }

    public void btn_RuleEngine(View v){
        startActivity(new Intent(MainActivity.this, RuleEngineOverviewActivity.class));
        //finish();
    }

    public void btn_History(View v){
        startActivity(new Intent(MainActivity.this, HistoryDiagrammActivity.class));
        //finish();
    }

    public void btn_Monitoring(View v){
        startActivity(new Intent(MainActivity.this, MonitoringDeviceOverviewActivity.class));

    }


    @Override
    public void onBackPressed()
    {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }





    /*
    This part of code was for testing sensorvalues from smartphone
    it is a part of a outlook
     */
    //TODO: it will need the interface for the smartphone sensorvalues
    //implements SensorEventListener
   /* public void btn_TestPublish(View v){
        Toast toast = Toast.makeText(getApplicationContext(), "Sensor data transmitted", Toast.LENGTH_SHORT);
        //toast.show();
        address = getMacAddr();
        String a = address;
        DeviceInfo deviceInfo = sqLiteHelper.getDevicemacid(address);
        deviceInfo.getName();

        //TODO: Bemerkt das PlattformID nicht gespeichert wird! korrigieren!
        cursor = sqLiteHelper.getAllCursorSensorsByDevicesPlattformid(deviceInfo.getPlattformid());
        sensorlist.clear();
        while (cursor.moveToNext()) {

            //int id = cursor.getInt(cursor.getColumnIndex(SQLiteHelper.KEY_ID));
            String sensorname = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_SensorNAME));
            String  generatesensorid= cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_SensorID));
            String plattformid = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_PLATTFORMID));
            byte[] sensorimage = cursor.getBlob(cursor.getColumnIndex(SQLiteHelper.KEY_SensorIMAGE));
            String sensorPinset = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_SensorPINSET));
            String sensortype = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_SensorTYP));
            //String userName = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_USERNAME));
            //String password = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_PASSWORD));

            //id gelöscht
            SensorInfo sensorInfo = new SensorInfo(generatesensorid, sensorname, sensorimage, sensorPinset, sensortype);
            // Log.d("Hiwi","device info: "+id+" "+name+" "+macid+" "+sensorId);
            //Log.d("Hiwi", "device info: " + " " + name + " " + macid + " ");

            sensorlist.add(sensorInfo);
        }

        for(SensorInfo sensorInf : sensorlist){
            //sensorInfo = sensorInf;
            getsensorvaluesfromsmartphone(sensorInf);
        }
        cursor.close();


        //startActivity(new Intent(MainActivity.this, HistoryDiagrammActivity.class));
        //finish();
    }

    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(Integer.toHexString(b & 0xFF) + ":");
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
            //handle exception
        }
        return "";
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        String serverURI = "tcp://192.168.209.189:1883";
        //TODO : find sensor id in database and set it error sensorinfo is immer letzter wert
        //event.sensor.getId();
        //event.sensor.getName();
        for(SensorInfo sen: sensorlist) {
            if(sen.getName().equals(event.sensor.getName())) {
                String topic = "sensor/" + sen.getGeneratesensorid();

                //String topic = "sensor/5bd18eba4f0cb71dd52873cb";
                try {
                    connectToMqtt(serverURI, topic, event, sen);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    public void getsensorvaluesfromsmartphone(SensorInfo sensorInfo){
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        Sensor sensor = mSensorManager.getDefaultSensor(Integer.parseInt(sensorInfo.getSensorTyp()));
        mSensorManager.registerListener((SensorEventListener) MainActivity.this, sensor,mSensorManager.SENSOR_DELAY_NORMAL);


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                mSensorManager.unregisterListener((SensorEventListener) MainActivity.this, sensor);
            }
        }, 5000);

    }

    public void pubMqttChannel(MqttAndroidClient client, SensorEvent sensorEvent, SensorInfo sen) {

        String topic = "sensor/"+sen.getGeneratesensorid();
        //String payload = "{\"component\": \"SENSOR\", \"id\": \"5bd18eba4f0cb71dd52873cb\", \"value\": "+sensorEvent.values[0] +"}";
        String payload = "{\"component\": \"SENSOR\", \"id\": "+"\"" +  sen.getGeneratesensorid() +"\", \"value\": "+sensorEvent.values[0] +"}";
        byte[] encodedPayload = new byte[0];
        try {
            encodedPayload = payload.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            message.setRetained(true);
            client.publish(topic, message);
        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }}

    public void connectToMqtt(String serverURI, final String topic, SensorEvent sensorEvent, SensorInfo sen ) throws Exception{
        String clientId = MqttClient.generateClientId();
        final MqttAndroidClient client=
                new MqttAndroidClient(this.getApplicationContext(), serverURI,
                        clientId);

        MqttConnectOptions options = new MqttConnectOptions();
        options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
        try {
            IMqttToken token = client.connect(options);
        } catch (MqttException e) {
            e.printStackTrace();
            //Toast.makeText(getApplicationContext(), "Error connect to Mqtt", Toast.LENGTH_SHORT).show();
           // Toast toast = Toast.makeText(getApplicationContext(), "Error connect to Mqtt", Toast.LENGTH_SHORT);
            //toast.show();
        }


        try {
            IMqttToken token = client.connect();
            if(token!=null){}
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d("MQtt", "onSuccess");
                    // Toast.makeText(getApplicationContext(), "Please wait,mqtt", Toast.LENGTH_SHORT).show();
                    //Toast toast = Toast.makeText(getApplicationContext(), "Error connect to Mqtt", Toast.LENGTH_SHORT);
                   // toast.show();
                    //pubMqttChannel(client);
                    pubMqttChannel(client, sensorEvent, sen);
                    //subscribe(client);

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d("TEEST", "onFailure");
                    // Toast.makeText(getApplicationContext(), "Please wait,mqtt fail"+ exception, Toast.LENGTH_SHORT).show();
                    //Toast toast = Toast.makeText(getApplicationContext(), "Error connect to Mqtt"+exception.toString(), Toast.LENGTH_SHORT);
                    //toast.show();
                    //Intent intent = new Intent(DiagrammActivity.this, DiagrammActivity.class);
                    //startActivity(intent);
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
            Log.e("TEEST", "onFailure");

            // Toast.makeText(getApplicationContext(), "Error connect to Mqtt", Toast.LENGTH_SHORT).show();
            //Toast toast = Toast.makeText(getApplicationContext(), "Error connect to Mqtt", Toast.LENGTH_SHORT);
            //toast.show();
            //Intent intent = new Intent(DiagrammActivity.this, DiagrammActivity.class);
            //startActivity(intent);

        }
    }*/
}
