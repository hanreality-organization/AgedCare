package com.punuo.sip.dev.service;

import android.util.Log;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.punuo.sip.dev.SipDevManager;
import com.punuo.sip.dev.event.ReRegisterDevEvent;
import com.punuo.sip.dev.request.BaseDevSipRequest;
import com.punuo.sip.dev.request.SipDevRegisterSecondRequest;
import com.punuo.sip.user.model.NegotiateResponse;
import com.punuo.sys.sdk.util.HandlerExceptionUtils;

import org.greenrobot.eventbus.EventBus;
import org.zoolu.sip.message.Message;

/**
 * Created by han.chen.
 * Date on 2019-09-23.
 * 注册第一步Response
 **/
@Route(path = DevServicePath.PATH_REGISTER)
public class RegisterServiceDev extends NormalDevRequestService<NegotiateResponse> {
    @Override
    protected String getBody() {
        return null;
    }

    @Override
    protected void onSuccess(Message msg, NegotiateResponse result) {
        SipDevRegisterSecondRequest sipRegisterRequest = new SipDevRegisterSecondRequest(result);
        SipDevManager.getInstance().addRequest(sipRegisterRequest);
    }

    @Override
    protected void onError(Exception e) {
        HandlerExceptionUtils.handleException(e);
    }

    @Override
    public void handleTimeOut(BaseDevSipRequest baseSipRequest) {
        Log.d(TAG, "注册第一步超时");
        EventBus.getDefault().post(new ReRegisterDevEvent());
    }
}
