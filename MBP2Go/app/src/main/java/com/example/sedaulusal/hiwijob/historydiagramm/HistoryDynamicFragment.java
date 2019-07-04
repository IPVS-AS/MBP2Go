package com.example.sedaulusal.hiwijob.historydiagramm;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sedaulusal.hiwijob.R;
import com.example.sedaulusal.hiwijob.device.DeviceOverviewActivity;
import com.example.sedaulusal.hiwijob.device.DeviceRegistryActivity;
import com.example.sedaulusal.hiwijob.device.SQLiteHelper;
import com.example.sedaulusal.hiwijob.device.SensorInfo;
import com.highsoft.highcharts.Common.HIChartsClasses.HIChart;
import com.highsoft.highcharts.Common.HIChartsClasses.HIEvents;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/*
class which generate the fragment for the history Data
it shows the diagramm for history data and calculate the max, min, average values
 */
public class HistoryDynamicFragment extends Fragment {
    SQLiteHelper sqLiteHelper;
    HistoryDynamicFragment context;

    TextView min, max, average;
    ArrayList<Double> sensorValues, sensorValueList;
    ArrayList<String> sensorTimeValueList;


    private static FragmentManager fragmentManager;
    int number = 1;
    View view;
    HIChart chart;
    HIEvents events;
    HIFunction hiFunction;
    ArrayList<List> list;
    HistoryDiagrammActivity historyDiagrammActivity;
    ImageButton historybutton;

    ArrayList<Double> yAchse;

    ArrayList<SensorInfo> sensorlist;
    static String TAG = "TAG";
    String unit = " ";

