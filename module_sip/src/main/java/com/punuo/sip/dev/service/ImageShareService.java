package com.punuo.sip.dev.service;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.punuo.sip.dev.model.ImageShare;
import com.punuo.sip.dev.request.BaseDevSipRequest;

import org.greenrobot.eventbus.EventBus;
import org.zoolu.sip.message.Message;

/**
 * Created by han.chen.
 * Date on 2021/2/23.
 **/
@Route(path = DevServicePath.PATH_IMAGE_SHARE)
public class ImageShareService extends NormalDevRequestService<ImageShare> {
    @Override
    protected String getBody() {
        return null;
    }

    @Override
    protected void onSuccess(Message msg, ImageShare result) {
        EventBus.getDefault().post(result);
    }

    @Override
    protected void onError(Exception e) {

    }

    @Override
    public void handleTimeOut(BaseDevSipRequest baseSipRequest) {

    }
}