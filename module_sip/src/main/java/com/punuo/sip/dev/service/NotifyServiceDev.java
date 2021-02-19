package com.punuo.sip.dev.service;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.punuo.sip.dev.request.BaseDevSipRequest;
import com.punuo.sys.sdk.util.HandlerExceptionUtils;

import org.zoolu.sip.message.Message;

/**
 * Created by han.chen.
 * Date on 2019-09-23.
 **/
@Route(path = DevServicePath.PATH_NOTIFY)
public class NotifyServiceDev extends NormalDevRequestService<Object> {

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
    public void handleTimeOut(BaseDevSipRequest baseSipRequest) {

    }
}
