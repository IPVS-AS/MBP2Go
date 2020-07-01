package com.example.sedaulusal.hiwijob;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
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
import com.example.sedaulusal.hiwijob.monitoring.MonitoringDeviceOverviewActivity;
import com.example.sedaulusal.hiwijob.ruleEngine.RuleEngineOverviewActivity;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

import java.io.UnsupportedEncodingException;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.sedaulusal.hiwijob.SettingActivity.mypreference;


public class MainActivity extends AppCompatActivity implements ServiceConnection {


    private Button btn_Device;
    String address;
    SQLiteHelper sqLiteHelper;
    Cursor cursor;
    ArrayList<SensorInfo> sensorlist;
    ArrayList<Integer> sensorTypeIntlist;
    private SensorManager mSensorManager;
    SensorInfo sensorInfo;
    SharedPreferences sharedPreferences;
    String url;
    Context context;
    IServiceInterface serviceInterface = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_Device = (Button) findViewById(R.id.btn_Main_device);
        context = this;
        sqLiteHelper = new SQLiteHelper(this);
        sensorlist = new ArrayList<>();
        sensorTypeIntlist = new ArrayList<>();

        //sensorInfo = new SensorInfo();
        sharedPreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        url = sharedPreferences.getString("URL", "");

        try {
            System.out.print("test");
            String[] splitUp = url.split("/");
            String ipAddress = splitUp[2];
            String serverURI = "tcp://"+ ipAddress;
            //TODO : find sensor id in database and set it error sensorinfo is immer letzter wert
            //event.sensor.getId();
            //event.sensor.getName();
            for(SensorInfo sen: sensorlist) {
                //TODO überprüfung event und gleiche value
                String topic = "sensor/" + sen.getGeneratesensorid();

                //String topic = "sensor/5bd18eba4f0cb71dd52873cb";
                try {
                    //sendSensorValue();
                    //connectToMqtt(serverURI, topic, sen);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            //sensorvalue_accelerometer.setText(serviceInterface.getSensorAccelerometer());
        } catch (Exception e) {
            e.printStackTrace();
        }

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
        getsensorvalues();
        //startActivity(new Intent(MainActivity.this, HistoryDiagrammActivity.class));
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
    public void getsensorvalues(){
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
            SensorInfo sensorInfo= sqLiteHelper.getSensorwithSensorPlattfromid(generatesensorid);

            //SensorInfo sensorInfo = new SensorInfo(generatesensorid, sensorname, sensorimage, sensorPinset, sensortype);
            // Log.d("Hiwi","device info: "+id+" "+name+" "+macid+" "+sensorId);
            //Log.d("Hiwi", "device info: " + " " + name + " " + macid + " ");

            sensorlist.add(sensorInfo);
        }

        for(SensorInfo sensorInf : sensorlist){
            sensorInfo = sensorInf;
            sensorTypeIntlist.add(Integer.valueOf(sensorInf.getSensorTyp()));
           // getsensorvaluesfromsmartphone(sensorInf);
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

   /* @Override
    public void onSensorChanged(SensorEvent event) {
        Toast toast = Toast.makeText(getApplicationContext(), "sensor value", Toast.LENGTH_SHORT);
        toast.show();
        String[] splitUp = url.split("/");
        String ipAddress = splitUp[2];
        String serverURI = "tcp://"+ ipAddress;
        //TODO : find sensor id in database and set it error sensorinfo is immer letzter wert
        //event.sensor.getId();
        //event.sensor.getName();
        for(SensorInfo sen: sensorlist) {
            if(sen.getName().equals(event.sensor.getName())) {
                String topic = "sensor/" + sen.getGeneratesensorid();

                //String topic = "sensor/5bd18eba4f0cb71dd52873cb";
                try {
                    //sendSensorValue();
                   // connectToMqtt(serverURI, topic, event, sen);
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

    }*/

    public void pubMqttChannel(IMqttAsyncClient client, SensorInfo sen) {

        }

    public void connectToMqtt(String serverURI, final String topic, SensorInfo sen ) throws Exception{
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
                    pubMqttChannel(asyncActionToken.getClient(), sen);
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
    }

    /**
     * This method will be called when the service will be binded
     * @param name
     * @param service
     */
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        System.out.println("Service Connected");
        serviceInterface = IServiceInterface.Stub.asInterface(service);

    }

    /**
     * This method will be executed when the service will be disconnected
     * @param name
     */
    @Override
    public void onServiceDisconnected(ComponentName name) {
        serviceInterface = null;
    }


    /**
     * this method will be called when the app is started or
     * when we return to the app after a pause
     * Here we start the servie and bind it
     * after that we check for updated sensor values
     */
    @Override
    protected void onResume() {
        super.onResume();
        Intent i = new Intent(this, BackgroundService.class);
        i.putExtra("sensorlist", sensorTypeIntlist);
        //command to start service
        startService(i);
        //command to bind it
        bindService(i, this, BIND_AUTO_CREATE);

        String[] splitUp = url.split("/");
        String ipAddress = splitUp[2];
        // String serverURI = "tcp://"+ ipAddress+":1883";
        String serverURI = "tcp://"+"192.168.178.61:1883";
        try {
            if(sensorInfo != null) {
                mqtt(serverURI, sensorInfo);
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }

        //with this thread we check regularly for sensor updates of the accelerometer sensor

    }

    public void mqtt(String serverURI, SensorInfo sensorInfo) throws MqttException {
        //final MqttAndroidClient client = new MqttAndroidClient(context, "tcp://192.168.0.13:1883");
        //new MqttAndroidClient(this.getApplicationContext(), serverURI,clientId);

        String clientId = MqttClient.generateClientId();
        MqttConnectOptions options = new MqttConnectOptions();

        MqttAndroidClient client= new MqttAndroidClient(this.getApplicationContext(), serverURI,
                        clientId);
               // client.connect(null, new IMqttActionListener() {

                    IMqttToken token = client.connect(options);

            token.setActionCallback(new IMqttActionListener() {
                                        @Override
                                        public void onSuccess(IMqttToken asyncActionToken) {
                                            if (serviceInterface != null){
                                                String topic = "sensor/"+sensorInfo.getGeneratesensorid();
                                                //String payload = "{\"component\": \"SENSOR\", \"id\": \"5bd18eba4f0cb71dd52873cb\", \"value\": "+sensorEvent.values[0] +"}";
                                                String payload = null;
                                                try {
                                                    payload = "{\"component\": \"SENSORS\", \"id\": "+"\"" +  sensorInfo.getGeneratesensorid() +"\", \"value\": "+serviceInterface.getSensorMagnetic() +"}";

                                                    //payload = "{\"component\": \"SENSOR\", \"id\": "+"\"" +  sen.getGeneratesensorid() +"\", \"value\": "+serviceInterface.getSensorMagnetic() +"}";
                                                } catch (RemoteException e) {
                                                    e.printStackTrace();
                                                }
                                                byte[] encodedPayload = new byte[0];
                                                try {
                                                    encodedPayload = payload.getBytes("UTF-8");
                                                    MqttMessage message = new MqttMessage(encodedPayload);
                                                    message.setRetained(true);
                                                    message.setQos(1);
                                                    client.publish(topic, message);
                                                    client.disconnect();
                                                } catch (UnsupportedEncodingException | MqttException e) {
                                                    e.printStackTrace();
                                                    System.out.println(e);
                                                }

                                            }else{
                                                System.out.println("Error No Servie");
                                            }
                                            Log.i("Main","still running");
                                    /*try {
                                      //  Thread.sleep(1000);

                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }*/
                                        }
                //}

                @Override
                public void onFailure(IMqttToken arg0, Throwable arg1) {
                    // TODO Auto-generated method stub
                    Log.i("ERROR", "Client connection failed: "+arg1.getMessage());

                }
            });
        // ).start();

    };


                                        // MqttMessage message = new MqttMessage("Hello, I am Android Mqtt Client.".getBytes());
                        //message.setQos(2);
                        //message.setRetained(false);

                        //new Thread(new Runnable() {
                           // @Override
                            //public void run() {
                               // while(true){




    public void disconnectmqtt(MqttAndroidClient client) throws MqttException {
        client.disconnect();
    }


}
