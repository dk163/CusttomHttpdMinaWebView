package com.kang.custom.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by kang on 2017/9/1.
 */

public class TimeUtil {
    public static String timeConvertToString(){
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");

        String dt = sdf.format(date);

        return dt;
    }
}
