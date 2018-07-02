package com.gyg.lenovo.world;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;

import static android.support.v7.widget.RecyclerView.*;

/**
 * Created by 24448 on 2018/6/16.
 */

public class InviteAdapter extends BaseAdapter implements View.OnClickListener {
    //上下文
    private Context context;
    //数据项
    private List<String> names;
    private List<Drawable> images;
    private List<String> telephone;
    private MyApp myApp;
    ViewHolder viewHolder = null;
    public  InviteAdapter(List<Drawable>images, List<String> names, List<String>telephone, MyApp myApp){
        this.images = images;
        this.names = names;
        this.telephone = telephone;
        this.myApp = myApp;
    }
    @Override
    public void onClick(View view) {
    }

    @Override
    public int getCount() {
        return names==null?0:names.size();
    }

    @Override
    public Object getItem(int i) {
        return names.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if(context == null)
            context = viewGroup.getContext();
        if(view == null){
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.invite_list,null);
            viewHolder = new ViewHolder();
            viewHolder.head_image = (ImageView) view.findViewById(R.id.people_head_view);
            viewHolder.name = (TextView)view.findViewById(R.id.name);
            viewHolder.telephone = (TextView)view.findViewById(R.id.telephone);
            viewHolder.invite = (Button)view.findViewById(R.id.invite);
            view.setTag(viewHolder);
        }
        //获取viewHolder实例
        viewHolder = (ViewHolder)view.getTag();
        //设置数据
        viewHolder.head_image.setImageDrawable(images.get(i));
        viewHolder.name.setText(names.get(i));
        viewHolder.telephone.setText(telephone.get(i));
        viewHolder.invite.setOnClickListener(new MyOnClickListener(i));
        return view;
    }
    class MyOnClickListener implements View.OnClickListener{
        private Integer i;
        MyOnClickListener(Integer i){
            this.i = i;
        }
        @Override
        public void onClick(View view) {
            String from,to;
            try{
                from = String.valueOf(myApp.user_id);
                to = telephone.get(i);
            }
            catch (Exception e){
                from = "NULL";
                to = "18222723864";
            }
            sendInvitation(from,to);
        }
    }
    public void sendInvitation(String from, String to){
        String params= "{\"inviterName\":"+from+",\"targetNumber\":"+to+"}";
        new InviteThread(params).start();
    }
    static class ViewHolder{
        ImageView head_image;
        TextView name;
        TextView telephone;
        Button invite;
    }
    public class InviteThread extends Thread {

        //继承Thread类，并改写其run方法
        private final static String TAG = "My Thread ===> ";
        private String param;
        InviteThread(String param){
            this.param = param;
        }
        public void run(){
            try {
                Log.i("beginSend","send");
                JSONObject postParams = new JSONObject(param);
                JSONObject answer = Conn.doJsonPost("/invitation/newInvitation",postParams);
                Log.i("afterSend","send");
                System.out.println("已经请求");
                Log.i("msg",answer.getString("msg"));
                Message msg = handler.obtainMessage();
                msg.obj = answer;
                handler.sendMessage(msg);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            catch (Exception e){
                Log.i("error",e.toString());
            }
        }
    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            JSONObject jsonObject = (JSONObject) msg.obj;
            try {
                if(jsonObject.getInt("code")==200)
                    Toast.makeText(context,"邀请成功",Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(context,"邀请失败",Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
}
