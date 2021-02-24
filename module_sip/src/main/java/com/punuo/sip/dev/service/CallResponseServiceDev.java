package com.punuo.sip.dev.service;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.punuo.sip.dev.model.CallResponse;
import com.punuo.sip.dev.request.BaseDevSipRequest;
import com.punuo.sip.user.SipUserManager;
import com.punuo.sip.user.request.SipQueryRequest;
import com.punuo.sys.sdk.account.AccountManager;

import org.greenrobot.eventbus.EventBus;
import org.zoolu.sip.message.Message;

/**
 * Created by han.chen.
 * Date on 2021/1/29.
 **/
@Route(path = DevServicePath.PATH_CALL_RESPONSE)
public class CallResponseServiceDev extends NormalDevRequestService<CallResponse> {
    @Override
    protected String getBody() {
        return null;
    }

    @Override
    protected void onSuccess(Message msg, CallResponse result) {
        String operate = result.operate;
        switch (operate) {
            case "agree":
                //目标用户接受了
                SipQueryRequest request = new SipQueryRequest(AccountManager.getTargetDevId());
                SipUserManager.getInstance().addRequest(request);
                break;
            case "refuse":
                //目标用户拒绝了
            case "cancel":
                //目标用户挂断了
                EventBus.getDefault().post(result);
                break;
        }
        onResponse(msg);
    }

    @Override
    protected void onError(Exception e) {

    }

    @Override
    public void handleTimeOut(BaseDevSipRequest baseSipRequest) {

    }
}
