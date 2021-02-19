package com.punuo.sip.dev.service;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.punuo.sip.dev.event.StopVideoEvent;
import com.punuo.sip.dev.request.BaseDevSipRequest;
import com.punuo.sip.user.SipUserManager;
import com.punuo.sip.user.request.SipByeRequest;
import com.punuo.sys.sdk.account.AccountManager;

import org.greenrobot.eventbus.EventBus;
import org.zoolu.sip.message.Message;

/**
 * Created by han.chen.
 * Date on 2021/2/17.
 **/
@Route(path = DevServicePath.PATH_RECVADDR)
public class RecvaddrService extends NormalDevRequestService<Object> {
    @Override
    protected String getBody() {
        return null;
    }

    @Override
    protected void onSuccess(Message msg, Object result) {
        onResponse(msg);
        SipByeRequest request = new SipByeRequest(AccountManager.getTargetDevId());
        SipUserManager.getInstance().addRequest(request);
        EventBus.getDefault().post(new StopVideoEvent());
    }

    @Override
    protected void onError(Exception e) {

    }

    @Override
    public void handleTimeOut(BaseDevSipRequest baseSipRequest) {

    }
}
