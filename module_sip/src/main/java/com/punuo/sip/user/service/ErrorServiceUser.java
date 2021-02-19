package com.punuo.sip.user.service;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.punuo.sip.user.event.UnauthorizedEvent;
import com.punuo.sip.user.request.BaseUserSipRequest;
import com.punuo.sys.sdk.account.AccountManager;
import com.punuo.sys.sdk.httplib.ErrorTipException;
import com.punuo.sys.sdk.util.HandlerExceptionUtils;
import com.punuo.sys.sdk.util.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.zoolu.sip.message.BaseSipResponses;
import org.zoolu.sip.message.Message;

/**
 * Created by han.chen.
 * Date on 2019-09-24.
 **/
@Route(path = UserServicePath.PATH_ERROR)
public class ErrorServiceUser extends NormalUserRequestService<String> {

    @Override
    protected String getBody() {
        return null;
    }

    @Override
    protected void onSuccess(Message msg, String result) {
        int code = msg.getStatusLine().getCode();
        if (code == 100) {

        } else if (code == 401) {
            EventBus.getDefault().post(new UnauthorizedEvent());
            if (!AccountManager.isLogin()) {
                ToastUtils.showToast("账号密码错误");
            }
        } else if (code == 400) {

        } else {
            HandlerExceptionUtils.handleException(new ErrorTipException(BaseSipResponses.reasonOf(code)));
        }
    }

    @Override
    protected void onError(Exception e) {
        HandlerExceptionUtils.handleException(e);
    }

    @Override
    public void handleTimeOut(BaseUserSipRequest baseUserSipRequest) {

    }
}
