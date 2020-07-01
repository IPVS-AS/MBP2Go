package com.example.sedaulusal.hiwijob;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.example.sedaulusal.hiwijob.device.SensorInfo;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This class starts the background service
 * It reads the sensor values of the magnetic sensor and accelerometer sensor
 * These values are set in the ServiceUnterface
 */
public class BackgroundService extends Service
{
    private static Timer timerMagnetic = new Timer();
    private static Timer timerAccelerometer = new Timer();

    float value_accelerometer, value_magnetic;
    ServiceInterfaceImplementation serviceInterfaceImplementation;
    TimerTaskMagnetic timerTaskMagnetic;
    TimerTaskAccelerometer timerTaskAccelerometer;

    ArrayList<Integer> sensorlist = new ArrayList<>();

    /**
     * This method bind the ServiceInterface
     * @param arg0
     * @return
     */
    public IBinder onBind(Intent arg0)
    {
        return serviceInterfaceImplementation;
    }

    /**
     * This method will be executed when the service starts and
     * when the service restarts after the app is closed
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        sensorlist = intent.getIntegerArrayListExtra("sensorlist");

        startBackgroundService();
        //the return value start_sticky describes that the service will restart again
        //when the app is closed
        return START_STICKY;
    }

    /**
     * This method will be executed when the service starts
     * there we call the method startBackgroundService()
     */
    public void onCreate()
    {
        super.onCreate();
        serviceInterfaceImplementation = new ServiceInterfaceImplementation();
        startBackgroundService();
    }

    /**
     * This method starts the TimerTasks for the magnetic sensor
     * and accelerometer sensor
     */
    private void startBackgroundService()
    {

        for(Integer sensorInf : sensorlist){
        timerTaskMagnetic = new TimerTaskMagnetic(sensorInf);
        //the period value describes how often the sensor value will be updated
        timerMagnetic.schedule(timerTaskMagnetic, 0, 10000);
    }
        //timerTaskAccelerometer = new TimerTaskAccelerometer();
        //the period value describes how often the sensor value will be updated
        //timerAccelerometer.schedule(timerTaskAccelerometer, 0,1000);
    }

    /**
     * This method will be executed when the service is closed
     * we unregister then the Sensor Listener
     */
    public void onDestroy()
    {
        super.onDestroy();
        timerTaskMagnetic.unregistersensor();
        timerTaskAccelerometer.unregistersensor();
        Toast.makeText(this, "Service Stopped ...", Toast.LENGTH_SHORT).show();
    }

    /**
     * In this class we  implement the methods of the IServiceInterface
     */
    private class ServiceInterfaceImplementation extends IServiceInterface.Stub{

    @Override
    public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {
    }

    /**
     * This method is the interface to get the value from sensor accelerometer
     */
    @Override
    public String getSensorAccelerometer() throws RemoteException {
        return Float.toString(value_accelerometer);
    }

    /**
     * This method is the interface to get the value from sensor magnetic
     */
    @Override
    public String getSensorMagnetic() throws RemoteException {
        return Float.toString(value_magnetic);

    }

        @Override
        public String getSensorValue() throws RemoteException {
            return null;
        }

    }

    /**
     * In this class we listen to the SensorListener
     * Here we register the  magnetic field sensor and get his values
     */
    class TimerTaskMagnetic extends TimerTask implements SensorEventListener{
        private SensorManager mSensorManager;
        private Sensor sensor;
        int sensortype;

        TimerTaskMagnetic(int sensortype){
            this.sensortype = sensortype;
        }

        /**
         * in this method we register the magnetic field sensor
         * @param sensorInfo
         */
        public void getsensorfromsmartphone(int sensorInfo) {
            mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            //if the sensor is registered then we unregister the sensor
            if(sensor != null){
                unregistersensor();
            }
            sensor = mSensorManager.getDefaultSensor(sensorInfo);
            mSensorManager.registerListener((SensorEventListener) this, sensor, mSensorManager.SENSOR_DELAY_NORMAL);
        }

        /**
         * we execute periodically the method getsensorfromsmartphone()
         */
        @Override
        public void run() {
            getsensorfromsmartphone(sensortype);
        }

        /**
         * this sensor will be called when the sensor value changed
         * @param event
         */
        @Override
        public void onSensorChanged(SensorEvent event) {

            if (event.sensor.getType() == sensortype) {
                //get the sensor value from magnetic field sensor
                value_magnetic = event.values[0];
                System.out.print(value_magnetic);
                unregistersensor();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

        /**
         * This method unregister the magnetic field sensor
         */
        public void unregistersensor() {
            mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            mSensorManager.unregisterListener((SensorEventListener) this, sensor);
        }

    }

    /**
     * In this class we listen to the SensorListener
     * Here we register the accelerometer sensor and get his values
     */
    class TimerTaskAccelerometer extends TimerTask implements SensorEventListener{
        private SensorManager mSensorManager;
        private Sensor sensor;

        /**
         * we execute periodically the method getsensorfromsmartphone()
         */
        @Override
        public void run() {
            getsensorfromsmartphone(Sensor.TYPE_ACCELEROMETER);
        }

        /**
         * this sensor will be called when the sensor value changed
         * @param event
         */
        @Override
        public void onSensorChanged(SensorEvent event) {

            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                value_accelerometer = event.values[0];
                Log.i("BackgroundService","still running");
                unregistersensor();
            }
        }


        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        /**
         * in this method we register the accelerometer sensor
         * @param sensorInfo
         */
        public void getsensorfromsmartphone(int sensorInfo) {
            mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            if(sensor != null){
                unregistersensor();
            }
            sensor = mSensorManager.getDefaultSensor(sensorInfo);
            mSensorManager.registerListener((SensorEventListener) this, sensor, mSensorManager.SENSOR_DELAY_NORMAL);
        }

        /**
         * This method unregister the accelerometer sensor
         */
        public void unregistersensor() {
            mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            mSensorManager.unregisterListener((SensorEventListener) this, sensor);

        }

    }

}