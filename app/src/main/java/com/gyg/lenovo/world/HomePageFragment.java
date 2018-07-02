package com.gyg.lenovo.world;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.*;
import android.os.Handler;
import android.os.Message;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import java.util.Random;
/**
 * Created by 24448 on 2018/6/12.
 */

public class HomePageFragment extends Fragment {
    private initHandler init_handler= new initHandler();
    private Handler timerHandler = new Handler();
    private FrameLayout container = null; //动态添加内容的容器
    private Intent intent = null;
    private Random rand = new Random();
    private int Window_x;
    private int Window_y;
    //private int childCount;
    private float[] instance_x;
    private float[] instance_y;
    private MyApp myApp;
    private boolean isThread = false;
    int childCount = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_homepage, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        myApp = (MyApp) getActivity().getApplication();
        //窗口大小
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        Window_x = dm.widthPixels;
        Window_y = dm.heightPixels;

        //
        container = (FrameLayout )view.findViewById(R.id.viewObj);
        intent = new Intent();
        container.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int height = container.getMeasuredHeight();
        int width = container.getMeasuredWidth();
        //init page
        int userID = myApp.user_id;
        new Thread(new initThread(userID)).start();
        //move
        if(!isThread) {
            System.out.println("speedup++++++++++++++++++++++++");
            timerHandler.postDelayed(timer_thread, 100);
        }
    }

    // 简单消息提示框
    private void showDialog(String resultMsg){
        new AlertDialog.Builder(getContext())
                .setTitle("结果")
                .setMessage(resultMsg)
                .setPositiveButton("确定", null)
                .show();
    }


    /**
     * 显示image和user_nickname
     */
    private void init_image(int id, String nickname){
        //先把图片和文字放入这个layout中，再将layout放入上层布局
        FrameLayout image_text = new FrameLayout(getContext());
        //textview
        TextView text_view = new TextView(getContext());
        text_view.setText(nickname);
        text_view.setId(id);
        //imageview
        Bitmap bitmap =  BitmapFactory.decodeResource(getContext().getResources(),R.mipmap.star);
        ImageView image_view = new ImageView(getContext());
        image_view.setImageBitmap(bitmap);
        image_view.setId(id);
        //add
        image_view.setX(0);
        image_view.setY(50);
        text_view.setX(50);
        text_view.setY(0);
        FrameLayout.LayoutParams lp1 = new FrameLayout.LayoutParams(100, 100);
        FrameLayout.LayoutParams lp2 = new FrameLayout.LayoutParams(200, 50);
        image_text.addView(image_view,lp1);
        image_text.addView(text_view,lp2);
        //窗口大小

        //随机位置
        int x = rand.nextInt(Window_x-300)+100;
        int y = rand.nextInt(Window_y-600)+100;
        //add
        image_text.setX(x);
        image_text.setY(y);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(200, 150);
        container.addView(image_text,lp);
        final int temp_id = id;
        image_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showDialog(temp_id+"");
                Bundle b = new Bundle();
                b.putInt("id",temp_id);
                b.putBoolean("isFriend", true);
                intent.putExtra("data",b);
                intent.setClassName("com.gyg.lenovo.world",
                        "com.gyg.lenovo.world.PersonActivity");
                if(intent.resolveActivity(getContext().getPackageManager()) != null)
                    startActivity(intent);
            }
        });
    }

    /**
     * 初始化页面的时候使用的hanlder
     */
    class initHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.obj == null){
                // code != 200
                Log.d("err","null error");
                showDialog("error");
            }
            else{
                JSONArray arr = (JSONArray) msg.obj;
                try {
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject o = (JSONObject) arr.get(i);
                        int friendID = o.getInt("id");
                        String nickname = o.getString("nickname");
                        //System.out.println(i);
                        init_image(friendID,nickname);
                    }
                    /**
                     *
                     */
                    float speed = 5.0f;
                    childCount = container.getChildCount();
                    //System.out.println(childCount);
                    instance_x = new float[arr.length()];
                    instance_y = new float[arr.length()];
                    Random r = new Random();
                    int speed_add = r.nextInt(3);
                    for(int i=0;i<arr.length();i++){
                        instance_x[i] = speed+speed_add;
                        instance_y[i] = speed+speed_add;
                    }

                }
                catch (JSONException e){
                    Log.d("err","error");
                }
            }
        }
    }

    /**
     * 初始化页面时用到的线程，得到好友列表
     */
    public class initThread implements Runnable {
        private int userID;
        initThread(int ID){
            userID = ID;
        }
        @Override
        public void run() {
            //当前用户的id
            JSONObject json = new JSONObject();
            try{
                json.put("id",this.userID);
            } catch (JSONException e){
                Log.d("jsonErr","json error");
            }
            Message msg = new Message();
            JSONObject res = Conn.doJsonPost("/relationship/all", json);
            //System.out.println("_____++++___"+res.toString());
            Integer code = null;
            try {
                code = res.getInt("code");
                if (code != 200)
                    msg.obj = null;
                else {
                    JSONArray arr = res.getJSONArray("result");
                    msg.obj = arr;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            init_handler.sendMessage(msg);
        }
    }

    /**
     * 位移
     */
    Runnable timer_thread = new Runnable() {

        @Override
        public void run() {
            isThread = true;
            //遍历下面所有的子控件，判断是否是layout
            int flag = 0;
            for(int i = 0; i < childCount; i++) {
                //System.out.println(container.getChildAt(i).toString());
                if (container.getChildAt(i) instanceof FrameLayout) {
                    flag++;
                    float X = container.getChildAt(i).getX();
                    float Y = container.getChildAt(i).getY();
                    if (X+100 > Window_x || X < 0)
                        instance_x[i] = -instance_x[i];
                    if (Y+400 > Window_y || Y < 0)
                        instance_y[i] = -instance_y[i];

                    if (flag % 4 == 0) {
                        container.getChildAt(i).setX(X + instance_x[i]);
                        container.getChildAt(i).setY(Y + instance_y[i]);
                    } else if (flag % 4 == 1) {
                        container.getChildAt(i).setX(X - instance_x[i]);
                        container.getChildAt(i).setY(Y - instance_y[i]);
                    } else if (flag % 4 == 2) {
                        container.getChildAt(i).setY(Y + instance_y[i]);
                    } else {
                        container.getChildAt(i).setY(Y - instance_y[i]);
                    }
                }
            }
            timerHandler.postDelayed(this, 100);
        }
    };
}
