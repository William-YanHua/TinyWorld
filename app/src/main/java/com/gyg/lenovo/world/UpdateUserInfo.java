package com.gyg.lenovo.world;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 24448 on 2018/6/20.
 */

public class UpdateUserInfo {
    static class MyTextWatcher implements TextWatcher {
        private String url = "/userinfo/update_userinfo";
        private String keys;
        private MyApp myApp;
        MyTextWatcher(String keys,MyApp myApp){
            this.keys = keys;
            this.myApp = myApp;
        }
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            Integer user_id = myApp.user_id;
            String query_condition =  "{\"id\":"+String.valueOf(user_id)+",\"Attributes\":" +
                    "{"+keys+":"+ editable.toString() +"}}";
            JSONObject jsonObject = null;
            try {
                Log.i("content changed",editable.toString());
                jsonObject = new JSONObject(query_condition);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.i("content changed",jsonObject.toString());
            new ModifyThread(url,jsonObject).start();
        }
    }
    static class ModifyThread extends Thread{
        JSONObject jsonObject;
        String url;
        ModifyThread(String url, JSONObject jsonObject){
            this.jsonObject = jsonObject;
            this.url = url;
        }
        @Override
        public void run() {
            Conn.doJsonPost(url,jsonObject);
        }
    }
}
