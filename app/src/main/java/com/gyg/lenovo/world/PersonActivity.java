package com.gyg.lenovo.world;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.PrivateKey;
import java.util.Map;
import java.util.PriorityQueue;

public class PersonActivity extends AppCompatActivity {

    private CircleImageView circleImageView;
    private Bitmap bitmap;
    private Context context;
    private InformationView level,account,gender,signings,address,hoby,name;
    private Integer Rid;
    private Integer userId;
    private MyApp myApp;
    private AddTask addTask = null;
    private Boolean isFriend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
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
        Intent intent = getIntent();
        Bundle b = intent.getBundleExtra("data");
        Rid = b.getInt("id");
        isFriend = b.getBoolean("isFriend");
        new GetContent().start();
        Button moreButton = (Button) findViewById(R.id.more_button);
        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myApp.center_id = Rid;
                Intent intent = new Intent();
                intent.setClass(PersonActivity.this,WorldActivity.class);
                startActivity(intent);
            }
        });

        Button addButton = (Button) findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptAdd();
                Toast.makeText(PersonActivity.this,"消息已发送",Toast.LENGTH_LONG).show();
            }
        });
        if(isFriend) {
            addButton.setVisibility(View.GONE);
        }
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
        }
        catch (Exception e){
            Log.e("error in setInfor",e.toString());
        }
    }

    class  GetContent extends Thread{
        @Override
        public void run() {
            try {
                JSONObject param = new JSONObject("{\"id\":"+String.valueOf(Rid)+"}");
                JSONObject jsonObject = Conn.doJsonPost("/userinfo/get_userinfo_all",param);
                Message msg = handler.obtainMessage();
                jsonObject = jsonObject.getJSONArray("result").getJSONObject(0);
                msg.obj = jsonObject;
//                bitmap = MessageAdapter.getImage(Uri.parse("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=3481560223,3633833626&fm=27&gp=0.jpg"));
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        String pakageName = "com.gyg.lenovo.world";
        String toActivity = ".BottomNavigation";
        Intent intent = new Intent();
        intent.setClassName(pakageName,pakageName+toActivity);
        PackageManager pm = getPackageManager();
        finishActivity(1);
        if(intent.resolveActivity(pm) != null){
            startActivity(intent);
        }
    }

    private void attemptAdd() {
        if(addTask != null) {
            return;
        }
        addTask = new AddTask(Rid);
        addTask.execute((Void) null);
    }

    public class AddTask extends AsyncTask<Void, Void, Boolean> {
        private final Integer mid;

        AddTask(Integer id) {
            mid=id;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                JSONObject json = new JSONObject();
                userId = myApp.user_id;
                json.put("sender",userId);
                json.put("receiver",mid);
                json.put("content","邀请您成为好友~");
                JSONObject obj = Conn.doJsonPost("/invitation/send",json);
                Integer code = obj.getInt("code");
                String msg = obj.getString("msg");
                //System.out.println(res);
                if(code == 200) {
                   return true;
               } else {
                    System.out.println(msg);
                   return false;
                }
                // Simulate network access.
                // Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            addTask = null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            addTask = null;
        }
    }
}
