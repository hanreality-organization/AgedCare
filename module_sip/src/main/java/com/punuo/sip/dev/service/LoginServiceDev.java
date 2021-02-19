package com.punuo.sip.dev.service;

import android.util.Log;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.punuo.sip.dev.event.ReRegisterDevEvent;
import com.punuo.sip.dev.model.LoginResponseDev;
import com.punuo.sip.dev.request.BaseDevSipRequest;
import com.punuo.sip.dev.request.SipDevHeartBeatRequest;
import com.punuo.sip.dev.request.SipDevRegisterSecondRequest;
import com.punuo.sys.sdk.util.HandlerExceptionUtils;

import org.greenrobot.eventbus.EventBus;
import org.zoolu.sip.message.Message;

/**
 * Created by han.chen.
 * Date on 2019-09-23.
 * 注册第二步Response / 心跳包Response
 **/
@Route(path = DevServicePath.PATH_LOGIN)
public class LoginServiceDev extends NormalDevRequestService<LoginResponseDev> {
    @Override
    protected String getBody() {
        return null;
    }

    @Override
    protected void onSuccess(Message msg, LoginResponseDev result) {
        EventBus.getDefault().post(result);
    }

    @Override
    protected void onError(Exception e) {
        HandlerExceptionUtils.handleException(e);
    }

    @Override
    public void handleTimeOut(BaseDevSipRequest baseSipRequest) {
        if (baseSipRequest instanceof SipDevRegisterSecondRequest) {
            Log.d(TAG, "注册第二步超时");
        } else if (baseSipRequest instanceof SipDevHeartBeatRequest) {
            Log.d(TAG, "心跳包超时");
        }
        EventBus.getDefault().post(new ReRegisterDevEvent());
    }

}
