package com.punuo.sys.app.agedcare.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.punuo.sip.user.SipUserManager;
import com.punuo.sip.user.request.SipUserAlarmRequest;
import com.punuo.sys.sdk.account.AccountManager;
import com.punuo.sys.sdk.model.BindUser;

import java.util.List;

public class SOSReceiver extends BroadcastReceiver {
    private static final String TAG = "SOSReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            boolean down = extras.getBoolean("down");
            if (down) {
                List<BindUser> bindUsers = AccountManager.getBindUsers();
                if (bindUsers != null && !bindUsers.isEmpty()) {
                    for (BindUser bindUser : bindUsers) {
                        SipUserAlarmRequest request = new SipUserAlarmRequest(bindUser.userid);
                        SipUserManager.getInstance().addRequest(request);
                    }
                }
            }
        }
    }
}
