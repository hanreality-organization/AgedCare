package com.punuo.sip.user.service;

import android.util.Log;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.punuo.sip.user.SipUserManager;
import com.punuo.sip.user.event.ReRegisterUserEvent;
import com.punuo.sip.user.model.NegotiateResponse;
import com.punuo.sip.user.request.BaseUserSipRequest;
import com.punuo.sip.user.request.SipRegisterRequest;
import com.punuo.sys.sdk.account.AccountManager;
import com.punuo.sys.sdk.util.HandlerExceptionUtils;

import org.greenrobot.eventbus.EventBus;
import org.zoolu.sip.message.Message;

/**
 * Created by han.chen.
 * Date on 2019-09-23.
 * 注册第一步Response
 **/
@Route(path = UserServicePath.PATH_REGISTER)
public class RegisterServiceUser extends NormalUserRequestService<NegotiateResponse> {
    @Override
    protected String getBody() {
        return null;
    }

    @Override
    protected void onSuccess(Message msg, NegotiateResponse result) {
        AccountManager.setUserId(result.userId);
//        AccountManager.setUserIpPhoneNum(result.phoneNum);
        SipRegisterRequest sipRegisterRequest = new SipRegisterRequest(result);
        SipUserManager.getInstance().addRequest(sipRegisterRequest);
    }

    @Override
    protected void onError(Exception e) {
        HandlerExceptionUtils.handleException(e);
    }

    @Override
    public void handleTimeOut(BaseUserSipRequest baseUserSipRequest) {
        Log.d(TAG, "注册第一步超时");
        EventBus.getDefault().post(new ReRegisterUserEvent());
    }
}
