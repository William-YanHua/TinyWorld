package com.gyg.lenovo.world;

import android.app.Instrumentation;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class ChangePassword extends AppCompatActivity {
    private TextInputEditText old_password,new_password,re_password;
    Button save,cancel;
    private Context context;
    private MyApp myApp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        myApp = (MyApp) getApplication();
        context = getBaseContext();
        old_password = (TextInputEditText) findViewById(R.id.old_password);
        new_password = (TextInputEditText) findViewById(R.id.new_password);
        re_password = (TextInputEditText) findViewById(R.id.re_password);
        save = (Button) findViewById(R.id.save);
        cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread() {
                    public void run() {
                        try {
                            Instrumentation instrument = new Instrumentation();
                            instrument.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
                        } catch (Exception e) {
                            Log.e("error in ChangePassword", e.toString());
                        }
                    }
                }.start();
            }
        });
        save = (Button) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String old_pass = old_password.getText().toString();
                final String new_pass = new_password.getText().toString();
                final String re_pass = re_password.getText().toString();
                if(checkTwoTimesInput(new_pass,re_pass)){
                    new SavePassword(old_pass,new_pass).start();
                }
                else{
                    Toast.makeText(ChangePassword.this,"两次密码不一致",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public boolean checkTwoTimesInput(String string1,String string2){
        return string1.equals(string2);
    }
    class SavePassword extends Thread{
        private String pass;
        private String old_pass;
        public  SavePassword(String old_pass,String Pass){
            this.old_pass = old_pass;
            this.pass = Pass;
        }
        @Override
        public void run() {
            Integer user_id = myApp.user_id;
            try {
                JSONObject params = new JSONObject("{\"id\":"+String.valueOf(user_id)+",\"oldpassword\":"+old_pass+",\"newpassword\":"+pass+"}");
                JSONObject answer =  Conn.doJsonPost("/users/modifyPassword",params);
                Message msg = handle.obtainMessage();
                msg.obj = answer;
                handle.sendMessage(msg);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    Handler handle = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            JSONObject answer =(JSONObject) msg.obj;
            try {
                if(answer.getInt("code") == 200)
                    Toast.makeText(context, "修改成功",Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(context, "修改失败",Toast.LENGTH_SHORT).show();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
}
