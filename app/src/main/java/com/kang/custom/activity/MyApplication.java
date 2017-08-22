package com.kang.custom.activity;

import android.app.Application;
import android.content.Context;

/**
 * Created by kang on 2017/7/15.
 */

public class MyApplication extends Application {
    private static Context myApp;

    public MyApplication() {
    }

    public void onCreate() {
        super.onCreate();
        myApp =  getContext();
    }

    public static Context getContext(){
        return myApp;
    }
}
