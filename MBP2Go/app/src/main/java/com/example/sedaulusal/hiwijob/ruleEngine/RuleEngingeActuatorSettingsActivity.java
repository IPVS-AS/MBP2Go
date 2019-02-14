package com.example.sedaulusal.hiwijob.ruleEngine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.sedaulusal.hiwijob.R;

public class RuleEngingeActuatorSettingsActivity extends AppCompatActivity {

    TextView ruleengine_setting_actuatorname;
    EditText ruleengine_setting_edittext;
    //ToggleButton ruleengine_togglebutton_operator;
    String actuatorname, ruleengine_ruleactuatorgenerateid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rule_enginge_actuator_settings);

       actuatorname = getIntent().getExtras().getString("ruleengine_ruleactuatorname");
       ruleengine_ruleactuatorgenerateid = getIntent().getExtras().getString("ruleengine_ruleactuatorgenerateid");
        ruleengine_setting_actuatorname = (TextView) findViewById(R.id.ruleengine_setting_actuatorname);
        ruleengine_setting_actuatorname.setText(actuatorname);
        ruleengine_setting_edittext = (EditText) findViewById(R.id.ruleengine_setting_actuator_edittext);
        //ruleengine_togglebutton_operator=(ToggleButton) findViewById(R.id.ruleengine_toggleButton_operator);



    }

    public void ruleengine_saveSetting_actuator(View v){
        Intent intent = new Intent();
        intent.putExtra("ruleengine_fromsetting_ruleactuatorname", actuatorname);
        intent.putExtra("ruleengine_fromsetting_ruleactuatorgenerateid", ruleengine_ruleactuatorgenerateid);

        intent.putExtra("ruleengine_fromsetting_value_actuator", ruleengine_setting_edittext.getText().toString());
       // intent.putExtra("ruleengine_fromsetting_operator", ruleengine_togglebutton_operator.getText().toString());
        Bundle bundle = new Bundle();
        intent.putExtras(bundle);
        //startActivity(intent);


        //returnIntent.putExtra("result",result);
        setResult(Activity.RESULT_OK,intent);
        finish();

    }
}
