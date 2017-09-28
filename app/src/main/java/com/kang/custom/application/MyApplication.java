package com.kang.custom.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

/**
 * Created by kang on 2017/7/15.
 */

public class MyApplication extends Application {
    private static Context mContext;

    public MyApplication() {
    }

    public void onCreate() {
        super.onCreate();
        mContext = this.getApplicationContext();
    }

    public static Context getContext(){
        return mContext;
    }
}
