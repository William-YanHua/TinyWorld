package com.gyg.lenovo.world;

import android.app.Application;

public class MyApp extends Application {
    public Integer user_id;
    public Integer center_id;
    public Boolean isIn;

    @Override
    public void onCreate() {
        isIn = false;
        super.onCreate();
    }
}
