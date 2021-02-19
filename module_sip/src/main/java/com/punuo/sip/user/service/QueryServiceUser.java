package com.punuo.sip.user.service;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.punuo.sip.user.H264ConfigUser;
import com.punuo.sip.user.SipUserManager;
import com.punuo.sip.user.model.QueryResponse;
import com.punuo.sip.user.request.BaseUserSipRequest;
import com.punuo.sip.user.request.SipMediaRequest;
import com.punuo.sys.sdk.account.AccountManager;
import com.punuo.sys.sdk.util.HandlerExceptionUtils;
import com.punuo.sys.sdk.util.ToastUtils;

import org.zoolu.sip.message.Message;

/**
 * Created by han.chen.
 * Date on 2021/1/29.
 **/
@Route(path = UserServicePath.PATH_QUERY)
public class QueryServiceUser extends NormalUserRequestService<QueryResponse> {
    @Override
    protected String getBody() {
        return null;
    }

    @Override
    protected void onSuccess(Message msg, QueryResponse result) {
        H264ConfigUser.initQueryData(result);
        SipMediaRequest request = new SipMediaRequest(AccountManager.getTargetDevId());
        SipUserManager.getInstance().addRequest(request);
    }

    @Override
    protected void onError(Exception e) {
        HandlerExceptionUtils.handleException(e);
    }

    @Override
    public void handleTimeOut(BaseUserSipRequest baseUserSipRequest) {
        ToastUtils.showToast("视频请求失败，请稍后重试");
    }
}
