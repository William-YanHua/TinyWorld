package com.gyg.lenovo.world;

import android.app.LauncherActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MessageAction extends AppCompatActivity {
    private List<Bitmap> bitmap;
    private List<String> name;
    private List<String> content;
    private ListView listView;
    private Button agree;
    private Button disagree;
    private Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messageaction);
        bitmap = new ArrayList<>();
        name = new ArrayList<>();
        content = new ArrayList<>();
        listView = (ListView) findViewById(R.id.message_content);
        agree = (Button) findViewById(R.id.agree);
        disagree = (Button) findViewById(R.id.disagree);
        Log.i("InMessageAction","yes");
        try{
            Intent intent = getIntent();
            bundle = intent.getBundleExtra("data");
            name.add(bundle.getString("nickname"));
            content.add(bundle.getString("content"));
            String imageUri = bundle.getString("image");
            if(bundle.getString("sender").equals("0")){
                disagree.setVisibility(View.GONE);
                agree.setVisibility(View.GONE);
            }
            new SetWhole(imageUri).start();
        }
        catch (Exception e){
            Log.e("error message",e.toString());
        }
        agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new updateInviteStatus("accepted").start();
                disagree.setVisibility(View.GONE);
                agree.setVisibility(View.GONE);
            }
        });
        disagree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new updateInviteStatus("rejected").start();
                agree.setVisibility(View.GONE);
                disagree.setVisibility(View.GONE);
            }
        });

    }
    class updateInviteStatus extends Thread{
        private String status;
        updateInviteStatus(String status){
            this.status = status;
        }
        @Override
        public void run() {
            Integer message_id = bundle.getInt("message_id");
            JSONObject params = null;
            try {
                params = new JSONObject();
                params.put("id",message_id);
                params.put("invitation_status",status);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Conn.doJsonPost("/invitation/update_invitation_status",params);
        }

    }
    class SetWhole extends Thread{
        private String uri;
        SetWhole(String  uri){
            this.uri = uri;
        }

        @Override
        public void run() {
            Bitmap temp = MessageAdapter.getImage(Uri.parse(uri));
            bitmap.add(temp);
            Message msg = handler.obtainMessage();
            msg.obj=bitmap;
            handler.sendMessage(msg);
        }
    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            MessageContentAdapter mca = new MessageContentAdapter(bitmap,name,content);
            Log.i("InMessageAction",name.toString());
            listView.setAdapter(mca);
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        intent.setClass(MessageAction.this,BottomNavigation.class);
        Bundle bundle = new Bundle();
        bundle.putInt("fragment",3);
        intent.putExtra("data",bundle);
        startActivity(intent);
        finish();
    }
}
