package com.punuo.sip.dev.service;

import android.text.TextUtils;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.punuo.sip.dev.SipDevManager;
import com.punuo.sip.dev.model.DirectionControl;
import com.punuo.sip.dev.request.BaseDevSipRequest;

import org.zoolu.sip.message.Message;

/**
 * Created by han.chen.
 * Date on 2021/2/23.
 **/
@Route(path = DevServicePath.PATH_DIRECTION_CONTROL)
public class DirectionControlService extends NormalDevRequestService<DirectionControl> {
    public static final byte[] TURN_LEFT = {(byte) 0xff, 0x01, 0x00, 0x04, (byte) 0xff, 0x00, 0x04};
    public static final byte[] TURN_RIGHT = {(byte) 0xff, 0x01, 0x00, 0x02, (byte) 0xff, 0x00, 0x02};
    public static final byte[] STOP = {(byte) 0xff, 0x01, 0x00, 0x00, 0x00, 0x00, 0x01};

    @Override
    protected String getBody() {
        return null;
    }

    @Override
    protected void onSuccess(Message msg, DirectionControl result) {
        if (TextUtils.equals("stop", result.operate)) {
            SipDevManager.getInstance().setData(STOP);
        } else if (TextUtils.equals("left", result.operate)) {
            SipDevManager.getInstance().setData(TURN_LEFT);
        } else if (TextUtils.equals("right", result.operate)) {
            SipDevManager.getInstance().setData(TURN_RIGHT);
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
