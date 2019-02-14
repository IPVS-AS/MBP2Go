package com.example.sedaulusal.hiwijob.historydiagramm;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sedaulusal.hiwijob.R;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class HistoryDynamicFragment extends Fragment  {
    SQLiteHelper sqLiteHelper;
    HistoryDynamicFragment context;

    TextView min, max, average;
    ArrayList<Double> sensorValues, sensorValueList;
    ArrayList<String> sensorTimeValueList;


    private static FragmentManager fragmentManager;
    int number= 1;
    View view;
    HIChart chart;
    HIEvents events;
    HIFunction hiFunction;
    ArrayList<List> list;
    HistoryDiagrammActivity historyDiagrammActivity;
    ImageButton historybutton;

    ArrayList<Double> yAchse;

    ArrayList<SensorInfo> sensorlist;
    static String TAG =  "TAG";

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

        view= inflater.inflate(R.layout.historydynamic_fragment_layout, container, false);
        historyDiagrammActivity= (HistoryDiagrammActivity) getActivity();

        ArrayList<SensorInfo> sensorInfos = historyDiagrammActivity.sensorlist;

        String sen= sensorInfos.get(getArguments().getInt("position")).getGeneratesensorid();

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
        String sen= sensorInfos.get(getArguments().getInt("position")).getGeneratesensorid();

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
        //sensorlist= sqLiteHelper.getAllSensorsByPlattformId(devicelist.get(0).getPlattformid());

        //String sensorName = sensorlist.get(1).getName();
        //title.setText(sqLiteHelper.getSensor(getArguments().getInt("position") + 1).getName());
        // DiagrammActivity diagrammActivity = new DiagrammActivity();
        // String id = diagrammActivity.sensorId;
        //title.setText(String.valueOf(diagrammActivity.getMyData()));
        title.setText("");
        options.setTitle(title);

        HIXAxis xaxis =  new HIXAxis();
        //xaxis.setType("datetime");
        //xaxis.type = "datetime";
        //String[] categoriesList = new String[] {"Apples", "Oranges", "Pears", "Grapes", "Bananas" };
        //xaxis.setCategories(new ArrayList<>(Arrays.asList(categoriesList)));
        options.setXAxis(new ArrayList<HIXAxis>(){{add(xaxis);}});

        //xaxis.tickPixelInterval = 150;
            //options.setXAxis(new ArrayList<>(Collections.singletonList(xaxis)));
        //options.xAxis = new ArrayList<>(Collections.singletonList(xaxis));

        HIYAxis yaxis =  new HIYAxis();
        // yaxis.title =  new HITitle();
        HITitle hiTitle = new HITitle();
        hiTitle.setText("value");

        yaxis.setTitle(hiTitle);
        //yaxis.title.text = "Value";
        //yaxis.setTitle(hiTitle.setText(""));;//!!!!
        HIPlotLines plotLines =  new HIPlotLines();
        //plotLines.value = 0;
        plotLines.setValue(0);
        plotLines.setWidth(1);
        plotLines.setColor(HIColor.initWithHexValue("808080"));
        yaxis.setPlotLines(new ArrayList<>(Collections.singletonList(plotLines)));
        options.setYAxis(new ArrayList<>(Collections.singletonList(yaxis)));

        HITooltip tooltip =  new HITooltip();
        tooltip.setFormatter(new HIFunction("function () { return '<b>' + this.series.name + '</b><br/>' + this.x + '<br/>' + Highcharts.numberFormat(this.y, 2); }"));
        options.setTooltip(tooltip);

        HILegend legend =  new HILegend();
        //legend.enabled = false;
        legend.setEnabled(false);
        //options.legend = legend;
        options.setLegend(legend);

        HIExporting exporting =  new HIExporting();
        //exporting.enabled = false;
        exporting.setEnabled(false);
        //options.exporting = exporting;
        options.setExporting(exporting);

        //HISpline spline =  new HISpline();
        //spline.name = "Random data";
        spline.setName("Data");
        //diagrammActivity = (DiagrammActivity) getActivity();


        HashMap<String, Object> splineData1 = getStringObjectHashMap(1506522303996L, 0.9008867958057089);
        HashMap<String, Object> splineData2 = new HashMap<>();
        splineData2.put("x", 1506522304996L);
        splineData2.put("y", 1 );
        HashMap<String, Object> splineData3 = new HashMap<>();
        splineData3.put("x", 1506522305996L);
        splineData3.put("y", 2 );
        HashMap<String, Object> splineData4 = getStringObjectHashMap(1506522306996L, 1);
        HashMap<String, Object> splineData5 = new HashMap<>();
        splineData5.put("x", 1506522307996L);
        splineData5.put("y", 4);
        HashMap<String, Object> splineData6= new HashMap<>();

        splineData6.put("x", 1506522307996L);
        splineData6.put("y", 3);

        //ArrayList<Double> yAchse;
        //yAchse = new ArrayList<>();
        //yAchse= historyDiagrammActivity.sensorValues;



        //yAchse = sensorValues;


        //int[] xArray = {1,2,3,4,5};
        //int[] yArray = {3,4,5,6,7};

        //yAchse = historyDiagrammActivity.sensorValues;
        //yAchse.add("1");
        //yAchse.add(2.0);
        //yAchse.add(3);
        //yAchse.add(4);

        ArrayList<String> xAchse;
        xAchse = new ArrayList<>();
        xAchse.add("1");
        xAchse.add("5");

        // xAchse.add(2);
       // xAchse.add(3);
       // xAchse.add(4);


        ArrayList<Object[]> listevalue;
        listevalue = new ArrayList<>();
        ArrayList<Object[]> myList1 = new ArrayList<>();


        //xaxis.setCategories(xAchse);
        //xaxis.setPlotLines(xAchse);
       // xaxis.setUnits(xAchse);
        //xaxis.setTickPositions();

        //methode(xAchse,yAchse);

        //list =new ArrayList(listevalue);





        //methode1(xAchse,yAchse);
        //listevalue = myList1;

        //spline.data =  new ArrayList<>(Arrays.asList(splineData1, splineData2, splineData3, splineData4, splineData5, splineData6, splineData7, splineData8, splineData9, splineData10, splineData11, splineData12, splineData13, splineData14, splineData15, splineData16, splineData17, splineData18, splineData19, splineData20));
       // spline.setData(new ArrayList<>(Arrays.asList(splineData1, splineData2, splineData3, splineData4, splineData5)));
        //ArrayList<List> list =new ArrayList(Arrays.asList(splineData1, splineData2, splineData3, splineData4, splineData5));

        //, splineData6, splineData7, splineData8, splineData9, splineData10, splineData11, splineData12, splineData13, splineData14, splineData15, splineData16, splineData17, splineData18, splineData19, splineData20)));
        //options.series = new ArrayList<>(Collections.singletonList(spline));
        //  spline.setPoint(new HIPoint());
        //HIPoint hiPoint = new HIPoint();
        //hiPoint.setX(3);
        //hiPoint.setY(4);
        // spline.setPoint(hiPoint);
      /*  spline.getPoint().setEvents(new HIEvents());

        spline.getPoint().getEvents().setClick(new HIFunction(
                this::accept,
                new String[] {"x", "y"}
        ));*/


        //HashMap<String, Object> splineData1 = getStringObjectHashMap(1506522303996L, givenList_shouldReturnARandomElement());
        //HashMap<String, Object> splineData2 = getStringObjectHashMap(5, givenList_shouldReturnARandomElement());
        //HashMap<String, Object> splineData5 = getStringObjectHashMap(10, 6);

        //list =new ArrayList(Arrays.asList( splineData1, splineData2,splineData5));



       // list.add(new ArrayList(Arrays.asList(splineData6)));
       // spline.setData(list);

        //ArrayList<List> list =new ArrayList(Arrays.asList(splineData1, splineData1, splineData1));
        //options.setSeries(new ArrayList<HISeries>(Collections.singletonList(spline)));
        //options.setSeries(new ArrayList<HISeries>((Collection<? extends HISeries>) spline));
        chart.setZoomType("x");
        //HIScrollablePlotArea scroll = new HIScrollablePlotArea();
        //scroll.setMinWidth(700);
        //scroll.setScrollPositionX(1);
        //chart.setScrollablePlotArea(scroll);

        getValuesfromSensor(sen, spline,options,chartView,xaxis);
        //chartevent();
        chartView.setOptions(options);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                chartView.reload();

            }
        }, 800);




    }




   /* Runnable helloRunnable = new Runnable() {
        public void run() {
            String s = "function request () { var series = this.series[0]; setInterval(function () { var x = (new Date()).getTime(), y ="+newInstance().test()+"; series.addPoint([x, y], true, true); }, 2000); }"));


        }
    };*/



    public double test(){
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



    public void getValuesfromSensor(String sensorId, HISpline spline, HIOptions options, HIChartView chartView, HIXAxis xaxis){

        sensorValueList = new ArrayList<>();
        sensorTimeValueList = new ArrayList<>();

        String url_forsensorvalues= historyDiagrammActivity.url +"/api/valueLogs/search/findAllByIdref?idref="+ sensorId+"&page=0&size=10000&sort=date,desc";
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
                                //String description = explrObject.getString("description");
                                //temperaturid = explrObject.getString("id");
                                //Log.d("Response Tempid", temperaturid);
                                //sensorTypeIDName.put(name, temperaturid);
                                //spinnerSensorAdapter.add(name);
                                sensorValueList.add(Double.parseDouble(name));
                                sensorTimeValueList.add(time);
                               // Collections.reverse(sensorValueList);

                               // sensorValues = sensorValueList;
                            }
                            Collections.reverse(sensorTimeValueList);
                            Collections.reverse(sensorValueList);

                            //sensorValues = reverse(sensorValueList);
                            setyAchse(sensorValueList);

                            xaxis.setCategories(sensorTimeValueList);

                            ArrayList realMadridData = new ArrayList<>();

                            if(getyAchse() == null){
                                Toast.makeText(getContext(), "Please load again", Toast.LENGTH_SHORT).show();

                            }else {
                                for (Double j : getyAchse()) {


                                    realMadridData.add(j);


                                }

                                realMadridData.size();

                                spline.setData(realMadridData);

                                ArrayList series = new ArrayList<>();
                                series.add(spline);
                                options.setSeries(series);
                                if(realMadridData.size()!=0){
                                max.setText(Collections.max(realMadridData).toString());
                                min.setText(Collections.min(realMadridData).toString());
                                average.setText(String.valueOf(calculateAverage(realMadridData)));}
                                // options.setXAxis(series);
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
                        //Log.d("Error.Response", response);
                    }
                }
        );

        // add it to the RequestQueue
        queue.add(getRequest);
   }

    private double calculateAverage(ArrayList<Double> list) {
        Double sum = 0.0;
        if(!list.isEmpty()) {
            for (Double mark : list) {
                sum += mark;
            }
            return sum.doubleValue() / list.size();
        }
        return sum;
    }

    public ArrayList<Double> reverse(ArrayList<Double> list) {
        if(list.size() > 1) {
            Double value = list.remove(0);
            reverse(list);
            list.add(value);
        }
        return list;
    }

}
