package com.example.sedaulusal.hiwijob.monitoring;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.example.sedaulusal.hiwijob.R;
import com.example.sedaulusal.hiwijob.device.SensorInfo;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class MonitoringDiagrammActivity extends AppCompatActivity {

    private TextView mTextMessage;
    View view;
    HIChartView chartView;
    MonitoringDiagrammActivity monitoringDiagrammActivity;


    public void setWv(WebView wv) {
        this.wv = wv;
    }

    private WebView wv;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {


        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    //chartView.setVisibility(View.GONE);
                    //wv.setVisibility(View.VISIBLE);
                    mTextMessage.setText(R.string.title_home);

                    wv.getSettings().setSafeBrowsingEnabled(false);
                    WebSettings settings = wv.getSettings();
                    settings.setJavaScriptEnabled(true);
                    wv.loadUrl("file:///android_asset/highchart.html");
                    setWv(wv);
                    String value = String.valueOf( Math.random());
                    wv.evaluateJavascript("curValue = " + Double.parseDouble(value) + ";", null);
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
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        wv.evaluateJavascript("curValue = " + Double.parseDouble(value) + ";", null);
                                    }
                                });

                            }
                        }
                    }).start();
                    // final Context self = this;
                    return true;
                case R.id.navigation_dashboard:
                   // wv.setVisibility(View.GONE);
                   // chartView.setVisibility(View.VISIBLE);
                    mTextMessage.setText(R.string.title_dashboard);
                    wv.getSettings().setSafeBrowsingEnabled(false);
                    settings = wv.getSettings();
                    settings.setJavaScriptEnabled(true);
                    wv.loadUrl("file:///android_asset/historyhighchart.html");
                    setWv(wv);
                    value = "1";


                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoringdiagramm);

        mTextMessage = (TextView) findViewById(R.id.message);
        wv = (WebView) findViewById(R.id.monitoring_webview);


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }





}
