package com.gyg.lenovo.world;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URL;
import java.security.DigestException;
import java.util.List;

/**
 * Created by 24448 on 2018/6/16.
 */

public class MessageAdapter extends BaseAdapter {
    //上下文
    private Context context;
    //数据项
    private List<Uri> images;
    private List<String> names;
    private List<String> digest;
    private List<String> times;
    ViewHolder viewHolder = null;
    public  MessageAdapter(List<Uri>images,List<String> names, List<String> digest, List<String> times){
        this.images = images;
        this.names = names;
        this.digest = digest;
        this.times = times;
    }
    @Override
    public int getCount() {
        return names.size();
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
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_list,null);
            viewHolder = new ViewHolder();
            viewHolder.head_image = (ImageView) view.findViewById(R.id.people_head_view);
            viewHolder.name = (TextView)view.findViewById(R.id.name);
            viewHolder.digest = (TextView)view.findViewById(R.id.digest);
            viewHolder.times = (TextView) view.findViewById(R.id.time);
            view.setTag(viewHolder);
        }
        //获取viewHolder实例
        viewHolder = (ViewHolder)view.getTag();
        //设置数据

//        new setImageThread(images.get(i)).start();
        viewHolder.name.setText(names.get(i));
        viewHolder.head_image.setImageDrawable(view.getResources().getDrawable(R.drawable.avatar));
        viewHolder.digest.setText(digest.get(i));
        viewHolder.times.setText(times.get(i));
        return view;
    }
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Bitmap bitmap = (Bitmap) msg.obj;
                    Matrix matrix = new Matrix();
                    matrix.setScale((float)90/ bitmap.getWidth(), (float)90/ bitmap.getHeight());
                    bitmap = Bitmap.createBitmap( bitmap, 0, 0,  bitmap.getWidth(), bitmap.getHeight(), matrix, false);
                    viewHolder.head_image.setImageBitmap(bitmap); //设置imageView显示的图片
                    break;
                case 0:
                    Toast.makeText(context, "图片加载失败", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };
    private class setImageThread extends Thread{
        private Bitmap bitmap;
        private Uri uri;
        public setImageThread(Uri uri){
            this.uri = uri;
        }
        @Override
        public void run() {
            try{
                bitmap = getImage(uri);
            }
            catch (Exception e){
                bitmap = null;
            }
            if(bitmap != null){
                Message msg = new Message();
                msg.what = 1;
                msg.obj = bitmap;
                handler.sendMessage(msg);
            }
            else{
                Message msg = new Message();
                msg.what = 0;
                handler.sendMessage(msg);
            }
        }
    }
    public static Bitmap getImage(Uri uri){
        HttpURLConnection conn = null;
        try{
            Log.i("connected to get bitmap",uri.toString());
            URL mURL = new URL("http://47.95.243.80:3000"+uri.toString());
//            URL mURL = new URL(uri.toString());
            Log.i("connected to get bitmap","yes");
            conn = (HttpURLConnection) mURL.openConnection();
            conn.setRequestMethod("GET"); //设置请求方法
            conn.setConnectTimeout(10000); //设置连接服务器超时时间
            conn.setReadTimeout(5000);  //设置读取数据超时时间
            try{
                conn.connect(); //开始连接
            }
            catch (Exception e){
                Log.i("connected error",e.toString());
            }
            int responseCode = conn.getResponseCode(); //得到服务器的响应码
            Log.i("connected to get bitmap","yes");
            if (responseCode == 200) {
                //访问成功
                Log.i("connected to get bitmap","yes");
                InputStream is = conn.getInputStream(); //获得服务器返回的流数据
                Log.i("connected to get bitmap","yes");
                Bitmap bitmap = BitmapFactory.decodeStream(is); //根据流数据 创建一个bitmap对象
                Log.i("connected to get bitmap","yes");
                return bitmap;
            } else {
                //访问失败
                Log.d("lyf--", "访问失败===responseCode：" + responseCode);
            }
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    static class ViewHolder{
        ImageView head_image;
        TextView name;
        TextView digest;
        TextView times;
    }
}
