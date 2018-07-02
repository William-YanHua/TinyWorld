package com.gyg.lenovo.world;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

public class EditAddress extends AppCompatActivity {
    private EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApp myApp;
        myApp = (MyApp) getApplication();
        setContentView(R.layout.activity_edit_address);
        Context context = getBaseContext();
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("data");
        editText = (EditText) findViewById(R.id.edit_address);
        editText.setText(bundle.getString("data"));
        editText.addTextChangedListener(new UpdateUserInfo.MyTextWatcher("address",myApp));
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        String pakageName = "com.gyg.lenovo.world";
        String toActivity = ".myinformation";
        Intent intent = new Intent();
        intent.setClassName(pakageName,pakageName+toActivity);
        PackageManager pm = getPackageManager();
        if(intent.resolveActivity(pm) != null){
            startActivity(intent);
        }
    }
}
