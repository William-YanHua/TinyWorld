package com.gyg.lenovo.world;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 24448 on 2018/6/15.
 */

public class InvitePageFragment extends Fragment {
    //上下文对象
    private Context context;
    private MyApp myApp;
    //联系人提供者的uri
    private Uri phoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
    // 号码
    public final static String NUM = ContactsContract.CommonDataKinds.Phone.NUMBER;
    // 联系人姓名
    public final static String NAME = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;
    private ListView listView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_invite,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = view.getContext();
        myApp = (MyApp) getActivity().getApplication();
        listView = (ListView) view.findViewById(R.id.invite);
        List<String> names = new ArrayList<>();
        List<Drawable> imgId = new ArrayList<>();
        List<String> telephone = new ArrayList<>();
        NameAndNumber contacts = getContact();
//        Uri uri = Uri.parse("https://image.baidu.com/search/detail?ct=503316480&z=&tn=baiduimagedetail&ipn=d&word=meili&step_word=&ie=utf-8&in=&cl=2&lm=-1&st=-1&cs=2880264292,4049010165&os=3865414986,1231028003&simid=0,0&pn=1&rn=1&di=196288720540&ln=1984&fr=&fmq=1529081536461_R&fm=result&ic=0&s=undefined&se=&sme=&tab=0&width=&height=&face=undefined&is=0,0&istype=2&ist=&jit=&bdtype=0&spn=0&pi=0&gsm=0&objurl=http%3A%2F%2Fphoto15.zastatic.com%2Fimages%2Fphoto%2F11558%2F46230976%2F1448187643230_2.jpg&rpstart=0&rpnum=0&adpicid=0");
        names = contacts.name;
        telephone = contacts.phoneNum;
        for (int i = 0; i < names.size(); i++) {
            imgId.add(view.getResources().getDrawable(R.drawable.contacts));
        }
        InviteAdapter iv = new InviteAdapter(imgId, names, telephone, myApp);
        listView.setAdapter(iv);
        listView.setOnItemClickListener(new ListView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Log.i("Information", "Click invite");
                Toast.makeText(getActivity(), "我是item点击事件 i = " + i + "l = " + l, Toast.LENGTH_SHORT).show();
            }
        });
//        SimpleAdapter mSimpleAdapter = new SimpleAdapter(this,listItems,R.layout.list_view_temp,new String[]{"img","text"},new int[]{R.id.image_view,R.id.text_view});
//        listView.setAdapter(mSimpleAdapter);
    }
    public NameAndNumber getContact(){
        NameAndNumber contact = new NameAndNumber();
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(phoneUri,new String[]{NUM,NAME},null,null,null);
        while (cursor.moveToNext()){
            contact.name.add(cursor.getString(cursor.getColumnIndex(NAME)));
            contact.phoneNum.add(cursor.getString(cursor.getColumnIndex(NUM)));
        }
        return contact;
    }
    public class NameAndNumber{
        public List<String>name;
        public List<String>phoneNum;
        public NameAndNumber(){
            name = new ArrayList<>();
            phoneNum = new ArrayList<>();
        }
    }
}
