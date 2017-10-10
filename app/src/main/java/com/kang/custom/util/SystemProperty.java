package com.kang.custom.util;


/**
 * Created by kang on 2017/9/26.
 */

public class SystemProperty {

    static public String getProperty(String key, String defaultValue) {
        String returnValue = defaultValue;
        ReflectCase r = new ReflectCase("android.os.SystemProperties");
        Object obj2 = r.initObject(r.getPackageName(), null, null);

        Class[] methodClazzs = { String.class, String.class };
        Object[] methodValues = { key, defaultValue };
        returnValue = (String)r.initMethod(obj2, "get", methodClazzs, methodValues);

        LogUtils.e("returnValue: " + returnValue);

        return returnValue;
    }

    public static void setProperty(String key, String value) {
        ReflectCase r = new ReflectCase("android.os.SystemProperties");
        Object obj2 = r.initObject(r.getPackageName(), null, null);

        Class[] methodClazzs = { String.class, String.class };
        Object[] methodValues = { key, value };
        r.initMethod(obj2, "set", methodClazzs, methodValues);
    }
}
