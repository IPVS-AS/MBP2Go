package com.example.sedaulusal.hiwijob.monitoring;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sedaulusal.hiwijob.R;
import com.example.sedaulusal.hiwijob.device.SQLiteHelper;
import com.example.sedaulusal.hiwijob.device.SensorInfo;
import com.example.sedaulusal.hiwijob.diagramm.DiagrammActivity;
import com.example.sedaulusal.hiwijob.diagramm.DynamicFragment;
import com.example.sedaulusal.hiwijob.historydiagramm.HistoryDiagrammActivity;
import com.example.sedaulusal.hiwijob.historydiagramm.HistoryDynamicFragment;
import com.highsoft.highcharts.Common.HIChartsClasses.HIChart;
import com.highsoft.highcharts.Common.HIChartsClasses.HIExporting;
import com.highsoft.highcharts.Common.HIChartsClasses.HILegend;
import com.highsoft.highcharts.Common.HIChartsClasses.HIOptions;
import com.highsoft.highcharts.Common.HIChartsClasses.HIPlotLines;
import com.highsoft.highcharts.Common.HIChartsClasses.HISpline;
import com.highsoft.highcharts.Common.HIChartsClasses.HITitle;
import com.highsoft.highcharts.Common.HIChartsClasses.HITooltip;
import com.highsoft.highcharts.Common.HIChartsClasses.HIXAxis;
import com.highsoft.highcharts.Common.HIChartsClasses.HIYAxis;
import com.highsoft.highcharts.Common.HIColor;
import com.highsoft.highcharts.Core.HIChartView;
import com.highsoft.highcharts.Core.HIFunction;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.example.sedaulusal.hiwijob.SettingActivity.mypreference;

public class MonitoringDiagramm2Activity extends AppCompatActivity {

    WebView wv;
    MonitoringDiagramm2Activity monitoringdiagramm;
    static String TAG = "TAG";
    SQLiteHelper sqLiteHelper;
    HistoryDynamicFragment context;

