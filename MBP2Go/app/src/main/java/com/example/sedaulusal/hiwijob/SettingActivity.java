package com.example.sedaulusal.hiwijob;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.MultiAutoCompleteTextView;

import java.util.ArrayList;

public class SettingActivity extends AppCompatActivity {
    ArrayList<String> settingURLList;
    SharedPreferences sharedpreferences;
    public static final String mypreference = "mypref";
    public static final String URL = "URL";
    public static final String Email = "emailKey";
    MultiAutoCompleteTextView multiAutoCompleteTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);



        settingURLList = new ArrayList<>();
        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, sharedpreferences.getString("historyURLS", "").split("\\|" +
                ""));
        String[] test = sharedpreferences.getString("historyURLS", "").split("\\|");
        multiAutoCompleteTextView = (MultiAutoCompleteTextView) findViewById(R.id.activitysetting_multiAutoCompleteTextView);
        multiAutoCompleteTextView.setAdapter(adapter);
        multiAutoCompleteTextView.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        if (sharedpreferences.contains(URL)) {
            multiAutoCompleteTextView.setText(sharedpreferences.getString(URL, ""));
        }

    }

    /*
    Method to save the url which is given in the multiAutoCompleteTextView
    saved with sharedpreferences with the Key URL
    historyURLS save all Url which was given for the Autocomplete
     */
    public void saveUrl(View view) {
       // String n = name.getText().toString();
       // String e = email.getText().toString();
        String url = multiAutoCompleteTextView.getText().toString();
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(URL, url);
        editor.putString("historyURLS", sharedpreferences.getString("historyURLS","")+"|"+url);

        //editor.putString(Email, e);
        editor.commit();
        //settingURLList.add(sharedpreferences.getString(URL, ""));
    }

    /*
    for testing a Method
     */
    public void clearMultiCompleteTextView(View view) {
        //name = (TextView) findViewById(R.id.etName);
        //email = (TextView) findViewById(R.id.etEmail);
        //name.setText("");
        //email.setText("");
        multiAutoCompleteTextView = (MultiAutoCompleteTextView) findViewById(R.id.activitysetting_multiAutoCompleteTextView);
        multiAutoCompleteTextView.setText("");
    }

    public void getUrl(View v) {
        multiAutoCompleteTextView = (MultiAutoCompleteTextView) findViewById(R.id.activitysetting_multiAutoCompleteTextView);

        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);

        if (sharedpreferences.contains(URL)) {
            multiAutoCompleteTextView.setText(sharedpreferences.getString(URL, ""));
        }
    }

    public String getUrltest() {
        //multiAutoCompleteTextView = (MultiAutoCompleteTextView) findViewById(R.id.activitysetting_multiAutoCompleteTextView);

        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        String url = sharedpreferences.getString(URL, "");
        return url;

    }

    /*
    Method when Button isClicked
     */
    public void settingInsertUrl(View v){
        saveUrl(v);
        getUrl(v);
    }


}






//}

