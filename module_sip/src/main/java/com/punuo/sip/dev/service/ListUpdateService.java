package com.punuo.sip.dev.service;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.punuo.sip.dev.event.UpdateBindUserEvent;
import com.punuo.sip.dev.request.BaseDevSipRequest;

import org.greenrobot.eventbus.EventBus;
import org.zoolu.sip.message.Message;

/**
 * Created by han.chen.
 * Date on 2021/2/23.
 **/
@Route(path = DevServicePath.PATH_LIST_UPDATE)
public class ListUpdateService extends NormalDevRequestService<Object> {
    @Override
    protected String getBody() {
        return null;
    }

    @Override
    protected void onSuccess(Message msg, Object result) {
        EventBus.getDefault().post(new UpdateBindUserEvent());
    }

    @Override
    protected void onError(Exception e) {

    }

    @Override
    public void handleTimeOut(BaseDevSipRequest baseSipRequest) {

    }
}