    TextView min, max, average;
    ArrayList<Double> sensorValues, sensorValueList;
    ArrayList<String> sensorTimeValueList;
    ArrayList<Double> yAchse;
    HIChart chart;
    SharedPreferences sharedPreferences;
    String url;
    String value = "0";
    String monitoringname, monitoringid, deviceid="";





    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring_diagramm2);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                monitoringname = "";
                monitoringid = "";
                deviceid = "";
            } else {
                monitoringname = extras.getString("monitoringname");
                monitoringid = extras.getString("monitoringid");
                deviceid = extras.getString("deviceinfoid");

            }
        }

        monitoringdiagramm = (MonitoringDiagramm2Activity) this;

        sharedPreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        url = sharedPreferences.getString("URL", "");

        try {
            String serverURI = "tcp://"+parseurl();
            //connectToMqtt( serverURI , "monitoring/5ce34000f8ea1208475c8b36@5c741376f8ea1203bc69aea4");
            connectToMqtt( serverURI , "monitoring/"+monitoringid+"@"+deviceid);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }




                wv = (WebView) findViewById(R.id.mon_webView_live);
        // final EditText et = (EditText) view.findViewById(R.id.editText);
        wv.getSettings().setSafeBrowsingEnabled(false);
        WebSettings settings = wv.getSettings();
        settings.setJavaScriptEnabled(true);
        wv.loadUrl("file:///android_asset/highchart.html");
        setWv(wv);
        // final Context self = this;

        new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    i++;
                    monitoringdiagramm.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            wv.evaluateJavascript("curValue = " + Double.parseDouble(value)+ ";", null);
                            //wv.evaluateJavascript("series: [{\n" +
                            //      "    name:'"+ String.valueOf( Math.random())+"'," , null);
                            //dynamicfragmentvalue.setText(String.format("%.2f", String.valueOf( Math.random())));
                            /*String test = test1;
                            if(!test.equals(textView.getText().toString())){
                            test = textView.toString();
                            wv.reload();
                            }*/
                            //
                        }
                    });

                }
            }
        }).start();

        initViews();


    }


    public void setWv(WebView wv) {
        this.wv = wv;
    }

    public WebView getWv() {
        return wv;
    }


    /////////////////


    public void setyAchse(ArrayList<Double> yAchse) {
        this.yAchse = yAchse;
    }

    public ArrayList<Double> getyAchse() {
        return yAchse;
    }

    private void initViews() {

        //TextView textView=view.findViewById(R.id.commonTextView);
        // diagrammActivity = (DiagrammActivity) getActivity();

        //String name = diagrammActivity.lastWord; //id device
        //ArrayList<SensorInfo> sensorInfos = historyDiagrammActivity.sensorlist;
        //String sen = sensorInfos.get(getArguments().getInt("position")).getGeneratesensorid();

        //getValuesfromSensor(sen);
        //textView.setText(String.valueOf("Category :  "+getArguments().getInt("position")));
        // textView.setText(String.valueOf("Category :  "+ sensorInfos.get(getArguments().getInt("position")).getName()));

        // ArrayList<Double> sensorvalue = historyDiagrammActivity.sensorValues;
        HIChartView chartView = (HIChartView) findViewById(R.id.mon_webView_history);

        HIOptions options = new HIOptions();

        chart = new HIChart();

        chart.setType("spline");
        chart.setAnimation("Highcharts.svg");// don't animate in old IE
        chart.setMarginRight(10);


        HISpline spline = new HISpline();


        // events.load = new HIFunction("function () { var series = this.series[0]; setInterval(function () { var x = (new Date()).getTime(), y = Math.random(); series.addPoint([x, y], true, true); }, 1000); }", true);
        options.setChart(chart);

        HITitle title = new HITitle();

        title.setText("");
        options.setTitle(title);

        HIXAxis xaxis = new HIXAxis();
        options.setXAxis(new ArrayList<HIXAxis>() {{
            add(xaxis);
        }});

        HIYAxis yaxis = new HIYAxis();
        // yaxis.title =  new HITitle();
        HITitle hiTitle = new HITitle();
        hiTitle.setText("value");

        yaxis.setTitle(hiTitle);
        //yaxis.title.text = "Value";
        //yaxis.setTitle(hiTitle.setText(""));;//!!!!
        HIPlotLines plotLines = new HIPlotLines();
        //plotLines.value = 0;
        plotLines.setValue(0);
        plotLines.setWidth(1);
        plotLines.setColor(HIColor.initWithHexValue("808080"));
        yaxis.setPlotLines(new ArrayList<>(Collections.singletonList(plotLines)));
        options.setYAxis(new ArrayList<>(Collections.singletonList(yaxis)));

        HITooltip tooltip = new HITooltip();
        tooltip.setFormatter(new HIFunction("function () { return '<b>' + this.series.name + '</b><br/>' + this.x + '<br/>' + Highcharts.numberFormat(this.y, 2); }"));
        options.setTooltip(tooltip);

        HILegend legend = new HILegend();
        //legend.enabled = false;
        legend.setEnabled(false);
        //options.legend = legend;
        options.setLegend(legend);

        HIExporting exporting = new HIExporting();
        //exporting.enabled = false;
        exporting.setEnabled(false);
        //options.exporting = exporting;
        options.setExporting(exporting);

        spline.setName("Data");


        HashMap<String, Object> splineData1 = getStringObjectHashMap(1506522303996L, 0.9008867958057089);
        HashMap<String, Object> splineData2 = new HashMap<>();
        splineData2.put("x", 1506522304996L);
        splineData2.put("y", 1);
        HashMap<String, Object> splineData3 = new HashMap<>();
        splineData3.put("x", 1506522305996L);
        splineData3.put("y", 2);
        HashMap<String, Object> splineData4 = getStringObjectHashMap(1506522306996L, 1);
        HashMap<String, Object> splineData5 = new HashMap<>();
        splineData5.put("x", 1506522307996L);
        splineData5.put("y", 4);
        HashMap<String, Object> splineData6 = new HashMap<>();

        splineData6.put("x", 1506522307996L);
        splineData6.put("y", 3);

        ArrayList<String> xAchse;
        xAchse = new ArrayList<>();
        xAchse.add("1");
        xAchse.add("5");


        chart.setZoomType("x");

        // getSensorParam(sen);
        getValuesfromSensor(spline, options, chartView, xaxis);

        chartView.setOptions(options);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                chartView.reload();

            }
        }, 800);


    }


    public double test() {
        return Math.random();
    }


    private HashMap<String, Object> getStringObjectHashMap(long l, double numberget) {
        HashMap<String, Object> splineData4 = new HashMap<>();
        splineData4.put("x", l);
        splineData4.put("y", numberget);
        return splineData4;
    }



    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


   /* public void getSensorParam(String sensorId) {
        String urlSensor = historyDiagrammActivity.url + "/api/sensors/" + sensorId;
        //final String url = "http://192.168.209.189:8080/MBP/api/types";
        RequestQueue queue = Volley.newRequestQueue(getContext()); // this = context

        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, urlSensor, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("Response", response.toString());
                        JSONObject obj = null;

                        try {
                            JSONObject mainObject = response;

                            JSONObject embeddedObject = mainObject.getJSONObject("_embedded");
                            JSONObject adapterObject = embeddedObject.getJSONObject("adapter");
                            //JSONArray parameterObject = adapterObject.getJSONArray("parameters");
                            unit = adapterObject.getString("unit");


                        } catch (JSONException e) {
                            e.printStackTrace();
                            //  dialog.dismiss();

                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error.Response", "errorrrrr");
                        // dialog.dismiss();

                    }
                }) {
            //
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> authentification = getHeaderforAuthentification();
                return authentification;

            }

        };

        queue.add(getRequest);

    }*/


    public void getValuesfromSensor(HISpline spline, HIOptions options, HIChartView chartView, HIXAxis xaxis) {

        sensorValueList = new ArrayList<>();
        sensorTimeValueList = new ArrayList<>();



       // String url_forsensorvalues = url + "/api/valueLogs/search/findAllByIdref?idref=" + sensorId + "&page=0&size=10000&sort=date,desc";
       //http://192.168.209.194:8888/deploy/master/api/monitoring/5c741376f8ea1203bc69aea4/valueLogs?adapter=5ce34000f8ea1208475c8b36&size=20&sort=time,desc&unit=%E2%84%83
        //final String url = "http://192.168.209.189:8080/MBP/api/types";
        //deviceid
        //monitoring adapter
        //String url_forsensorvalues = "http://192.168.209.194:8888/deploy/master/api/monitoring/5c741376f8ea1203bc69aea4/valueLogs?adapter=5ce34000f8ea1208475c8b36&size=20&sort=time,desc&unit=%E2%84%83";
        //http://192.168.209.194:8888/deploy/master/api/monitoring/5c7fd67df8ea1203bcf381b8/valueLogs?adapter=5d8c8465b1c4d32a86a28543&size=200&sort=time,desc
        String url_forsensorvalues = url+"/api/monitoring/"+deviceid+"/valueLogs?adapter="+monitoringid+"&size=200&sort=time,desc";

        RequestQueue queue = Volley.newRequestQueue(this); // this = context
        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url_forsensorvalues, null,
                new Response.Listener<JSONObject>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("Response", response.toString());
                        JSONObject obj = null;

                        try {
                            JSONObject mainObject = response;

                            //obj = response.getJSONObject(response);

                           // Log.d("Response ", obj.toString());
                            JSONArray jsonArray = mainObject.getJSONArray("content");
                            //Log.d("Response Array", jsonArray.toString());

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject explrObject = jsonArray.getJSONObject(i);
                                String name = explrObject.getString("value");
                                JSONObject time = explrObject.getJSONObject("time");
                                String test = time.getString("epochSecond"); // returns "Marina Rasche Werft GmbH & Co. KG"

                                Date date = Date.from( Instant.ofEpochSecond(Long.valueOf(test).longValue()));
                                sensorValueList.add(Double.parseDouble(name));
                                sensorTimeValueList.add(date.toString());

                            }
                            Collections.reverse(sensorTimeValueList);
                            Collections.reverse(sensorValueList);

                            setyAchse(sensorValueList);

                            xaxis.setCategories(sensorTimeValueList);

                            ArrayList historicValueList = new ArrayList<>();

                            if (getyAchse() == null) {

                            } else {
                                for (Double j : getyAchse()) {


                                    historicValueList.add(j);


                                }

                                historicValueList.size();

                                spline.setData(historicValueList);

                                ArrayList series = new ArrayList<>();
                                series.add(spline);
                                options.setSeries(series);
                                if (historicValueList.size() != 0) {




                                }
                                chartView.reload();


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error.
                        error.printStackTrace();

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
        //This method is responsible that the request dont have a timeout
        getRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // add it to the RequestQueue
        queue.add(getRequest);
    }




    public ArrayList<Double> reverse(ArrayList<Double> list) {
        if (list.size() > 1) {
            Double value = list.remove(0);
            reverse(list);
            list.add(value);
        }
        return list;
    }



    /////////////////MQTT PART
    public void connectToMqtt(String serverURI, final String topic ) throws Exception{
        String clientId = MqttClient.generateClientId();
        final MqttAndroidClient client=
                new MqttAndroidClient(this.getApplicationContext(), serverURI,
                        clientId);

        MqttConnectOptions options = new MqttConnectOptions();
        options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
        options.setUserName("admin");
        options.setPassword("admin".toCharArray());

        try {
            IMqttToken token = client.connect(options);
        } catch (MqttException e) {
            e.printStackTrace();
            //Toast.makeText(getApplicationContext(), "Error connect to Mqtt", Toast.LENGTH_SHORT).show();
            //Toast toast = Toast.makeText(getApplicationContext(), "Error connect to Mqtt", Toast.LENGTH_SHORT);
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
                    //  Toast toast = Toast.makeText(getApplicationContext(), "Error connect to Mqtt", Toast.LENGTH_SHORT);
                    // toast.show();
                    //pubMqttChannel(client);
                    subscribeMqttChannel(client,topic);
                    //subscribe(client);

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d("TEEST", "onFailure");
                    // Toast.makeText(getApplicationContext(), "Please wait,mqtt fail"+ exception, Toast.LENGTH_SHORT).show();
                    //  Toast toast = Toast.makeText(getApplicationContext(), "Error connect to Mqtt", Toast.LENGTH_SHORT);
                    // toast.show();
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

    public void subscribeMqttChannel(MqttAndroidClient client, String channelName) {
        try {
            Log.d("tag","mqtt channel name>>>>>>>>" + channelName);
            Log.d("tag","client.isConnected()>>>>>>>>" + client.isConnected());
            if (client.isConnected()) {
                client.subscribe(channelName, 0);
                client.setCallback(new MqttCallback() {
                    @Override
                    public void connectionLost(Throwable cause) {
                        value="0";
                    }

                    @Override
                    public void messageArrived(String topic, MqttMessage message) throws Exception {
                        Log.d("tag","message>>" + new String(message.getPayload()));
                        Log.d("tag","topic>>" + topic);
                        // Toast.makeText(getApplicationContext(), "Please wait,mqtt playload" + message.getPayload() , Toast.LENGTH_SHORT).show();

                        //Toast toast = Toast.makeText(getApplicationContext(), "Error connect to Mqtt", Toast.LENGTH_SHORT);
                        //toast.show();

                        String payload = message.toString();
                        //parseMqttMessage(new String(message.getPayload()));
                        parsePayload(payload);



                    }

                    @Override
                    public void deliveryComplete(IMqttDeliveryToken token) {

                    }
                });
            }
        } catch (Exception e) {
            Log.d("tag","Error :" + e);

        }
    }

    public void parsePayload(String payload) throws JSONException {
        JSONObject mainObject = new JSONObject(String.valueOf(payload));
        value = mainObject.get("value").toString();
        // setValue("mainObject.get(\"value\").toString()");
        String id = mainObject.get("id").toString();
        String component = mainObject.get("component").toString();
    }


    ////////////////

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

    /*
        Parsing the url form sharedpref to a url for the MQTT connection
        TODO: Port is hardcoded
       */
    public String parseurl() throws MalformedURLException {
        String parseurl = "";
        URL aURL = new URL(url);
        parseurl = aURL.getHost()+":1883";


        return parseurl;

    }



}
