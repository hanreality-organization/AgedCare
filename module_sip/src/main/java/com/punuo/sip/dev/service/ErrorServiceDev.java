package com.punuo.sip.dev.service;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.punuo.sip.dev.event.DevLoginFailEvent;
import com.punuo.sip.dev.request.BaseDevSipRequest;
import com.punuo.sys.sdk.httplib.ErrorTipException;
import com.punuo.sys.sdk.util.HandlerExceptionUtils;

import org.greenrobot.eventbus.EventBus;
import org.zoolu.sip.message.BaseSipResponses;
import org.zoolu.sip.message.Message;

/**
 * Created by han.chen.
 * Date on 2019-09-24.
 **/
@Route(path = DevServicePath.PATH_ERROR)
public class ErrorServiceDev extends NormalDevRequestService<String> {

    @Override
    protected String getBody() {
        return null;
    }

    @Override
    protected void onSuccess(Message msg, String result) {
        int code = msg.getStatusLine().getCode();
        if (code == 100) {
            return;
        } else if (code == 401) {
            EventBus.getDefault().post(new DevLoginFailEvent());
        } else if (code == 400) {
            return;
        } else {
            HandlerExceptionUtils.handleException(new ErrorTipException(BaseSipResponses.reasonOf(code)));
        }
    }

    @Override
    protected void onError(Exception e) {
        HandlerExceptionUtils.handleException(e);
    }

    @Override
    public void handleTimeOut(BaseDevSipRequest baseSipRequest) {

    }
}
