package com.gyg.lenovo.world;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

/**
 * Created by 24448 on 2018/6/16.
 */

public class MessageContentAdapter extends BaseAdapter {
    private List<Bitmap> head_view;
    private List<String> messages;
    private List<String> name;
    private Context context;
    ViewHolder viewHolder = null;
    public MessageContentAdapter(List<Bitmap> bitmap,List<String>name, List<String> messages){
        this.head_view = bitmap;
        this.messages = messages;
        this.name = name;
    }
    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int i) {
        return messages.get(i);
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
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_content_list,null);
            viewHolder = new ViewHolder();
            viewHolder.head_view = (ImageView)view.findViewById(R.id.contact_head_view);
            viewHolder.name = (TextView)view.findViewById(R.id.sender);
            viewHolder.messages = (TextView) view.findViewById(R.id.content);
            view.setTag(viewHolder);
        }
        //获取viewHolder实例
        viewHolder = (ViewHolder)view.getTag();
        //设置数据
        try{
            viewHolder.head_view.setImageBitmap(head_view.get(i));
            viewHolder.name.setText(name.get(i));
            viewHolder.messages.setText(messages.get(i));
            Log.i("message",name.toString());
        }
        catch (Exception e){
            Log.e("error in MCA",e.toString());
        }
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
                    matrix.setScale((float)50/ bitmap.getWidth(), (float)50/ bitmap.getHeight());
                    bitmap = Bitmap.createBitmap( bitmap, 0, 0,  bitmap.getWidth(), bitmap.getHeight(), matrix, false);
                    viewHolder.head_view.setImageDrawable(new BitmapDrawable(bitmap)); //设置imageView显示的图片
                    break;
                case 0:
                    Toast.makeText(context, "图片加载失败", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };
    static class ViewHolder{
        ImageView head_view;
        TextView messages;
        TextView name;
    }

}
