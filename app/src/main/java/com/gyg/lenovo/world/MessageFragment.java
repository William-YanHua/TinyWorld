package com.gyg.lenovo.world;

import android.app.LauncherActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 24448 on 2018/6/12.
 */

public class MessageFragment extends Fragment {
    private ListView listView;
    private MyApp myApp;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_message,container,false);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        myApp = (MyApp) getActivity().getApplication();
        listView = (ListView)view.findViewById(R.id.message);
        try {
            getNameAndMessage();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void getNameAndMessage() throws JSONException {
        String from = String.valueOf(myApp.user_id);
        Log.i("InMessage","yes");
        if(from ==""){
            from = "22";
        }
        new NAMThread("{\"id\":"+from+"}").start();
    }
    public class NAMThread extends Thread {

        //继承Thread类，并改写其run方法
        private JSONObject param;
        NAMThread(String param) throws JSONException {
            this.param = new JSONObject(param);
        }
        public void run(){
            try {
                JSONObject answer = Conn.doJsonPost("/message/receive_list",param);
                try {
                    JSONArray array = answer.getJSONArray("result");
                    JSONArray answerArray = new JSONArray();
                    Log.i("array length",String.valueOf(array.length()));
                    for(int i = 0; i < array.length(); i++){
                        if(array.getJSONObject(i).getString("message_status").equals("read") != true) {
                            answerArray.put(array.getJSONObject(i));
                            Log.i("jsonObject message",array.getJSONObject(i).toString());
                        }
                    }
                    Log.i("unread message",String.valueOf(answerArray.length()));
                    Message msg = handler.obtainMessage();
                    msg.obj = answerArray;
                    msg.what = 0;
                    Log.i("InMessageThread", answerArray.toString());
                    handler.sendMessage(msg);
                }catch (Exception e){
                    JSONObject jsonObject = answer.getJSONObject("result");
                    Message msg = handler.obtainMessage();
                    msg.obj = jsonObject;
                    msg.what = 1;
                    handler.sendMessage(msg);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg){
        Log.i("InMessageHandle","yes");
        List<Uri> imgId = new ArrayList<>();
        List<String> names = new ArrayList<>();
        List<String> signings = new ArrayList<>();
        List<String> times = new ArrayList<>();
        if(msg.what == 0){
            JSONArray data = (JSONArray) msg.obj;
            JSONObject jObject = null;
            for(int i = 0; i < data.length(); i++){
                try {
                    jObject = data.getJSONObject(i);
                    imgId.add(Uri.parse(jObject.getString("profile_photo")));
                    names.add(jObject.getString("nickname"));
                    String content = jObject.getString("content").length() > 10 ?
                            jObject.getString("content").substring(0,10)+"..." :
                            jObject.getString("content") + "...";
                    signings.add(content);
                    String time = formatTime(jObject.getString("time"));
                    times.add(time);
                } catch (JSONException e) {
                    try {
                        jObject = data.getJSONObject(i);
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                    e.printStackTrace();
                }
            }
            Log.i("imageid",imgId.toString());
            MessageAdapter mv = new MessageAdapter(imgId,names,signings,times);
            listView.setAdapter(mv);
            clickListener(data);
        }
        else{
            try {
                JSONObject jsonObject =(JSONObject) msg.obj;
                imgId.add(Uri.parse(jsonObject.getString("profile_photo")));
                names.add(jsonObject.getString("nickname"));
                String content = jsonObject.getString("content").length() > 10 ?
                        jsonObject.getString("content").substring(0,10)+"..." :
                        jsonObject.getString("content") + "...";
                signings.add(content);
                String time = formatTime(jsonObject.getString("time"));
                times.add(time);
                MessageAdapter mv = new MessageAdapter(imgId,names,signings,times);
                listView.setAdapter(mv);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        }
    };
    public void clickListener(final JSONArray jsonArray){
        listView.setOnItemClickListener(new ListView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("Information","Click invite");
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                try {
                    JSONObject  jsonObject = jsonArray.getJSONObject(i);
                    Log.i("update_params",jsonArray.toString());
                    Integer message_id = jsonObject.getInt("id");
                    String message_status = "read";
                    JSONObject update_params = new JSONObject("{\"id\":"+message_id.toString()+",\"message_status\":"+message_status+"}");
                    Log.i("update_params",update_params.toString());
                    new changeStatusThread(update_params).start();
                    bundle.putString("sender", jsonObject.getString("sender"));
                    bundle.putString("nickname",jsonObject.getString("nickname"));
                    bundle.putString("content",jsonObject.getString("content"));
                    bundle.putString("image",jsonObject.getString("profile_photo"));
//                    bundle.putString("image","https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1529252792389&di=bcff415d973927fdf5a079c98a50b383&imgtype=0&src=http%3A%2F%2Fimgsrc.baidu.com%2Fimgad%2Fpic%2Fitem%2Fb3b7d0a20cf431ad73c8c61a4136acaf2edd98ff.jpg");
                    bundle.putInt("message_id",message_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.i("InMessageFragment",bundle.toString());
                intent.putExtra("data",bundle);
                intent.setClassName("com.gyg.lenovo.world",
                        "com.gyg.lenovo.world.MessageAction");
                try{
                    PackageManager packageManager = getActivity().getPackageManager();
                    if (intent.resolveActivity(packageManager) != null) {
                        startActivity(intent);
                    }
                }
                catch (Exception e){
                    Log.e("tag",e.getMessage().toString());
                }
            }
        });
    }
    public JSONArray parseJSON(String jsonStr) throws JSONException{
        JSONArray arr = new JSONArray(jsonStr);
        return  arr;
    }
    public class NameAndMessage{
        List<Uri> uri;
        List<String>name;
        List<String>messages;
        List<String>digest;
        List<String>time;
        public NameAndMessage() {
            uri = new ArrayList<>();
            name = new ArrayList<>();
            messages = new ArrayList<>();
            digest = new ArrayList<>();
            time = new ArrayList<>();
        }
    }
    public String formatTime(String time){
        String tempTime = time.substring(5,19);
        String []times = tempTime.split("T");
        tempTime = times[0]+"     "+times[1];
        return tempTime;
    }
    class changeStatusThread extends Thread{
        private JSONObject update_params;
        changeStatusThread(JSONObject update_params){
            this.update_params = update_params;
        }
        @Override
        public void run() {
            Conn.doJsonPost("/message/update_message_status",update_params);
        }
    }
}
