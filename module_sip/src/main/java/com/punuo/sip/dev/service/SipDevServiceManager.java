package com.punuo.sip.dev.service;

import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;

import com.alibaba.android.arouter.launcher.ARouter;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.punuo.sip.dev.request.BaseDevSipRequest;
import com.punuo.sys.sdk.util.BaseHandler;

/**
 * Created by han.chen.
 * Date on 2019-08-20.
 **/
public class SipDevServiceManager implements BaseHandler.MessageHandler {
    private static SipDevServiceManager sSipServiceManager;
    private static final int MSG_HANDLER = 1;
    private static final int MSG_HANDLER_TIME_OUT = 2;

    public static SipDevServiceManager getInstance() {
        if (sSipServiceManager == null) {
            synchronized (SipDevServiceManager.class) {
                if (sSipServiceManager == null) {
                    sSipServiceManager = new SipDevServiceManager();
                }
            }
        }
        return sSipServiceManager;
    }

    private final BaseHandler mBaseHandler;

    private SipDevServiceManager() {
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

    public void handleTimeOut(String key, BaseDevSipRequest baseSipRequest) {
        //回调到主线程
        Message message = new Message();
        message.what = MSG_HANDLER_TIME_OUT;
        Bundle bundle = new Bundle();
        bundle.putString("key", key);
        message.setData(bundle);
        message.obj = baseSipRequest;
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
                    if (!TextUtils.isEmpty(key) && DevServicePath.sMapping.contains("/dev/" + key)) {
                        NormalDevRequestService<?> service = (NormalDevRequestService<?>) ARouter.getInstance()
                                .build("/dev/" + key).navigation();
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
                if (!TextUtils.isEmpty(key) && DevServicePath.sMapping.contains("/dev/" + key)) {
                    NormalDevRequestService<?> service = (NormalDevRequestService<?>) ARouter.getInstance()
                            .build("/dev/" + key).navigation();
                    if (service != null) {
                        service.handleTimeOut((BaseDevSipRequest) msg.obj);
                    }
                }
                break;
            default:
                break;
        }

    }
}
