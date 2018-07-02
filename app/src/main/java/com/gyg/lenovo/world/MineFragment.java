package com.gyg.lenovo.world;

import android.content.Context;
import android.content.Intent;
import android.content.pm.InstrumentationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 24448 on 2018/6/12.
 */

public class MineFragment extends Fragment {
    private InformationView change_password;
    private InformationView check_information;
    private Uri imageUri;
    private CircleImageView imageView;
    private InformationView logout;
    private InformationView check_update;
    private TextView signing;
    private Context context;
    private MyApp myApp;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.mine,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        myApp = (MyApp) getActivity().getApplication();
        imageView = (CircleImageView) view.findViewById(R.id.imageBox);
        signing = (TextView)view.findViewById(R.id.sign);
        check_information =(InformationView) view.findViewById(R.id.check_information);
        context = view.getContext();
        check_information.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.i("Message","In Mine");
                Intent intent = new Intent();
                Bundle data = new Bundle();
                intent.setClassName("com.gyg.lenovo.world",
                        "com.gyg.lenovo.world.myinformation");
                PackageManager packageManager = getActivity().getPackageManager();
                try{
                    if (intent.resolveActivity(packageManager) != null) {
                        startActivity(intent);
                    }
                }
                catch (Exception e){
                    Log.e("tag",e.getMessage().toString());
                }
            }
        });
        change_password = (InformationView) view.findViewById(R.id.modify_password);
        change_password.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClassName("com.gyg.lenovo.world",
                        "com.gyg.lenovo.world.ChangePassword");
                PackageManager packageManager = getActivity().getPackageManager();
                try{
                    if(intent.resolveActivity(packageManager)!=null){
                        startActivity(intent);
                    }
                }
                catch (Exception e){
                    Log.e("error in Mine",e.toString());
                }
            }
        });
        logout = (InformationView) view.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myApp.isIn = false;
                Intent intent = new Intent();
                intent.setClassName("com.gyg.lenovo.world",
                        "com.gyg.lenovo.world.LoginActivity");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                PackageManager pm = context.getPackageManager();
                if(intent.resolveActivity(pm)!=null){
                    startActivity(intent);
                }
            }
        });
        check_update = (InformationView)view.findViewById(R.id.update);
        check_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,"已经是最新版!",Toast.LENGTH_SHORT).show();
            }
        });
        new getUserInfoSignature().start();
        new getUserInfoImage().start();
    }
    class getUserInfoSignature extends Thread{
        @Override
        public void run() {
            Integer user_id = myApp.user_id;
            try {
                JSONObject jsonObject = new JSONObject("{\"id\":"+String.valueOf(user_id)+",\"Attributes\":\"signature\"}");
                JSONObject answer = Conn.doJsonPost("/userinfo/get_userinfo",jsonObject);
                Log.i("getSignature",answer.toString());
                JSONObject signature = answer.getJSONArray("result").getJSONObject(0);
                Message msg = handler.obtainMessage();
                msg.what = 0;
                msg.obj = signature;
                handler.sendMessage(msg);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
    class getUserInfoImage extends Thread{
        @Override
        public void run() {
            Integer user_id = myApp.user_id;
            try {
                JSONObject jsonObject = new JSONObject("{\"id\":"+String.valueOf(user_id)+",\"Attributes\":\"profile_photo\"}");
                JSONObject answer = Conn.doJsonPost("/userinfo/get_userinfo",jsonObject);
                JSONObject imageUri = answer.getJSONArray("result").getJSONObject(0);
                Bitmap image =MessageAdapter.getImage(Uri.parse(imageUri.getString("profile_photo")));
//                Bitmap image = MessageAdapter.getImage(Uri.parse("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=3481560223,3633833626&fm=27&gp=0.jpg"));
                Message msg = handler.obtainMessage();
                msg.obj = image;
                msg.what = 1;
                handler.sendMessage(msg);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 0:
                    JSONObject jsonObject = (JSONObject) msg.obj;
                    try {
                        Log.i("getSignatureInHandle",jsonObject.getString("signature"));
                        signing.setText(jsonObject.getString("signature"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 1:
                    try{
                        Bitmap bitmap = (Bitmap) msg.obj;
                        imageView.setImageBitmap(bitmap);
                    }
                    catch (Exception e){
                        Log.i("error message:",e.toString());
                    }
            }
        }
    };
}
