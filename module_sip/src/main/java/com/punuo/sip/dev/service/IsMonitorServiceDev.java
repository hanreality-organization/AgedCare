package com.punuo.sip.dev.service;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.punuo.sip.H264Config;
import com.punuo.sip.dev.request.BaseDevSipRequest;
import com.punuo.sip.user.model.IsMonitor;
import com.punuo.sys.sdk.account.AccountManager;

import org.zoolu.sip.message.Message;

/**
 * Created by han.chen.
 * Date on 2021/1/29.
 **/
@Route(path = DevServicePath.PATH_IS_MONITOR)
public class IsMonitorServiceDev extends NormalDevRequestService<IsMonitor> {
    @Override
    protected String getBody() {
        return null;
    }

    @Override
    protected void onSuccess(Message msg, IsMonitor result) {
        onResponse(msg);
        if (result.isMonitor) {
            H264Config.monitorType = H264Config.SINGLE_MONITOR; //监控
            AccountManager.setTargetDevId(result.targetDevId);
            AccountManager.setTargetUserId(result.targetDevId);
        }

    }

    @Override
    protected void onError(Exception e) {

    }

    @Override
    public void handleTimeOut(BaseDevSipRequest baseDevSipRequest) {

    }
}
