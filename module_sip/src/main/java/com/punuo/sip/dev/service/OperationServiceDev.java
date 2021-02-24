package com.punuo.sip.dev.service;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.punuo.sip.H264Config;
import com.punuo.sip.dev.model.OperationData;
import com.punuo.sip.dev.request.BaseDevSipRequest;
import com.punuo.sys.sdk.account.AccountManager;

import org.greenrobot.eventbus.EventBus;
import org.zoolu.sip.message.Message;

/**
 * Created by han.chen.
 * Date on 2021/1/29.
 **/
@Route(path = DevServicePath.PATH_OPERATION)
public class OperationServiceDev extends NormalDevRequestService<OperationData> {
    @Override
    protected String getBody() {
        return null;
    }

    @Override
    protected void onSuccess(Message msg, OperationData result) {
        H264Config.monitorType = H264Config.DOUBLE_MONITOR_NEGATIVE; //双向视频被动接收
        AccountManager.setTargetUserId(result.targetUserId);
        AccountManager.setTargetDevId(result.targetDevId);
        EventBus.getDefault().post(result);
    }

    @Override
    protected void onError(Exception e) {

    }

    @Override
    public void handleTimeOut(BaseDevSipRequest baseSipRequest) {

    }
}
