package com.punuo.sip.user.service;

import android.util.Log;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.punuo.sip.user.event.ReRegisterUserEvent;
import com.punuo.sip.user.model.LoginResponseUser;
import com.punuo.sip.user.request.BaseUserSipRequest;
import com.punuo.sip.user.request.SipRegisterRequest;
import com.punuo.sip.user.request.SipUserHeartBeatRequest;
import com.punuo.sys.sdk.util.HandlerExceptionUtils;

import org.greenrobot.eventbus.EventBus;
import org.zoolu.sip.message.Message;

/**
 * Created by han.chen.
 * Date on 2019-09-23.
 * 注册第二步Response / 心跳包Response
 **/
@Route(path = UserServicePath.PATH_LOGIN)
public class LoginServiceUser extends NormalUserRequestService<LoginResponseUser> {
    @Override
    protected String getBody() {
        return null;
    }

    @Override
    protected void onSuccess(Message msg, LoginResponseUser result) {
        EventBus.getDefault().post(result);
    }

    @Override
    protected void onError(Exception e) {
        HandlerExceptionUtils.handleException(e);
    }

    @Override
    public void handleTimeOut(BaseUserSipRequest baseUserSipRequest) {
        if (baseUserSipRequest instanceof SipRegisterRequest) {
            Log.d(TAG, "注册第二步超时");
        } else if (baseUserSipRequest instanceof SipUserHeartBeatRequest) {
            Log.d(TAG, "心跳包超时");
        }
        EventBus.getDefault().post(new ReRegisterUserEvent());
    }
}
