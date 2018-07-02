package com.gyg.lenovo.world;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

import static android.support.v7.widget.RecyclerView.*;

/**
 * Created by 24448 on 2018/6/16.
 */

public class SearchAdapter extends BaseAdapter{
    //上下文
    private Context context;
    //数据项
    private List<String> names;
    private List<Uri> images;
    private List<String> telephone;
    private List<String> id;
    ViewHolder viewHolder = null;
    public  SearchAdapter(List<Uri> images, List<String> names, List<String> telephone, List<String> id){
        this.images = images;
        this.names = names;
        this.telephone = telephone;
        this.id = id;
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
        final int innerI = i;
        if(context == null)
            context = viewGroup.getContext();
        if(view == null){
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.search_list,null);
            viewHolder = new ViewHolder();
            viewHolder.head_image = (ImageView) view.findViewById(R.id.people_head_view);
            viewHolder.name = (TextView)view.findViewById(R.id.name);
            viewHolder.telephone = (TextView)view.findViewById(R.id.telephone);
            view.setTag(viewHolder);
        }
        //获取viewHolder实例
        viewHolder = (ViewHolder)view.getTag();
        //设置数据
//        new setImageThread(images.get(i)).start();
        viewHolder.name.setText(names.get(i));
        viewHolder.head_image.setImageDrawable(view.getResources().getDrawable(R.drawable.avatar));
        viewHolder.telephone.setText(telephone.get(i));
        return view;
    }
    static class ViewHolder{
        ImageView head_image;
        TextView name;
        TextView telephone;
    }
}
