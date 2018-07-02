package com.gyg.lenovo.world;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

public class EditSigning extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_signing);
        Intent intent = getIntent();
        Context context = getBaseContext();
        Bundle bundle = intent.getBundleExtra("data");
        EditText editText = (EditText) findViewById(R.id.edit_signing);
        editText.setText(bundle.getString("data"));
        MyApp myApp;
        myApp = (MyApp) getApplication();
        editText.addTextChangedListener(new UpdateUserInfo.MyTextWatcher("signature",myApp));
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
