package com.punuo.sip.dev.service;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.punuo.sip.dev.event.SuspendMonitorEvent;
import com.punuo.sip.dev.request.BaseDevSipRequest;

import org.greenrobot.eventbus.EventBus;
import org.zoolu.sip.message.Message;

/**
 * Created by han.chen.
 * Date on 2021/2/17.
 **/
@Route(path = DevServicePath.PATH_SUSPEND_MONITOR)
public class SuspendMonitorServiceDev extends NormalDevRequestService<String> {
    @Override
    protected String getBody() {
        return null;
    }

    @Override
    protected void onSuccess(Message msg, String result) {
        EventBus.getDefault().post(new SuspendMonitorEvent());
    }

    @Override
    protected void onError(Exception e) {

    }

    @Override
    public void handleTimeOut(BaseDevSipRequest baseSipRequest) {

    }
}
