package com.example.sedaulusal.hiwijob.device;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.sedaulusal.hiwijob.ruleEngine.RuleEngineActuatorInfo;
import com.example.sedaulusal.hiwijob.ruleEngine.RuleEngineRuleInfo;
import com.example.sedaulusal.hiwijob.ruleEngine.RuleEngineSensorInfo;

import java.util.ArrayList;


public class SQLiteHelper extends SQLiteOpenHelper {


    SQLiteDatabase sqLiteDatabase;
    // All Static variables

    // Logcat tag
    private static final String LOG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 7;

    // Database URL
    private static final String DATABASE_NAME = "DeviceInfoDB";

    // Table Names
    private static final String TABLE_RULEINFO = "RULEINFO"; //umbennen
    private static final String TABLE_RULESENSORINFO = "RULESENSORINFO"; //umbennen
    private static final String TABLE_RULEACTUATORINFO = "RULEACTUATORINFO"; //umbennen
    private static final String TABLE_DEVICE = "DEVICEINFO"; //umbennen
    private static final String TABLE_SENSOR = "SENSORTABLE";
    private static final String TABLE_ACTUATOR = "ACTUATORTABLE";


    // Common column names
    public static final String KEY_ID = "id";


    // Device Table Columns names
    public static final String KEY_NAME = "name";
    public static final String KEY_MACID = "macid";
    public static final String KEY_PLATTFORMID = "plattformid";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_DEVICETYPE = "devicetype";
    public static final String KEY_OPTIONALIP = "optionalIP";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";


    //Sensor Table Columns names
    public static final String KEY_SensorID = "sensorid";
    public static final String KEY_SensorNAME = "sensorname";
    public static final String KEY_SensorIMAGE = "sensorimage";
    public static final String KEY_SensorTYP = "sensortyp";
    public static final String KEY_SensorPINSET = "sensorpinset";

    //Actuator Table Columns names
    public static final String KEY_ActuatorID = "actuatorid";
    public static final String KEY_ActuatorNAME = "actuatorname";
    public static final String KEY_ActuatorIMAGE = "actuatorimage";
    public static final String KEY_ActuatorTYP = "actuatortyp";
    public static final String KEY_ActuatorPINSET = "actuatorpinset";


    // Rule Info
    public static final String KEY_RULE_ID = "ruleid";
    public static final String KEY_RULE_NAME = "rulename";
    public static final String KEY_RULE_STATUS = "rulestatus";
    //public static final String KEY_RULE_JSON = "plattformid";
    public static final String KEY_RULE_IMAGE = "ruleimage";
    public static final String KEY_RULE_ASSOCIATION = "ruleassociation";


    //RuleSensorInfo
    public static final String KEY_RULE_SensorID = "rulesensorid";
    public static final String KEY_RULE_Sensorvalue = "sensorvalue";
    public static final String KEY_RULE_Sensoroperation = "sensoroperation";

    //RuleSensorInfo
    public static final String KEY_RULE_ActuatorID = "ruleactuatorid";
    public static final String KEY_RULE_Actuatorvalue = "actuatorvalue";
    //public static final String KEY_RULE_Sensoroperation = "sensoroperation";


    // Device Create Statements
    //INTEGER PRIMARY KEY AUTOINCREMENT nötig?
    // Todo table create statement
    private static final String CREATE_TABLE_DEVICE = "CREATE TABLE " + TABLE_DEVICE + " ("
            + KEY_ID + " INTEGER PRIMARY KEY, "
            + KEY_NAME + " TEXT,"
            + KEY_MACID + " TEXT,"
            + KEY_PLATTFORMID + " TEXT,"
            + KEY_IMAGE + " BLOB,"
            + KEY_DEVICETYPE + " TEXT,"
            + KEY_OPTIONALIP + " TEXT,"
            + KEY_USERNAME + " TEXT,"
            + KEY_PASSWORD + " TEXT"
            + ")";

    // Sensor table create statement
    //Sensor Id nötig??
    private static final String CREATE_TABLE_SENSOR = "CREATE TABLE " + TABLE_SENSOR + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_SensorID + " TEXT,"
            + KEY_SensorNAME + " TEXT,"
            + KEY_PLATTFORMID + " TEXT,"
            + KEY_SensorIMAGE + " BLOB,"
            + KEY_SensorTYP + " TEXT,"
            + KEY_SensorPINSET + " TEXT" + ")";


    // Sensor table create statement
    //Sensor Id nötig??
    private static final String CREATE_TABLE_ACTUATOR = "CREATE TABLE " + TABLE_ACTUATOR + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_ActuatorID + " TEXT,"
            + KEY_ActuatorNAME + " TEXT,"
            + KEY_PLATTFORMID + " TEXT,"
            + KEY_ActuatorIMAGE + " BLOB,"
            + KEY_ActuatorTYP + " TEXT,"
            + KEY_ActuatorPINSET + " TEXT" + ")";


    //INTEGER PRIMARY KEY AUTOINCREMENT nötig?
    // Todo table create statement
    private static final String CREATE_TABLE_RULE = "CREATE TABLE " + TABLE_RULEINFO + "("
            + KEY_RULE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_RULE_NAME + " TEXT,"
            + KEY_RULE_STATUS + " TEXT,"
            + KEY_RULE_IMAGE + " BLOB,"
            + KEY_RULE_ASSOCIATION + " TEXT"
            + ")";


    //INTEGER PRIMARY KEY AUTOINCREMENT nötig?
    //SEnsorID von SensorInfo und RuleID von RuleInfo
    // Todo table create statement
    private static final String CREATE_TABLE_RULESENSOR = "CREATE TABLE " + TABLE_RULESENSORINFO + "("
            + KEY_RULE_SensorID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_RULE_ID + " TEXT,"
            + KEY_SensorID + " TEXT,"
            + KEY_RULE_Sensoroperation + " TEXT,"
            + KEY_RULE_Sensorvalue + " TEXT"
            + ")";


