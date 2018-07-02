package com.gyg.lenovo.world;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.PrivateKey;
import java.util.Map;
import java.util.PriorityQueue;

public class myinformation extends AppCompatActivity {
    private CircleImageView circleImageView;
    private Bitmap bitmap;
    private Context context;
    private InformationView level,account,gender,signings,address,hoby;
    private MyApp myApp;
    private InformationView name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myinformation);
        myApp = (MyApp) getApplication();
        context = getBaseContext();
        circleImageView = (CircleImageView) findViewById(R.id.imageBox);
        level = (InformationView) findViewById(R.id.level);
        account = (InformationView) findViewById(R.id.account);
        gender = (InformationView) findViewById(R.id.gender);
        signings = (InformationView) findViewById(R.id.signing);
        address = (InformationView) findViewById(R.id.address);
        hoby = (InformationView) findViewById(R.id.hoby);
        name = (InformationView) findViewById(R.id.name);
        new GetContent().start();
    }
    public void setContent(JSONObject content){
        try{
            if(bitmap != null){
                circleImageView.setImageBitmap(bitmap);
            }
            level.setText("昵称:" + content.getString("nickname"));
            account.setText("账号:" + content.getString("phone_number"));
            gender.setText("性别:" + content.getString("gender"));
            String sig = "";
            if(content.getString("signature").length() >= 20){
                sig = sig + content.getString("signature").substring(0,20)+"...";
            }
            else{
                sig = sig + content.getString("signature");
            }
            signings.setText("签名:" + sig);
            address.setText("地址:" + content.getString("address"));
            hoby.setText("爱好:" + content.getString("habit"));
            name.setText("姓名:" + content.getString("name"));
            level.setOnClickListener(new InformaitonClickLIstern(".ChangeNickName",content.getString("nickname")));
            gender.setOnClickListener(new InformaitonClickLIstern(".EditGender",content.getString("gender")));
            signings.setOnClickListener(new InformaitonClickLIstern(".EditSigning",content.getString("signature")));
            address.setOnClickListener(new InformaitonClickLIstern(".EditAddress",content.getString("address")));
            hoby.setOnClickListener(new InformaitonClickLIstern(".EditHoby",content.getString("habit")));
            name.setOnClickListener(new InformaitonClickLIstern(".EditName",content.getString("name")));
        }
        catch (Exception e){
            Log.e("error in setInfor",e.toString());
        }
    }
    class  GetContent extends Thread{
        @Override
        public void run() {
            Integer user_id = myApp.user_id;
            try {
                JSONObject param = new JSONObject("{\"id\":"+String.valueOf(user_id)+"}");
                JSONObject jsonObject = Conn.doJsonPost("/userinfo/get_userinfo_all",param);
                Message msg = handler.obtainMessage();
                jsonObject = jsonObject.getJSONArray("result").getJSONObject(0);
                msg.obj = jsonObject;
                bitmap = MessageAdapter.getImage(Uri.parse(jsonObject.getString("profile_photo")));
                handler.sendMessage(msg);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public Uri getHeadImage(){
        Uri iv = null;
        return iv;
    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            JSONObject jsonObject = (JSONObject) msg.obj;
            setContent(jsonObject);
        }
    };
    class InformaitonClickLIstern implements View.OnClickListener {
        private String pakageName = "com.gyg.lenovo.world";
        private String toActivity;
        private String params;
        InformaitonClickLIstern(String toActivity, String params){
            this.toActivity = toActivity;
            this.params = params;
        }
        @Override
        public void onClick(View view) {
            Intent intent = new Intent();
            intent.setClassName(pakageName,pakageName+toActivity);
            PackageManager pm = context.getPackageManager();
            Bundle bundle = new Bundle();
            bundle.putString("data",this.params);
            intent.putExtra("data",bundle);
            if(intent.resolveActivity(pm) != null){
                startActivity(intent);
            }
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        String pakageName = "com.gyg.lenovo.world";
        String toActivity = ".BottomNavigation";
        Intent intent = new Intent();
        intent.setClassName(pakageName,pakageName+toActivity);
        Bundle bundle = new Bundle();
        bundle.putInt("fragment",4);
        intent.putExtra("data",bundle);
        PackageManager pm = getPackageManager();
        finishActivity(1);
        if(intent.resolveActivity(pm) != null){
            startActivity(intent);
            finish();
        }
    }
}
