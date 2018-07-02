package com.gyg.lenovo.world;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

public class EditGender extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_gender);
        Intent intent = getIntent();
        Spinner editText = (Spinner) findViewById(R.id.edit_gender);
        final MyApp myApp;
        myApp = (MyApp) getApplication();
        editText.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Integer user_id = myApp.user_id;
                String gender;
                if(id == 0){
                    gender = "male";
                }
                else if(id == 1){
                    gender = "female";
                }
                else{
                    gender = "else";
                }
                String query_condition =  "{\"id\":"+String.valueOf(user_id)+",\"Attributes\":" +
                        "{gender:"+ gender +"}}";
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(query_condition);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.i("content changed",jsonObject.toString());
                new UpdateUserInfo.ModifyThread("/userinfo/update_userinfo",jsonObject).start();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        String pakageName = "com.gyg.lenovo.world";
        String toActivity = ".myinformation";
        Intent intent = new Intent();
        intent.setClassName(pakageName,pakageName+toActivity);
        PackageManager pm = getPackageManager();
        finishActivity(1);
        if(intent.resolveActivity(pm) != null){
            startActivity(intent);
        }
    }

}