    //INTEGER PRIMARY KEY AUTOINCREMENT nötig?
    //SEnsorID von SensorInfo und RuleID von RuleInfo
    // Todo table create statement
    private static final String CREATE_TABLE_RULEACTUATOR = "CREATE TABLE " + TABLE_RULEACTUATORINFO + "("
            + KEY_RULE_ActuatorID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_RULE_ID + " TEXT,"
            + KEY_ActuatorID + " TEXT,"
            + KEY_RULE_Actuatorvalue + " TEXT"
            + ")";


    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }


    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try {
            // creating required tables
            sqLiteDatabase.execSQL(CREATE_TABLE_DEVICE);
            sqLiteDatabase.execSQL(CREATE_TABLE_SENSOR);
            sqLiteDatabase.execSQL(CREATE_TABLE_ACTUATOR);
            sqLiteDatabase.execSQL(CREATE_TABLE_RULE);
            sqLiteDatabase.execSQL(CREATE_TABLE_RULESENSOR);
            sqLiteDatabase.execSQL(CREATE_TABLE_RULEACTUATOR);



        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_DEVICE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_SENSOR);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTUATOR);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_RULEINFO);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_RULESENSORINFO);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_RULEACTUATORINFO);


        // create new tables
        onCreate(sqLiteDatabase);
    }


    public Cursor getAllData() {
        sqLiteDatabase = getWritableDatabase();
        return sqLiteDatabase.rawQuery("select * from " + TABLE_DEVICE, null);

    }


    // Deleting single device
    public boolean delete(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            int result = db.delete(TABLE_DEVICE, KEY_ID + " =?", new String[]{String.valueOf(id)});
            db.close();
            if (result > 0) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Deleting single device
    public void deleteall() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_DEVICE);
    }


    /**
     * weitere params von sensor nötig
     *
     * @param name
     * @param macid
     * @param image
     * @return
     */
    public boolean updateData(String name, String macid, byte[] image) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_NAME, name);
        contentValues.put(KEY_MACID, macid);
        contentValues.put(KEY_IMAGE, image);
        sqLiteDatabase.update(TABLE_DEVICE, contentValues, "name = ?", new String[]{name});

        return true;
    }


    public void queryData(String sql) {
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(sql);
    }


    /*
    * Creating a device
    */
    public long createDevice(DeviceInfo info) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, info.getName());
        values.put(KEY_MACID, info.getMacid());
        values.put(KEY_PLATTFORMID, info.getPlattformid());

        values.put(KEY_IMAGE, info.getImage());

        values.put(KEY_DEVICETYPE, info.getDevicetype());
        values.put(KEY_OPTIONALIP, info.getOptionalIP());
        values.put(KEY_USERNAME, info.getUsername());
        values.put(KEY_PASSWORD, info.getPassword());

        // insert row
        long device_id = db.insert(TABLE_DEVICE, null, values);



        return device_id;
    }


    /*
    * get single device
    */
    public DeviceInfo getDevice(String deviceplattformid) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_DEVICE + " WHERE "
                + KEY_PLATTFORMID + " = '" + deviceplattformid +"'";

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setId(c.getInt(c.getColumnIndex(KEY_ID)));
        deviceInfo.setName((c.getString(c.getColumnIndex(KEY_NAME))));
        deviceInfo.setMacid(c.getString(c.getColumnIndex(KEY_MACID)));
        deviceInfo.setPlattformid(c.getString(c.getColumnIndex(KEY_PLATTFORMID)));
        deviceInfo.setImage(c.getBlob(c.getColumnIndex(KEY_IMAGE)));
        deviceInfo.setDevicetype(c.getString(c.getColumnIndex(KEY_DEVICETYPE)));
        deviceInfo.setOptionalIP(c.getString(c.getColumnIndex(KEY_OPTIONALIP)));
        deviceInfo.setUsername(c.getString(c.getColumnIndex(KEY_USERNAME)));
        deviceInfo.setPassword(c.getString(c.getColumnIndex(KEY_PASSWORD)));



        return deviceInfo;
    }

    /*
    * get single device
    */

    public DeviceInfo getDevicemacid(String mac_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_DEVICE + " WHERE "
                + KEY_MACID + " = '" + mac_id +"'";

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setId(c.getInt(c.getColumnIndex(KEY_ID)));
        deviceInfo.setName((c.getString(c.getColumnIndex(KEY_NAME))));
        deviceInfo.setMacid(c.getString(c.getColumnIndex(KEY_MACID)));
        deviceInfo.setPlattformid((c.getString(c.getColumnIndex(KEY_PLATTFORMID))));
        deviceInfo.setImage(c.getBlob(c.getColumnIndex(KEY_IMAGE)));
        deviceInfo.setDevicetype(c.getString(c.getColumnIndex(KEY_DEVICETYPE)));
        deviceInfo.setOptionalIP(c.getString(c.getColumnIndex(KEY_OPTIONALIP)));
        deviceInfo.setUsername(c.getString(c.getColumnIndex(KEY_USERNAME)));
        deviceInfo.setPassword(c.getString(c.getColumnIndex(KEY_PASSWORD)));

        return deviceInfo;
    }

    /*
    * get single device
    */
    public DeviceInfo getDeviceplattformid(String plattform_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_DEVICE + " WHERE "
                + KEY_PLATTFORMID + " = '" + plattform_id +"'";

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setId(c.getInt(c.getColumnIndex(KEY_ID)));
        deviceInfo.setName((c.getString(c.getColumnIndex(KEY_NAME))));
        deviceInfo.setMacid(c.getString(c.getColumnIndex(KEY_MACID)));
        deviceInfo.setPlattformid(c.getString(c.getColumnIndex(KEY_PLATTFORMID)));
        deviceInfo.setImage(c.getBlob(c.getColumnIndex(KEY_IMAGE)));
        deviceInfo.setDevicetype(c.getString(c.getColumnIndex(KEY_DEVICETYPE)));
        deviceInfo.setOptionalIP(c.getString(c.getColumnIndex(KEY_OPTIONALIP)));
        deviceInfo.setUsername(c.getString(c.getColumnIndex(KEY_USERNAME)));
        deviceInfo.setPassword(c.getString(c.getColumnIndex(KEY_PASSWORD)));

        return deviceInfo;
    }



    public boolean checkIsDataAlreadyInDBorNot(String fieldValue) {
        SQLiteDatabase sqldb = this.getWritableDatabase();
        String Query = "SELECT * FROM " + TABLE_DEVICE + " d WHERE d." + KEY_PLATTFORMID + " = '" + fieldValue + "'";
        Cursor cursor = sqldb.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }




    /*
    * getting all device
    * */
    public ArrayList<DeviceInfo> getAllDevice() {
        ArrayList<DeviceInfo> deviceInfoslist = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_DEVICE;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to devicelist
        if (c.moveToFirst()) {
            do {
                DeviceInfo deviceInfo = new DeviceInfo();
                deviceInfo.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                deviceInfo.setName((c.getString(c.getColumnIndex(KEY_NAME))));
                deviceInfo.setMacid(c.getString(c.getColumnIndex(KEY_MACID)));
                deviceInfo.setPlattformid(c.getString(c.getColumnIndex(KEY_PLATTFORMID)));

                deviceInfo.setImage(c.getBlob(c.getColumnIndex(KEY_IMAGE)));

                deviceInfo.setDevicetype(c.getString(c.getColumnIndex(KEY_DEVICETYPE)));
                deviceInfo.setOptionalIP(c.getString(c.getColumnIndex(KEY_OPTIONALIP)));
                deviceInfo.setUsername(c.getString(c.getColumnIndex(KEY_USERNAME)));
                deviceInfo.setPassword(c.getString(c.getColumnIndex(KEY_PASSWORD)));

                // adding to todo devicelist
                deviceInfoslist.add(deviceInfo);
            } while (c.moveToNext());
        }
        return deviceInfoslist;
    }


    public ArrayList<DeviceInfo> getAllDeviceNameMacPlattform() {
        ArrayList<DeviceInfo> deviceInfoslist = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_DEVICE;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to devicelist
        if (c.moveToFirst()) {
            do {
                DeviceInfo deviceInfo = new DeviceInfo();
                deviceInfo.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                deviceInfo.setName((c.getString(c.getColumnIndex(KEY_NAME))));
                deviceInfo.setMacid(c.getString(c.getColumnIndex(KEY_MACID)));
                deviceInfo.setPlattformid(c.getString(c.getColumnIndex(KEY_PLATTFORMID)));



                // adding to todo devicelist
                deviceInfoslist.add(deviceInfo);
            } while (c.moveToNext());
        }
        return deviceInfoslist;
    }


    /*
  * Updating a device
  */
    public int updateDevice(DeviceInfo deviceInfo) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, deviceInfo.getName());
        values.put(KEY_MACID, deviceInfo.getMacid());
        values.put(KEY_IMAGE, deviceInfo.getImage());
        values.put(KEY_DEVICETYPE, deviceInfo.getDevicetype());
        values.put(KEY_OPTIONALIP, deviceInfo.getOptionalIP());
        values.put(KEY_USERNAME, deviceInfo.getUsername());
        values.put(KEY_PASSWORD, deviceInfo.getPassword());


        // updating row
        return db.update(TABLE_DEVICE, values, KEY_PLATTFORMID + " = ?",
                new String[]{String.valueOf(deviceInfo.getPlattformid())});
    }




    /**
     * Deleting a device
     */
    public void deleteDevice(DeviceInfo deivceInfo) {
        SQLiteDatabase db = this.getWritableDatabase();

        // before deleting sensor
        // check if device under this sensor should also be deleted
        //if (should_delete_all_tag_todos) {

       // }

        // now delete the tag
        //oder Key Id
        db.delete(TABLE_DEVICE, KEY_PLATTFORMID + " = ?",
                new String[] { String.valueOf(deivceInfo.getPlattformid()) });
    }


    // ------------------------ "sensor" table methods ----------------//


    public long createSensor(SensorInfo sensor, DeviceInfo deviceInfo) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SensorID, sensor.getGeneratesensorid());
        values.put(KEY_SensorNAME, sensor.getName());
        values.put(KEY_SensorIMAGE, sensor.getImage());
        values.put(KEY_PLATTFORMID, deviceInfo.getPlattformid());
        values.put(KEY_SensorTYP, sensor.getSensorTyp());
        values.put(KEY_SensorPINSET, sensor.getSensorPinset());

        // insert row
        long sensor_id = db.insert(TABLE_SENSOR, null, values);

        // assigning device to sensor
        //for (long device_id : device_ids) {
        //  createDeviceSensor(sensor_id, device_id);
        //}


        return sensor_id;
    }

    /*
 * get single sensor
 */
    public SensorInfo getSensor(int sensor_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_SENSOR + " WHERE "
                + KEY_ID + " = " + "'" + sensor_id +"'";

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        SensorInfo sensorInfo = new SensorInfo();
        DeviceInfo deviceInfo = new DeviceInfo();
        sensorInfo.setId(c.getInt(c.getColumnIndex(KEY_ID)));
        sensorInfo.setGeneratesensorid(c.getString(c.getColumnIndex(KEY_SensorID)));
        sensorInfo.setName((c.getString(c.getColumnIndex(KEY_SensorNAME))));
        sensorInfo.setImage(c.getBlob(c.getColumnIndex(KEY_SensorIMAGE)));
        deviceInfo.setPlattformid((c.getString(c.getColumnIndex(KEY_PLATTFORMID))));
        sensorInfo.setSensorTyp(c.getString(c.getColumnIndex(KEY_SensorTYP)));
        sensorInfo.setSensorPinset(c.getString(c.getColumnIndex(KEY_SensorPINSET)));

        return sensorInfo;
    }

    public SensorInfo getSensorwithSensorPlattfromid(String plattformid) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_SENSOR + " WHERE "
                + KEY_SensorID + " = " + "'" + plattformid +"'";

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        SensorInfo sensorInfo = new SensorInfo();
        DeviceInfo deviceInfo = new DeviceInfo();
        sensorInfo.setId(c.getInt(c.getColumnIndex(KEY_ID)));
        sensorInfo.setGeneratesensorid(c.getString(c.getColumnIndex(KEY_SensorID)));
        sensorInfo.setName((c.getString(c.getColumnIndex(KEY_SensorNAME))));
        sensorInfo.setImage(c.getBlob(c.getColumnIndex(KEY_SensorIMAGE)));
        deviceInfo.setPlattformid((c.getString(c.getColumnIndex(KEY_PLATTFORMID))));


        sensorInfo.setSensorTyp(c.getString(c.getColumnIndex(KEY_SensorTYP)));
        sensorInfo.setSensorPinset(c.getString(c.getColumnIndex(KEY_SensorPINSET)));

        return sensorInfo;
    }

    public SensorInfo getSensorwithDevicePlattfromid(String plattformid) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_SENSOR + " WHERE "
                + KEY_PLATTFORMID + " = " + "'" + plattformid +"'";

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        SensorInfo sensorInfo = new SensorInfo();
        DeviceInfo deviceInfo = new DeviceInfo();
        sensorInfo.setId(c.getInt(c.getColumnIndex(KEY_ID)));
        sensorInfo.setGeneratesensorid(c.getString(c.getColumnIndex(KEY_SensorID)));
        sensorInfo.setName((c.getString(c.getColumnIndex(KEY_SensorNAME))));
        sensorInfo.setImage(c.getBlob(c.getColumnIndex(KEY_SensorIMAGE)));
        deviceInfo.setPlattformid((c.getString(c.getColumnIndex(KEY_PLATTFORMID))));


        sensorInfo.setSensorTyp(c.getString(c.getColumnIndex(KEY_SensorTYP)));
        sensorInfo.setSensorPinset(c.getString(c.getColumnIndex(KEY_SensorPINSET)));

        return sensorInfo;
    }

    /**
     * getting all tags
     * */
    public ArrayList<SensorInfo> getAllSensor() {
        ArrayList<SensorInfo> sensorInfosList = new ArrayList<SensorInfo>();
        String selectQuery = "SELECT  * FROM " + TABLE_SENSOR;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to devicelist
        if (c.moveToFirst()) {
            do {
                //fehlt Sensor id??
                SensorInfo sensorInfo = new SensorInfo();

                DeviceInfo deviceInfo = new DeviceInfo();

                sensorInfo.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                //klappt das ?? generiertes id
                sensorInfo.setGeneratesensorid(c.getString((c.getColumnIndex(KEY_SensorID))));
                sensorInfo.setName(c.getString(c.getColumnIndex(KEY_SensorNAME)));
                sensorInfo.setImage(c.getBlob(c.getColumnIndex(KEY_SensorIMAGE)));

                deviceInfo.setPlattformid(c.getString(c.getColumnIndex(KEY_PLATTFORMID)));


                sensorInfo.setSensorTyp(c.getString(c.getColumnIndex(KEY_SensorTYP)));
                sensorInfo.setSensorPinset(c.getString(c.getColumnIndex(KEY_SensorPINSET)));


                // adding to tags devicelist
                sensorInfosList.add(sensorInfo);
            } while (c.moveToNext());
        }
        return sensorInfosList;
    }

    public Cursor getAllSensorData() {
        sqLiteDatabase = getWritableDatabase();
        return sqLiteDatabase.rawQuery("select * from " + TABLE_SENSOR, null);

    }


    public ArrayList<SensorInfo> getAllSensorNameSensorId() {
        ArrayList<SensorInfo> sensorInfoslist = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_SENSOR;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to devicelist
        if (c.moveToFirst()) {
            do {
                SensorInfo sensorInfo = new SensorInfo();
                sensorInfo.setGeneratesensorid(c.getString(c.getColumnIndex(KEY_SensorID)));
                sensorInfo.setName((c.getString(c.getColumnIndex(KEY_SensorNAME))));


                // adding to todo devicelist
              sensorInfoslist.add(sensorInfo);
            } while (c.moveToNext());
        }
        return sensorInfoslist;
    }


    /**
     * Updating a sensor
     */
    public int updateSensor(SensorInfo sensorInfo, DeviceInfo deviceInfo) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        //fehlt Sensorid???
        values.put(KEY_SensorID, sensorInfo.getGeneratesensorid());
        values.put(KEY_SensorNAME, sensorInfo.getName());
        values.put(KEY_SensorIMAGE, sensorInfo.getImage());

        values.put(KEY_PLATTFORMID, deviceInfo.getPlattformid());

        values.put(KEY_SensorTYP, sensorInfo.getSensorTyp());
        values.put(KEY_SensorPINSET, sensorInfo.getSensorPinset());


        // updating row
        return db.update(TABLE_SENSOR, values, KEY_PLATTFORMID + " = ?",
                new String[] { String.valueOf(deviceInfo.getPlattformid()) });
    }

    /**
     * getting sensor count
     */
    public int getSensorCount() {
        String countQuery = "SELECT  * FROM " + TABLE_SENSOR;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }



    public ArrayList<SensorInfo> getAllSensorsByPlattformId(String plattform_id) {
        ArrayList<SensorInfo> sensorInfosList = new ArrayList<SensorInfo>();

        String selectQuery = "SELECT * FROM " + TABLE_SENSOR + " s WHERE s." + KEY_PLATTFORMID + " = '" + plattform_id + "'";
        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to devicelist

        if (c.moveToFirst()) {
            do {
                //fehlt Sensor id??
                SensorInfo sensorInfo = new SensorInfo();
                DeviceInfo deviceInfo = new DeviceInfo();
                sensorInfo.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                sensorInfo.setGeneratesensorid(c.getString((c.getColumnIndex(KEY_SensorID))));
                sensorInfo.setName(c.getString(c.getColumnIndex(KEY_SensorNAME)));
                sensorInfo.setImage(c.getBlob(c.getColumnIndex(KEY_SensorIMAGE)));

                deviceInfo.setPlattformid(c.getString(c.getColumnIndex(KEY_PLATTFORMID)));

                sensorInfo.setSensorTyp(c.getString(c.getColumnIndex(KEY_SensorTYP)));
                sensorInfo.setSensorPinset(c.getString(c.getColumnIndex(KEY_SensorPINSET)));

                Log.d("debugging selectquery", sensorInfo.getName() );

                // adding to sensor devicelist
                sensorInfosList.add(sensorInfo);
            } while (c.moveToNext());
        }
        return sensorInfosList;
    }





    public Cursor getAllCursorSensorsByDevicesPlattformid(String plattform_id) {

        sqLiteDatabase = getWritableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_SENSOR + " s WHERE s." + KEY_PLATTFORMID + " = '" + plattform_id + "'";

        Log.e(LOG, selectQuery);

        return sqLiteDatabase.rawQuery(selectQuery, null);
    }



    public boolean checkIsDataAlreadyInDBorNotSensor(String fieldValue) {
        SQLiteDatabase sqldb = this.getWritableDatabase();
        String Query = "SELECT * FROM " + TABLE_SENSOR + " d WHERE d." + KEY_SensorID + " = '" + fieldValue + "'";
        Cursor cursor = sqldb.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    /*
      * Deleting a sensor
      */
    public void deletesensor(int sensor_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SENSOR, KEY_ID + " = ?",
                new String[]{String.valueOf(sensor_id)});
    }

    /*
      * Deleting a sensor
      */
    public void deletesensorId(String sensor_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SENSOR, KEY_SensorID + " = ?",
                new String[]{String.valueOf(sensor_id)});
    }

    /*
     * Deleting a sensor
     */
    public void deletesensorplattformid(String plattform_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SENSOR, KEY_PLATTFORMID + " = ?",
                new String[]{String.valueOf(plattform_id)});
    }






    // ------------------------ "actuator" table methods ----------------//


    public long createActuator(ActuatorInfo actuator, DeviceInfo deviceInfo) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ActuatorID, actuator.getGenerateActuatorId());
        values.put(KEY_ActuatorNAME, actuator.getName());
        values.put(KEY_ActuatorIMAGE, actuator.getImage());
        values.put(KEY_PLATTFORMID, deviceInfo.getPlattformid());
        values.put(KEY_ActuatorTYP, actuator.getActuatorTyp());
        values.put(KEY_ActuatorPINSET, actuator.getActuatorPinset());



        // insert row
        long actuator_id = db.insert(TABLE_ACTUATOR, null, values);

        return actuator_id;
    }

    /*
 * get single actuator
 */
    public ActuatorInfo getActuator(long actuator_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_ACTUATOR + " WHERE "
                + KEY_ID + " = " + actuator_id;

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        ActuatorInfo actuatorInfo = new ActuatorInfo();
        DeviceInfo deviceInfo = new DeviceInfo();
        actuatorInfo.setId(c.getInt(c.getColumnIndex(KEY_ID)));
        actuatorInfo.setGenerateActuatorId(c.getString(c.getColumnIndex(KEY_ActuatorID)));
        actuatorInfo.setName((c.getString(c.getColumnIndex(KEY_SensorNAME))));
        actuatorInfo.setImage(c.getBlob(c.getColumnIndex(KEY_SensorIMAGE)));
        deviceInfo.setPlattformid((c.getString(c.getColumnIndex(KEY_PLATTFORMID))));
        actuatorInfo.setActuatorTyp((c.getString(c.getColumnIndex(KEY_ActuatorTYP))));
        actuatorInfo.setActuatorPinset((c.getString(c.getColumnIndex(KEY_ActuatorPINSET))));


        return actuatorInfo;
    }

    /**
     * getting all tags
     * */
    public ArrayList<ActuatorInfo> getAllActuator() {
        ArrayList<ActuatorInfo> actuatorInfosList = new ArrayList<ActuatorInfo>();
        String selectQuery = "SELECT  * FROM " + TABLE_ACTUATOR;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to devicelist
        if (c.moveToFirst()) {
            do {
                //fehlt Sensor id??
                ActuatorInfo actuatorInfo = new ActuatorInfo();

                DeviceInfo deviceInfo = new DeviceInfo();
                actuatorInfo.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                actuatorInfo.setGenerateActuatorId(c.getString((c.getColumnIndex(KEY_ActuatorID))));
                actuatorInfo.setName(c.getString(c.getColumnIndex(KEY_ActuatorNAME)));
                actuatorInfo.setImage(c.getBlob(c.getColumnIndex(KEY_ActuatorIMAGE)));

                deviceInfo.setPlattformid((c.getString(c.getColumnIndex(KEY_PLATTFORMID))));

                actuatorInfo.setActuatorTyp((c.getString(c.getColumnIndex(KEY_ActuatorTYP))));
                actuatorInfo.setActuatorPinset((c.getString(c.getColumnIndex(KEY_ActuatorPINSET))));



                // adding to tags devicelist
               actuatorInfosList.add(actuatorInfo);
            } while (c.moveToNext());
        }
        return actuatorInfosList;
    }

    public Cursor getAllActuatorData() {
        sqLiteDatabase = getWritableDatabase();
        return sqLiteDatabase.rawQuery("select * from " + TABLE_ACTUATOR, null);

    }

    /**
     * Updating a sensor
     */
    public int updateActuator(ActuatorInfo actuatorInfo, DeviceInfo deviceInfo) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        //fehlt Sensorid???
        values.put(KEY_ActuatorID, actuatorInfo.getGenerateActuatorId());
        values.put(KEY_ActuatorNAME, actuatorInfo.getName());
        values.put(KEY_ActuatorIMAGE, actuatorInfo.getImage());

        values.put(KEY_ActuatorTYP, actuatorInfo.getActuatorTyp());
        values.put(KEY_ActuatorPINSET, actuatorInfo.getActuatorPinset());

        values.put(KEY_PLATTFORMID, deviceInfo.getPlattformid());




        // updating row
        return db.update(TABLE_ACTUATOR, values, KEY_MACID + " = ?",
                new String[] { String.valueOf(deviceInfo.getPlattformid()) });
    }


    public boolean checkIsDataAlreadyInDBorNotActuator(String fieldValue) {
        SQLiteDatabase sqldb = this.getWritableDatabase();
        String Query = "SELECT * FROM " + TABLE_ACTUATOR + " d WHERE d." + KEY_ActuatorID + " = '" + fieldValue + "'";
        Cursor cursor = sqldb.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    /**
     * getting actuator count
     */
    public int getActuatorCount() {
        String countQuery = "SELECT * FROM " + TABLE_ACTUATOR;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    public ArrayList<ActuatorInfo> getAllActuatorNameActuatorId() {
        ArrayList<ActuatorInfo> actuatorInfoslist = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_ACTUATOR;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to devicelist
        if (c.moveToFirst()) {
            do {
                ActuatorInfo actuatorInfo = new ActuatorInfo();
                actuatorInfo.setGenerateActuatorId(c.getString(c.getColumnIndex(KEY_ActuatorID)));
                actuatorInfo.setName((c.getString(c.getColumnIndex(KEY_ActuatorNAME))));


                // adding to todo devicelist
                actuatorInfoslist.add(actuatorInfo);
            } while (c.moveToNext());
        }
        return actuatorInfoslist;
    }


    public ArrayList<ActuatorInfo> getAllActuatorsByDevicePlattform(String plattformid) {
        ArrayList<ActuatorInfo> actuatorInfosList = new ArrayList<ActuatorInfo>();


        //String selectQuery = "SELECT * FROM " + TABLE_SENSOR + " s, "
        //    + TABLE_DEVICE_SENSOR + " ds WHERE ds."
        //  + KEY_DEVICE_ID + " = '" + device_id + "'"+ " AND s." + KEY_ID + " = " + "ds." + KEY_SENSOR_ID;

        String selectQuery = "SELECT * FROM " + TABLE_ACTUATOR + " s WHERE s." + KEY_PLATTFORMID + " = '" + plattformid + "'";



        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to devicelist

        if (c.moveToFirst()) {
            do {
                //fehlt Sensor id??
                ActuatorInfo actuatorInfo = new ActuatorInfo();
                DeviceInfo deviceInfo = new DeviceInfo();
                actuatorInfo.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                actuatorInfo.setGenerateActuatorId(c.getString((c.getColumnIndex(KEY_ActuatorID))));
                actuatorInfo.setName(c.getString(c.getColumnIndex(KEY_ActuatorNAME)));
                actuatorInfo.setImage(c.getBlob(c.getColumnIndex(KEY_ActuatorIMAGE)));
                deviceInfo.setPlattformid(c.getString(c.getColumnIndex(KEY_PLATTFORMID)));
                actuatorInfo.setActuatorTyp((c.getString(c.getColumnIndex(KEY_ActuatorTYP))));
                actuatorInfo.setActuatorPinset((c.getString(c.getColumnIndex(KEY_ActuatorPINSET))));



                Log.d("debugging selectquery", actuatorInfo.getName() );

                // adding to sensor devicelist
                actuatorInfosList.add(actuatorInfo);
            } while (c.moveToNext());
        }

        return actuatorInfosList;
    }
    public Cursor getAllCursorActuatorByDevicesPlattform(String plattformid) {

        sqLiteDatabase = getWritableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_ACTUATOR + " s WHERE s." + KEY_PLATTFORMID + " = '" + plattformid + "'";

        Log.e(LOG, selectQuery);

        return sqLiteDatabase.rawQuery(selectQuery, null);
    }

    /*
      * Deleting a sensor
      */
    public void deleteActuator(long actuator_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ACTUATOR, KEY_ID + " = ?",
                new String[]{String.valueOf(actuator_id)});
    }

    public void deleteActuatorpin(String actuator_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ACTUATOR, KEY_ActuatorPINSET + " = ?",
                new String[]{String.valueOf(actuator_id)});
    }

    public void deleteActuatorId(String actuator_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ACTUATOR, KEY_ActuatorID + " = ?",
                new String[]{String.valueOf(actuator_id)});
    }

    /*
     * Deleting a sensor
     */
    public void deleteActuatorbyPlattform(String plattform) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ACTUATOR, KEY_PLATTFORMID + " = ?",
                new String[]{String.valueOf(plattform)});
    }


    /////////////////////////////////////////////////RULEINFO/////////////////////

    /*
     * Creating a device
     */
    public long createRuleInfo(RuleEngineRuleInfo info) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        //values.put(KEY_RULE_ID, info.getRuleengineruleid());
        values.put(KEY_RULE_NAME, info.getRulename());
        values.put(KEY_RULE_IMAGE, info.getRuleImage());

        values.put(KEY_RULE_STATUS, info.getRulestatus());

        values.put(KEY_RULE_ASSOCIATION, info.getRuleassociation());
        //values.put(KEY_OPTIONALIP, info.getOptionalIP());
        //values.put(KEY_USERNAME, info.getUsername());
        //values.put(KEY_PASSWORD, info.getPassword());

        // insert row
        long rule_id = db.insert(TABLE_RULEINFO, null, values);



        return rule_id;
    }

    /*
     * get single device
     */
    public RuleEngineRuleInfo getRuleInfo(long ruleid) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_RULEINFO + " WHERE "
                + KEY_RULE_ID + " = '" + ruleid +"'";

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();


        RuleEngineRuleInfo ruleInfo = new RuleEngineRuleInfo();
        ruleInfo.setRuleengineruleid(c.getInt(c.getColumnIndex(KEY_RULE_ID)));
        ruleInfo.setRulename((c.getString(c.getColumnIndex(KEY_RULE_NAME))));
        ruleInfo.setRuleImage(c.getBlob(c.getColumnIndex(KEY_RULE_IMAGE)));
        ruleInfo.setRulestatus(c.getString(c.getColumnIndex(KEY_RULE_STATUS)));
        ruleInfo.setRuleassociation(c.getString(c.getColumnIndex(KEY_PLATTFORMID)));



        return ruleInfo;
    }

    public Cursor getAllDataRuleInfo() {
        sqLiteDatabase = getWritableDatabase();
        return sqLiteDatabase.rawQuery("select * from " + TABLE_RULEINFO, null);

    }


    /*
     * Updating a device
     */
    public int updateRuleInfo(RuleEngineRuleInfo ruleInfo) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_RULE_ID, ruleInfo.getRuleengineruleid());
        values.put(KEY_RULE_NAME, ruleInfo.getRulename());
        values.put(KEY_RULE_IMAGE, ruleInfo.getRuleImage());
        values.put(KEY_RULE_STATUS, ruleInfo.getRulestatus());
        values.put(KEY_RULE_ASSOCIATION, ruleInfo.getRuleassociation());

        // updating row
        return db.update(TABLE_RULEINFO, values, KEY_RULE_ID + " = ?",
                new String[]{String.valueOf(ruleInfo.getRuleengineruleid())});
    }


    /**
     * Deleting a device
     */
    public void deleteRuleInfo(RuleEngineRuleInfo ruleEngineRuleInfo) {
        SQLiteDatabase db = this.getWritableDatabase();

        // before deleting sensor
        // check if device under this sensor should also be deleted
        //if (should_delete_all_tag_todos) {

        // }

        // now delete the tag
        //oder Key Id
        db.delete(TABLE_RULEINFO, KEY_RULE_ID + " = ?",
                new String[] { String.valueOf(ruleEngineRuleInfo.getRuleengineruleid()) });
    }


    ////////////////////RuleSensorInfo//////////////////////

    public long createRuleSensor(RuleEngineSensorInfo sensor, RuleEngineRuleInfo ruleInfo, String sensorInfoGenerateId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_RULE_ID, ruleInfo.getRuleengineruleid());
        values.put(KEY_SensorID, sensorInfoGenerateId);
        values.put(KEY_RULE_Sensoroperation, sensor.getRuleopeator());
        values.put(KEY_RULE_Sensorvalue, sensor.getRulevalue());

        // insert row
        long sensor_id = db.insert(TABLE_RULESENSORINFO, null, values);

        // assigning device to sensor
        //for (long device_id : device_ids) {
        //  createDeviceSensor(sensor_id, device_id);
        //}


        return sensor_id;
    }

    /*
     * Deleting a sensor
     */
    public void deleteRuleSensorInfo(String plattform_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RULESENSORINFO, KEY_RULE_ID + " = ?",
                new String[]{String.valueOf(plattform_id)});
    }


    /*
     * get single sensor
     */
    public RuleEngineSensorInfo getRuleSensorInfo(int sensor_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_RULESENSORINFO + " WHERE "
                + KEY_RULE_SensorID + " = " + "'" + sensor_id +"'";

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        SensorInfo sensorInfo = new SensorInfo();
        RuleEngineSensorInfo ruleEngineSensorInfo = new RuleEngineSensorInfo();
        RuleEngineRuleInfo ruleInfo = new RuleEngineRuleInfo();
        sensorInfo.setGeneratesensorid(c.getString(c.getColumnIndex(KEY_SensorID)));
        ruleInfo.setRuleengineruleid((c.getInt(c.getColumnIndex(KEY_RULE_ID))));
        ruleEngineSensorInfo.setRulesensorid((c.getInt(c.getColumnIndex(KEY_RULE_SensorID))));
        ruleEngineSensorInfo.setRuleopeator(c.getString(c.getColumnIndex(KEY_RULE_Sensoroperation)));
        ruleEngineSensorInfo.setRulevalue((c.getString(c.getColumnIndex(KEY_RULE_Sensorvalue))));
        //sensorInfo.setImage(c.getBlob(c.getColumnIndex(KEY_SensorIMAGE)));
        //sensorInfo.setSensorTyp(c.getString(c.getColumnIndex(KEY_SensorTYP)));
        //sensorInfo.setSensorPinset(c.getString(c.getColumnIndex(KEY_SensorPINSET)));

        return ruleEngineSensorInfo;
    }



    public RuleEngineSensorInfo getRuleSensorInfoRuleID(long ruleid) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_RULESENSORINFO + " WHERE "
                + KEY_RULE_ID + " = " + "'" + ruleid +"'";

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        SensorInfo sensorInfo = new SensorInfo();
        RuleEngineSensorInfo ruleEngineSensorInfo = new RuleEngineSensorInfo();
        RuleEngineRuleInfo ruleInfo = new RuleEngineRuleInfo();
        sensorInfo.setGeneratesensorid(c.getString(c.getColumnIndex(KEY_SensorID)));
        ruleInfo.setRuleengineruleid((c.getInt(c.getColumnIndex(KEY_RULE_ID))));
        ruleEngineSensorInfo.setRulesensorid((c.getInt(c.getColumnIndex(KEY_RULE_SensorID))));
        ruleEngineSensorInfo.setRuleopeator(c.getString(c.getColumnIndex(KEY_RULE_Sensoroperation)));
        ruleEngineSensorInfo.setRulevalue((c.getString(c.getColumnIndex(KEY_RULE_Sensorvalue))));
        //sensorInfo.setImage(c.getBlob(c.getColumnIndex(KEY_SensorIMAGE)));
        //sensorInfo.setSensorTyp(c.getString(c.getColumnIndex(KEY_SensorTYP)));
        //sensorInfo.setSensorPinset(c.getString(c.getColumnIndex(KEY_SensorPINSET)));

        return ruleEngineSensorInfo;
    }

    public Cursor getAllDataRuleSensorInfo() {
        sqLiteDatabase = getWritableDatabase();
        return sqLiteDatabase.rawQuery("select * from " + TABLE_RULESENSORINFO, null);

    }

    public Cursor getAllCursorRuleInfoSensor(long ruleid) {

        sqLiteDatabase = getWritableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_RULESENSORINFO + " s WHERE s." + KEY_RULE_ID + " = '" + ruleid + "'";

        Log.e(LOG, selectQuery);

        return sqLiteDatabase.rawQuery(selectQuery, null);
    }


    ////////////////////RuleActuatorInfo//////////////////////

    public long createRuleActuator(RuleEngineActuatorInfo actuatorInfo, RuleEngineRuleInfo ruleInfo, String actuatorInfoGenerateId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_RULE_ID, ruleInfo.getRuleengineruleid());
        values.put(KEY_ActuatorID, actuatorInfoGenerateId);
        //values.put(KEY_RULE_Sensoroperation, actuatorInfo.getRuleopeator());
        values.put(KEY_RULE_Actuatorvalue, actuatorInfo.getRulevalue());

        // insert row
        long actuator_id = db.insert(TABLE_RULEACTUATORINFO, null, values);

        // assigning device to sensor
        //for (long device_id : device_ids) {
        //  createDeviceSensor(sensor_id, device_id);
        //}


        return actuator_id;
    }


    /*
     * get single sensor
     */
    public RuleEngineActuatorInfo getRuleActuatorInfo(int actuator_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_RULEACTUATORINFO + " WHERE "
                + KEY_RULE_ActuatorID + " = " + "'" + actuator_id +"'";

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        ActuatorInfo actuatorInfo = new ActuatorInfo();
        RuleEngineActuatorInfo ruleEngineActuatorInfo = new RuleEngineActuatorInfo();
        RuleEngineRuleInfo ruleInfo = new RuleEngineRuleInfo();
        actuatorInfo.setGenerateActuatorId(c.getString(c.getColumnIndex(KEY_ActuatorID)));
        ruleInfo.setRuleengineruleid((c.getInt(c.getColumnIndex(KEY_RULE_ID))));
        ruleEngineActuatorInfo.setRuleactuatorid((c.getInt(c.getColumnIndex(KEY_RULE_ActuatorID))));
        //actuatorInfo.setRuleopeator(c.getString(c.getColumnIndex(KEY_RULE_Sensoroperation)));
        ruleEngineActuatorInfo.setRulevalue((c.getString(c.getColumnIndex(KEY_RULE_Actuatorvalue))));
        //sensorInfo.setImage(c.getBlob(c.getColumnIndex(KEY_SensorIMAGE)));
        //sensorInfo.setSensorTyp(c.getString(c.getColumnIndex(KEY_SensorTYP)));
        //sensorInfo.setSensorPinset(c.getString(c.getColumnIndex(KEY_SensorPINSET)));

        return ruleEngineActuatorInfo;
    }



    public RuleEngineActuatorInfo getRuleActuatorInfoRuleID(long ruleid) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_RULEACTUATORINFO + " WHERE "
                + KEY_RULE_ID + " = " + "'" + ruleid +"'";

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        ActuatorInfo actuatorInfo = new ActuatorInfo();
        RuleEngineActuatorInfo ruleEngineActuatorInfo = new RuleEngineActuatorInfo();
        RuleEngineRuleInfo ruleInfo = new RuleEngineRuleInfo();
        actuatorInfo.setGenerateActuatorId(c.getString(c.getColumnIndex(KEY_ActuatorID)));
        ruleInfo.setRuleengineruleid((c.getInt(c.getColumnIndex(KEY_RULE_ID))));
        ruleEngineActuatorInfo.setRuleactuatorid((c.getInt(c.getColumnIndex(KEY_RULE_ActuatorID))));
        //actuatorInfo.setRuleopeator(c.getString(c.getColumnIndex(KEY_RULE_Sensoroperation)));
        ruleEngineActuatorInfo.setRulevalue((c.getString(c.getColumnIndex(KEY_RULE_Actuatorvalue))));
        //sensorInfo.setImage(c.getBlob(c.getColumnIndex(KEY_SensorIMAGE)));
        //sensorInfo.setSensorTyp(c.getString(c.getColumnIndex(KEY_SensorTYP)));
        //sensorInfo.setSensorPinset(c.getString(c.getColumnIndex(KEY_SensorPINSET)));

        return ruleEngineActuatorInfo;
    }

    /*
     * Deleting a sensor
     */
    public void deleteRuleActuator(String plattform_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RULEACTUATORINFO, KEY_RULE_ID + " = ?",
                new String[]{String.valueOf(plattform_id)});
    }

    public Cursor getAllDataRuleActuatorInfo() {
        sqLiteDatabase = getWritableDatabase();
        return sqLiteDatabase.rawQuery("select * from " + TABLE_RULEACTUATORINFO, null);

    }

    public Cursor getAllCursorRuleInfoActuator(long ruleid) {

        sqLiteDatabase = getWritableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_RULEACTUATORINFO + " s WHERE s." + KEY_RULE_ID + " = '" + ruleid + "'";

        Log.e(LOG, selectQuery);

        return sqLiteDatabase.rawQuery(selectQuery, null);
    }

}
