package com.punuo.sip.user.service;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.punuo.sip.user.SipUserManager;
import com.punuo.sip.user.model.IsMonitor;
import com.punuo.sip.user.request.BaseUserSipRequest;
import com.punuo.sip.user.request.SipQueryRequest;
import com.punuo.sys.sdk.account.AccountManager;

import org.zoolu.sip.message.Message;

/**
 * Created by han.chen.
 * Date on 2021/1/29.
 **/
@Route(path = UserServicePath.PATH_IS_MONITOR)
public class IsMonitorServiceUser extends NormalUserRequestService<IsMonitor> {
    @Override
    protected String getBody() {
        return null;
    }

    @Override
    protected void onSuccess(Message msg, IsMonitor result) {
        if (TextUtils.equals("200", result.code)) {
            Log.v(TAG, "is monitor success");
            SipQueryRequest request = new SipQueryRequest(AccountManager.getTargetDevId());
            SipUserManager.getInstance().addRequest(request);
        } else {
            Log.v(TAG, "is monitor failed");
        }
    }

    @Override
    protected void onError(Exception e) {

    }

    @Override
    public void handleTimeOut(BaseUserSipRequest baseUserSipRequest) {

    }
}
