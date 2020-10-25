package com.punuo.sys.app.agedcare.receiver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.punuo.sys.app.agedcare.ui.MessageEvent;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Method;

public class PhoneListener extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.e("PhoneListener",action);

            TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Service.TELEPHONY_SERVICE);

            switch (tm.getCallState()) {
                case TelephonyManager.CALL_STATE_RINGING:

                    Log.e("PhoneListener","来电");
                    EventBus.getDefault().post(new MessageEvent("callstart"));

                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Log.e("PhoneListener","接听");
                    EventBus.getDefault().post(new MessageEvent("calling"));
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    Log.e("PhoneListener","正常");
                    EventBus.getDefault().post(new MessageEvent("callend"));
                    break;
            }
        }

}