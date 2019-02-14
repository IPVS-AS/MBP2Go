package com.example.sedaulusal.hiwijob.diagramm;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.example.sedaulusal.hiwijob.R;
import com.example.sedaulusal.hiwijob.Updateable;
import com.example.sedaulusal.hiwijob.device.SQLiteHelper;
import com.example.sedaulusal.hiwijob.device.SensorInfo;

import java.util.ArrayList;


public class DynamicFragment extends Fragment implements Updateable {

    SQLiteHelper sqLiteHelper;
    private static FragmentManager fragmentManager;
    int number = 1;
    View view;
   WebView wv;

    DiagrammActivity diagrammActivity;
    ArrayList<SensorInfo> sensorlist;
    static String TAG = "TAG";

    public static DynamicFragment newInstance() {
        return new DynamicFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    public void setWv(WebView wv) {
        this.wv = wv;
    }

    public WebView getWv() {
        return wv;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        sqLiteHelper = new SQLiteHelper(getContext());

        view = inflater.inflate(R.layout.dynamic_fragment_layout, container, false);


       //TextView textView = view.findViewById(R.id.commonTextView);
       TextView dynamicfragmentvalue = view.findViewById(R.id.dynamic_fragment_value);
       // TextView sensorid = view.findViewById(R.id.dynamic_fragment_sensorid);

        diagrammActivity = (DiagrammActivity) getActivity();

        //String name = diagrammActivity.lastWord; //id device
        ArrayList<SensorInfo> sensorInfos = diagrammActivity.sensorlist;

        //textView.setText(String.valueOf("Category :  "+getArguments().getInt("position")));

       // textView.setText(String.valueOf("Category :  " + sensorInfos.get(getArguments().getInt("position")).getName()));
      //  sensorid.setText(String.valueOf("Category :  " + sensorInfos.get(getArguments().getInt("position")).getGeneratesensorid()+" "+sensorInfos.get(getArguments().getInt("position")).getSensorTyp()));



        wv = (WebView) view.findViewById(R.id.webView);
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
                    diagrammActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            wv.evaluateJavascript("curValue = " + Double.parseDouble( diagrammActivity.value) + ";", null);
                            //wv.evaluateJavascript("series: [{\n" +
                              //      "    name:'"+ String.valueOf( Math.random())+"'," , null);
                            dynamicfragmentvalue.setText(String.format("%.2f", Double.parseDouble( diagrammActivity.value)));
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


        fragmentManager = this.getFragmentManager();//Get Fragment Manager

        return view; //return view

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

    @Override
    public void update() {
        wv.reload();
    }
}