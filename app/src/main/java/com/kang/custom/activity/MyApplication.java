package com.kang.custom.activity;

import android.app.Application;
import android.content.Context;

/**
 * Created by kang on 2017/7/15.
 */

public class MyApplication extends Application {
    private static Context context;

    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
    public static Context getContext(){
        return context;
    }
}
