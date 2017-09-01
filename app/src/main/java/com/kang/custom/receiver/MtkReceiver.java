package com.kang.custom.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.kang.custom.util.LogUtils;
import com.kang.custom.util.StringUtil;

/**
 * Created by kang on 2017/8/31.
 */

public class MtkReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        /**
         * 1) "affected_log_type": 指明这次log state chage event是哪些log type改变了。这是一个int型值，
           主要利用低3bit进行记录：mobile log(bit2), modem log(bit1), network log(bit0)。假如该值为0x7，那么三种log state都改变了。
           2)"log_new_state": 指明对应log type新的状态。该值同样以最低3bit进行记录：mobile log(bit2), modem log(bit1), network log(bit0)
           如果对应的bit为0，代表对应的log state为off；如果对应的bit为1，代表对应的log state为on
         */

        String action = intent.getAction();
        if(action.equals("com.mediatek.mtklogger.intent.action.LOG_STATE_CHANGED")){
            int log_type = intent.getIntExtra("affected_log_type", -1);
            int new_state = intent.getIntExtra("log_new_state", -1);

            byte[] type = StringUtil.intTo4byte(log_type);
            for(byte t:type){
                LogUtils.i("mtkReceiver type: " + t);
            }

            byte[] state = StringUtil.intTo4byte(new_state);
            for(byte st:state){
                LogUtils.i("mtkReceiver state: " + st);
            }
        }
    }
}