    public static HistoryDynamicFragment newInstance() {
        return new HistoryDynamicFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        sqLiteHelper = new SQLiteHelper(getContext());

        view = inflater.inflate(R.layout.historydynamic_fragment_layout, container, false);
        historyDiagrammActivity = (HistoryDiagrammActivity) getActivity();

        ArrayList<SensorInfo> sensorInfos = historyDiagrammActivity.sensorlist;

        String sen = sensorInfos.get(getArguments().getInt("position")).getGeneratesensorid();

        //getValuesfromSensor(sen);
        min = (TextView) view.findViewById(R.id.minValue);
        max = (TextView) view.findViewById(R.id.maxValue);
        average = (TextView) view.findViewById(R.id.t);
        historybutton = (ImageButton) view.findViewById(R.id.btn_History_actual);

        initViews(view);

        historybutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initViews(view);
            }
        });

        Button deleteHistoryValues = (Button) view.findViewById(R.id.button_deleteHistoryValues);
        deleteHistoryValues.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(getActivity());
                myAlertDialog.setTitle("Delete value data");
                myAlertDialog.setMessage("Are you sure you want to delete all value data that has been recorded so far for this component? This action cannot be undone.");

               // List<String> items = new ArrayList<String>();
               // items.add("RED");
               // items.add("BLUE");


               // final ArrayAdapter<String> arrayAdapterItems = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_expandable_list_item_1, items);

                myAlertDialog.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do stuff
                    }
                });
                myAlertDialog.setPositiveButton("Delete",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do stuff
                        deleteHistoryValues(sen);
                        initViews(view);
                    }
                });
                myAlertDialog.show();


            }
        });

        //return view;

        // View view = inflater.inflate(R.layout.fragmentlayout, container, false);//Inflate Layout
        // TextView text = (TextView) view.findViewById(R.id.commonTextView);//Find textview Id


        //Get Argument that passed from activity in "data" key value
        //String getArgument = getArguments().getString("sensorInfoGenerateSensorId");
        fragmentManager = this.getFragmentManager();//Get Fragment Manager
        //chartView.reload();
        return view;//return view

    }


    public void setyAchse(ArrayList<Double> yAchse) {
        this.yAchse = yAchse;
    }

    public ArrayList<Double> getyAchse() {
        return yAchse;
    }

    private void initViews(View view) {

        //TextView textView=view.findViewById(R.id.commonTextView);
        // diagrammActivity = (DiagrammActivity) getActivity();

        //String name = diagrammActivity.lastWord; //id device
        ArrayList<SensorInfo> sensorInfos = historyDiagrammActivity.sensorlist;
        String sen = sensorInfos.get(getArguments().getInt("position")).getGeneratesensorid();

        //getValuesfromSensor(sen);
        //textView.setText(String.valueOf("Category :  "+getArguments().getInt("position")));
        // textView.setText(String.valueOf("Category :  "+ sensorInfos.get(getArguments().getInt("position")).getName()));

        // ArrayList<Double> sensorvalue = historyDiagrammActivity.sensorValues;
        HIChartView chartView = (HIChartView) view.findViewById(R.id.webView);

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

        getSensorParam(sen);
        getValuesfromSensor(sen, spline, options, chartView, xaxis);

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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public void getSensorParam(String sensorId) {
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

    }


    public void getValuesfromSensor(String sensorId, HISpline spline, HIOptions options, HIChartView chartView, HIXAxis xaxis) {

        sensorValueList = new ArrayList<>();
        sensorTimeValueList = new ArrayList<>();

        max.setText("");
        min.setText("");
        average.setText("");

        String url_forsensorvalues = historyDiagrammActivity.url + "/api/valueLogs/search/findAllByIdref?idref=" + sensorId + "&page=0&size=10000&sort=date,desc";
        //final String url = "http://192.168.209.189:8080/MBP/api/types";
        RequestQueue queue = Volley.newRequestQueue(getContext()); // this = context
        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url_forsensorvalues, null,
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
                            JSONArray jsonArray = obj.getJSONArray("valueLogs");
                            Log.d("Response Array", jsonArray.toString());

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject explrObject = jsonArray.getJSONObject(i);
                                String name = explrObject.getString("value");
                                String time = explrObject.getString("date");

                                sensorValueList.add(Double.parseDouble(name));
                                sensorTimeValueList.add(time);

                            }
                            Collections.reverse(sensorTimeValueList);
                            Collections.reverse(sensorValueList);

                            setyAchse(sensorValueList);

                            xaxis.setCategories(sensorTimeValueList);

                            ArrayList historicValueList = new ArrayList<>();

                            if (getyAchse() == null) {
                                Toast.makeText(getContext(), "Please load again", Toast.LENGTH_SHORT).show();

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

                                    Double maxValue = Double.valueOf(Collections.max(historicValueList).toString());

                                    Double minValue = Double.valueOf(Collections.min(historicValueList).toString());

                                    Double averageValue = calculateAverage(historicValueList);

                                    //if unit is null set ""
                                    if (unit.contains("null")) {
                                        unit = "";
                                    }


                                    max.setText(new DecimalFormat("##.##", new DecimalFormatSymbols(Locale.US))
                                            .format(maxValue) + " " + unit);

                                    min.setText(new DecimalFormat("##.##", new DecimalFormatSymbols(Locale.US))
                                            .format(minValue) + " " + unit);

                                    average.setText(new DecimalFormat("##.##", new DecimalFormatSymbols(Locale.US))
                                            .format(averageValue) + " " + unit);
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


        // add it to the RequestQueue
        queue.add(getRequest);
    }

    /*
    method to delete the history values from database
     */
    public void deleteHistoryValues(String sensorInfoGenerateID) {
        //ArrayList<SensorInfo> sensorInfos = historyDiagrammActivity.sensorlist;
        //String sen = sensorInfos.get(getArguments().getInt("position")).getGeneratesensorid();

        String url_forsensorvalues = historyDiagrammActivity.url + "/api/sensors/" + sensorInfoGenerateID + "/valueLogs";
        //final String url = "http://192.168.209.189:8080/MBP/api/types";
        RequestQueue queue = Volley.newRequestQueue(getContext()); // this = context
        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.DELETE, url_forsensorvalues, null,
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
                            JSONArray jsonArray = obj.getJSONArray("valueLogs");
                            Log.d("Response Array", jsonArray.toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error.

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


        // add it to the RequestQueue
        queue.add(getRequest);
    }


    private double calculateAverage(ArrayList<Double> list) {
        Double sum = 0.0;
        if (!list.isEmpty()) {
            for (Double mark : list) {
                sum += mark;
            }
            return sum.doubleValue() / list.size();
        }
        return sum;
    }

    public ArrayList<Double> reverse(ArrayList<Double> list) {
        if (list.size() > 1) {
            Double value = list.remove(0);
            reverse(list);
            list.add(value);
        }
        return list;
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
