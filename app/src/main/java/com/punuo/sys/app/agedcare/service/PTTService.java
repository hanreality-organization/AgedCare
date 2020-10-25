package com.punuo.sys.app.agedcare.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.punuo.sys.app.agedcare.receiver.MyReceiver;
import com.punuo.sys.app.agedcare.receiver.SosReceiver;


/**
 * Created by acer on 2016/7/1.
 */
public class PTTService extends Service {
    private static final String TAG = "PTTService";
    private String GLOBAL_BUTTON = "android.intent.action.GLOBAL_BUTTON";
    private MyReceiver myReceiver;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerPTTReceiver(this);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)                         {
        return START_STICKY;
    }

    private void registerPTTReceiver(Context context) {
        Log.d(TAG, "registerPTTReceiver");
        myReceiver = new MyReceiver();
        IntentFilter P91 = new IntentFilter();
        P91.addAction(GLOBAL_BUTTON);
        context.registerReceiver(myReceiver, P91);
    }



    private void unregisterPTTReceiver(Context context) {
        Log.d(TAG, "unregisterPTTReceiver");
        if (myReceiver != null) {
            context.unregisterReceiver(myReceiver);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterPTTReceiver(this);
    }
}
