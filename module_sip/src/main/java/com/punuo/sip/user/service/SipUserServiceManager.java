package com.punuo.sip.user.service;

import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;

import com.alibaba.android.arouter.launcher.ARouter;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.punuo.sip.user.request.BaseUserSipRequest;
import com.punuo.sys.sdk.util.BaseHandler;

/**
 * Created by han.chen.
 * Date on 2019-08-20.
 **/
public class SipUserServiceManager implements BaseHandler.MessageHandler {
    private static SipUserServiceManager sSipServiceManager;
    private static final int MSG_HANDLER = 1;
    private static final int MSG_HANDLER_TIME_OUT = 2;

    public static SipUserServiceManager getInstance() {
        if (sSipServiceManager == null) {
            synchronized (SipUserServiceManager.class) {
                if (sSipServiceManager == null) {
                    sSipServiceManager = new SipUserServiceManager();
                }
            }
        }
        return sSipServiceManager;
    }

    private final BaseHandler mBaseHandler;

    private SipUserServiceManager() {
        mBaseHandler = new BaseHandler(this);
    }

    public void handleRequest(String key, String jsonStr, org.zoolu.sip.message.Message msg) {
        //回调到主线程
        Message message = new Message();
        message.what = MSG_HANDLER;
        Bundle bundle = new Bundle();
        bundle.putString("key", key);
        bundle.putString("jsonStr", jsonStr);
        message.setData(bundle);
        message.obj = msg;
        mBaseHandler.sendMessage(message);
    }

    public void handleTimeOut(String key, BaseUserSipRequest baseUserSipRequest) {
        //回调到主线程
        Message message = new Message();
        message.what = MSG_HANDLER_TIME_OUT;
        Bundle bundle = new Bundle();
        bundle.putString("key", key);
        message.setData(bundle);
        message.obj = baseUserSipRequest;
        mBaseHandler.sendMessage(message);
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_HANDLER:
                try {
                    Bundle bundle = msg.getData();
                    String key = bundle.getString("key");
                    String jsonStr = bundle.getString("jsonStr", "{}");
                    JsonElement jsonElement = new JsonParser().parse(jsonStr);
                    if (!TextUtils.isEmpty(key) && UserServicePath.sMapping.contains("/user/" + key)) {
                        NormalUserRequestService<?> service = (NormalUserRequestService<?>) ARouter.getInstance()
                                .build("/user/" + key).navigation();
                        if (service != null) {
                            service.handleRequest((org.zoolu.sip.message.Message) msg.obj, jsonElement);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case MSG_HANDLER_TIME_OUT:
                Bundle bundle = msg.getData();
                String key = bundle.getString("key");
                if (!TextUtils.isEmpty(key) && UserServicePath.sMapping.contains("/user/" + key)) {
                    NormalUserRequestService<?> service = (NormalUserRequestService<?>) ARouter.getInstance()
                            .build("/user/" + key).navigation();
                    if (service != null) {
                        service.handleTimeOut((BaseUserSipRequest) msg.obj);
                    }
                }
                break;
            default:
                break;
        }

    }
}
