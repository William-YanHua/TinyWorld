package com.gyg.lenovo.world;

import android.content.Context;
import android.content.res.TypedArray;
import android.media.Image;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.jar.Attributes;

/**
 * Created by 24448 on 2018/5/22.
 */

public class InformationView extends LinearLayout {
    private TextView description = null;
    private ImageView leftLogo = null;
    private ImageView rightLogo = null;

    public InformationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.infomationbar,this,true);
        description = (TextView)findViewById(R.id.information_main_body);
        leftLogo = (ImageView)findViewById(R.id.information_left_logo);
        rightLogo = (ImageView)findViewById(R.id.information_right_logo);
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.InformationView);
        if(attrs!=null){
            //如果属性值不为空，则进行设置
            int logoDrawable = attributes.getResourceId(R.styleable.InformationView_left_logo,R.drawable.password);
            leftLogo.setBackgroundResource(logoDrawable);
            leftLogo.setScaleType(ImageView.ScaleType.FIT_CENTER);
            String text = attributes.getString(R.styleable.InformationView_body_text);
            description.setText(text);
            int rightLogoDrawable = attributes.getResourceId(R.styleable.InformationView_right_logo,-1);
            if(rightLogoDrawable != -1){
                rightLogo.setBackgroundResource(rightLogoDrawable);
            }
        }
        attributes.recycle();
    }

    public InformationView(Context context) {
        super(context);
    }
    public void setText(String str){
        description.setText(str);
    }

}
