package com.kang.custom.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

/**
 * Created by kang on 2017/7/15.
 */

public class MyApplication extends Application {
    private static Activity mActivity;

    public MyApplication() {
    }

    public void onCreate() {
        super.onCreate();
    }

    public static void setContext(Activity activity){
        mActivity = activity;
    }

    public static Activity getContext(){
        return mActivity;
    }
}
