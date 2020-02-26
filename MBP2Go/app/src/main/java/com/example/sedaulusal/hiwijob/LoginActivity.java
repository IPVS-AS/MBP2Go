package com.example.sedaulusal.hiwijob;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.sedaulusal.hiwijob.device.SensorInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static com.example.sedaulusal.hiwijob.SettingActivity.mypreference;


public class LoginActivity extends AppCompatActivity {
    SharedPreferences sharedpreferences;
    public static final String URL = "URL";
    private Button btn_Login_SignIn;
    EditText username, password,ip;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_login);
        //http://192.168.209.194:8888/deploy/master/api/authenticate

        //{username: "admin", password: "admin"}
        //password: "admin"
        //username: "admin"

        context = this;
        btn_Login_SignIn = (Button) findViewById(R.id.btn_activitylogin_signin);
        username = (EditText) findViewById(R.id.txt_login_username);
        password = (EditText) findViewById(R.id.txt_login_password);
        ip = (EditText) findViewById(R.id.txt_login_ip);

        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);

    }


    public void btn_Login_SignInClick(View v) throws JSONException {


        //final RequestQueue queue = Volley.newRequestQueue(context); // this = context

        RequestQueue queue2 = Volley.newRequestQueue(context); // this = context
        //String url = "http://192.168.209.189:8080/MBP/api/sensors/";
        //String typesurl = "http://192.168.209.189:8080/MBP/api/types/";
        //String deviceurl = "http://192.168.209.189:8080/MBP/api/devices/";
        //String urlSensors = url + "/api/sensors/";
        //String typesurl = url+"/api/types/";
        //String deviceurl = url+ "/api/devices/";
        //String url = "http://192.168.178.61:8080/MBP/api/authenticate";
        String usernameString = username.getText().toString();
        String passwordString = password.getText().toString();
        String ipUrl =  ip.getText().toString();
        String url = ipUrl +"/api/authenticate";
        final JSONObject jsonBody = new JSONObject("{\n" +
                "  \"password\":\""+passwordString+"\",\"username\":\""+usernameString+"\"}");


        JSONObject params_sensor = new JSONObject();
       // params_sensor.put("name", sensori.getName());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, jsonBody,
                new Response.Listener() {
                    @Override
                    public void onResponse(Object o) {
                        Log.d("Response Sensor", o.toString());

                        //Save url from Setting Activity
                        String url = ip.getText().toString();
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString(URL, url);
                        editor.putString("historyURLS", sharedpreferences.getString("historyURLS","")+"|"+url);
                        editor.commit();

                        SharedPreferences sp=getSharedPreferences("Login", 0);
                        SharedPreferences.Editor Ed=sp.edit();
                        Ed.putString("Username",usernameString );
                        Ed.putString("Password",passwordString);
                        Ed.commit();

                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();

                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Error! Wrong Ip, Username or Password", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
        ){
            //
           /* @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> authentification = getHeaderforAuthentification();
                return authentification;

            }*/
        };
        jsonObjReq.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
                Log.d("Error.retry", error.toString());


            }
        });
    queue2.add(jsonObjReq);



         //{
            //
           /* @Override
            public Map<String, String> getHeaders()  {
                    //String username ="admin";
                    //String password = "admin";
                String usernameString = username.getText().toString();
                String passwordString = password.getText().toString();

                // String auth =new String(username + ":" + password);
                   // String auth =new String("admin:admin");
                   String auth =new String(usernameString+":"+passwordString);

                byte[] data = auth.getBytes();
                    String base64 = Base64.encodeToString(data, Base64.NO_WRAP);
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Authorization","Basic "+base64);
                    //headers.put("accept-language","EN");
                    headers.put("Content-Type","application/json");
                    //headers.put("Accept","application/json");
                    return headers;

            }*/

       // };
        // Add the request to the RequestQueue.
       // queue2.add(jsonRequest);



        //JSONObject params_sensor = new JSONObject();
        //params_sensor.put("username", username.getText());
        //params_sensor.put("password", password.getText());
        //params_sensor.put("device", deviceurl + deviceInfo.getPlattformid());
        /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url,
                new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        // response
                        Log.d("Response Authorization", (String) response);
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
        ) {@Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> headers = new HashMap<>();
            String credentials = "username:password";
            String auth = "Basic "
                    + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
            headers.put("Accept", "application/json");
            headers.put("Content-Type", "application/json");
            headers.put("Authorization", auth);
            return headers;}


            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                try {
                    // Sensoren id bekommen im Head
                    String jsonString2 = new String(response.data, "UTF-8");
                    //String post = response.allHeaders.get(2).toString();
                    String location = response.headers.get("X-MBP-alert");
                    location.length();
                    Toast.makeText(context,"test"+ location,Toast.LENGTH_LONG).show();
                    //post = location.substring(location.lastIndexOf("/") + 1);
                    //SensorInfo sensorInf = new SensorInfo(post, sensorInfo.getName(), sensorInfo.getImage(), sensorInfo.getSensorPinset(), sensorInfo.getSensorTyp());
                    //sensorid = sqLiteHelper.createSensor(sensorInf, deviceInfo);
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
        queue2.add(jsonObjReq);*/



    }

    public Map<String, String> getHeaderforAuthentification(){
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
