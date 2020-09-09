package com.example.sedaulusal.hiwijob.ruleEngine;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.ViewFlipper;

import com.example.sedaulusal.hiwijob.R;

public class RuleEngineActionActivity extends AppCompatActivity {

    ViewFlipper vf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rule_engine_action);


        /**
         * definition of first spinner with raspberry/arduino etc.
         * view with animation
         */
        vf = (ViewFlipper) findViewById(R.id.viewflipper_rule);
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.viewspinner);
        //relativeLayout.setMinimumHeight(startHeight);

        Spinner spinner = (Spinner) findViewById(R.id.rule_spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String selectedItem = parent.getItemAtPosition(position).toString();
                if (selectedItem.equals("Actuator action")) {
                    vf.setDisplayedChild(0);

                }

                if (selectedItem.equals("IFTT webhook")) {
                    vf.setDisplayedChild(1);
                }
                if (selectedItem.equals("Component deployment")) {
                    vf.setDisplayedChild(2);
                }

            } // to close the onItemSelected

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

}
