package com.example.sedaulusal.hiwijob.ruleEngine;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.sedaulusal.hiwijob.R;

public class RuleEngingeSensorSettingsActivity extends AppCompatActivity {

    TextView ruleengine_setting_sensorname;
    EditText ruleengine_setting_edittext;
    ToggleButton ruleengine_togglebutton_operator;
    Button button;
    String sensorname, ruleengine_rulesensorgenerateid;
    int counter = 0;
    //final String[] list = {">", "<", ">=", "<=", "="};; //get the array


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rule_enginge_sensor_settings);

       sensorname = getIntent().getExtras().getString("ruleengine_rulesensorname");
       ruleengine_rulesensorgenerateid = getIntent().getExtras().getString("ruleengine_rulesensorgenerateid");
        ruleengine_setting_sensorname = (TextView) findViewById(R.id.ruleengine_setting_sensorname);
        //sensorname
        ruleengine_setting_sensorname.setText(sensorname);
        ruleengine_setting_edittext = (EditText) findViewById(R.id.ruleengine_setting_edittext);
        //ruleengine_togglebutton_operator=(ToggleButton) findViewById(R.id.ruleengine_toggleButton_operator);
        button = (Button) findViewById(R.id.sensorsettingbutton);

        final String[] list = getResources().getStringArray(R.array.list); //get the array


        button.setText (list[counter]); //set the initial message.

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tx = (TextView) findViewById(R.id.textView2);
                counter++;
                if (counter >= list.length)
                    counter = 0;

                button.setText(list[counter]); //set the new message.

            }
        });


    }

    public void ruleengine_saveSetting(View v){
       /* Intent intent = new Intent(RuleEngingeSensorSettingsActivity.this, RuleEngineActivity.class);

        intent.putExtra("ruleengine_fromsetting_rulesensorname", sensorname);
        intent.putExtra("ruleengine_fromsetting_rulesensorgenerateid", ruleengine_rulesensorgenerateid);

        intent.putExtra("ruleengine_fromsetting_value", ruleengine_setting_edittext.getText().toString());
        intent.putExtra("ruleengine_fromsetting_operator", ruleengine_togglebutton_operator.getText().toString());
        Bundle bundle = new Bundle();
        intent.putExtras(bundle);
        //startActivity(intent);*/

        Intent returnIntent = new Intent();
        returnIntent.putExtra("ruleengine_fromsetting_rulesensorname", sensorname);
        returnIntent.putExtra("ruleengine_fromsetting_rulesensorgenerateid", ruleengine_rulesensorgenerateid);
        returnIntent.putExtra("ruleengine_fromsetting_value", ruleengine_setting_edittext.getText().toString());
        returnIntent.putExtra("ruleengine_fromsetting_operator", button.getText().toString());

        //returnIntent.putExtra("result",result);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
        //---close the activity---
        finish();


    }
}
