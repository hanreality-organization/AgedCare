package com.punuo.sip.user.service;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.punuo.sip.user.event.UserReplaceEvent;
import com.punuo.sip.user.request.BaseUserSipRequest;

import org.greenrobot.eventbus.EventBus;
import org.zoolu.sip.message.Message;

/**
 * Created by han.chen.
 * Date on 2021/1/14.
 **/
@Route(path = UserServicePath.PATH_SESSION_NOTIFY)
public class SipUserSessionNotifyService extends NormalUserRequestService<Object> {
    @Override
    protected String getBody() {
        return null;
    }

    @Override
    protected void onSuccess(Message msg, Object result) {
        EventBus.getDefault().post(new UserReplaceEvent());
    }

    @Override
    protected void onError(Exception e) {

    }

    @Override
    public void handleTimeOut(BaseUserSipRequest baseUserSipRequest) {

    }
}
