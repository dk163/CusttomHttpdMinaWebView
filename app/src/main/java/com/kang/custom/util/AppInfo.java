package com.kang.custom.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by kang on 2017/8/31.
 */

public class AppInfo {
    //版本名
    public static String getPackageName(Context context) {
        return getPackageInfo(context).packageName;
    }

    //版本号
    public static String getVersionCode(Context context) {
        return getPackageInfo(context).versionName;
    }

    private static PackageInfo getPackageInfo(Context context) {
        PackageInfo pi = null;

        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);

            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pi;
    }
}
