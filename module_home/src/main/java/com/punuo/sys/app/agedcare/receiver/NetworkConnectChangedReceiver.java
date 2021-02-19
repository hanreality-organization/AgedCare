package com.punuo.sys.app.agedcare.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.punuo.sys.app.agedcare.tools.LogUtil;
import com.punuo.sys.sdk.account.AccountManager;

import org.greenrobot.eventbus.EventBus;

/**
 * 网络变化监听
 */
public class NetworkConnectChangedReceiver extends BroadcastReceiver {
    private static final String TAG = "NetworkConnectChangedReceiver";

    private String getConnectionType(int type) {
        String connType = "";
        if (type == ConnectivityManager.TYPE_MOBILE) {
            connType = "数据流量";
        } else if (type == ConnectivityManager.TYPE_WIFI) {
            connType = "WIFI网络";
        }
        return connType;
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        /* 监听网络连接，包括wifi和移动数据的打开和关闭,以及连接上可用的连接都会接到监听*/
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            /*获取联网状态的NetworkInfo对象*/
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if (info != null) {
                /*如果当前的网络连接成功并且网络连接可用*/
                if (NetworkInfo.State.CONNECTED == info.getState() && info.isAvailable()) {
                    if (info.getType() == ConnectivityManager.TYPE_WIFI
                            || info.getType() == ConnectivityManager.TYPE_MOBILE) {
                        LogUtil.d(TAG, getConnectionType(info.getType()) + "连上");
                        if (AccountManager.isLogin()) {
                            EventBus.getDefault().post(info);
                        }
                    }
                } else {
                    LogUtil.d(TAG, getConnectionType(info.getType()) + "断开");
                }
            }
        }
    }
}