package com.punuo.sip.user.service;

import android.text.TextUtils;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.punuo.sip.user.model.ImageShare;
import com.punuo.sip.user.request.BaseUserSipRequest;
import com.punuo.sys.sdk.util.HandlerExceptionUtils;
import com.punuo.sys.sdk.util.ToastUtils;

import org.zoolu.sip.message.Message;

/**
 * Created by han.chen.
 * Date on 2021/1/29.
 **/
@Route(path = UserServicePath.PATH_IMAGE_SHARE)
public class ImageShareServiceUser extends NormalUserRequestService<ImageShare> {
    @Override
    protected String getBody() {
        return null;
    }

    @Override
    protected void onSuccess(Message msg, ImageShare result) {
        if (TextUtils.equals("200", result.code)) {
            ToastUtils.showToast("分享成功");
        } else {
            ToastUtils.showToast("分享失败");
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
