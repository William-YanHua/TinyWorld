package com.gyg.lenovo.world;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

public class EditName extends AppCompatActivity {
    private EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_name);
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("data");
        editText = (EditText) findViewById(R.id.edit_name);
        editText.setText(bundle.getString("data"));
        MyApp myApp;
        myApp = (MyApp) getApplication();
        editText.addTextChangedListener(new UpdateUserInfo.MyTextWatcher("name",myApp));
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
