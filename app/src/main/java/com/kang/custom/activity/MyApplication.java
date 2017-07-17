package com.kang.custom.activity;

import android.app.Application;

/**
 * Created by kang on 2017/7/15.
 */

public class MyApplication extends Application {
    private static MyApplication myApp;

    public MyApplication() {
    }

    public void onCreate() {
        super.onCreate();
    }

    public MyApplication getInstance(){
        if(myApp == null){
            myApp = new MyApplication();
        }
        return myApp;
    }
}
