package com.punuo.sip.user.service;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.punuo.sip.user.request.BaseUserSipRequest;
import com.punuo.sys.sdk.util.HandlerExceptionUtils;

import org.zoolu.sip.message.Message;

/**
 * Created by han.chen.
 * Date on 2019-09-23.
 **/
@Route(path = UserServicePath.PATH_NOTIFY)
public class NotifyServiceUser extends NormalUserRequestService<Object> {

    @Override
    protected String getBody() {
        return null;
    }

    @Override
    protected void onSuccess(Message msg, Object result) {

    }

    @Override
    protected void onError(Exception e) {
        HandlerExceptionUtils.handleException(e);
    }

    @Override
    public void handleTimeOut(BaseUserSipRequest baseUserSipRequest) {

    }
}
