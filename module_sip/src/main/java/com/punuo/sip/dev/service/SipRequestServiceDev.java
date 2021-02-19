package com.punuo.sip.dev.service;

import com.alibaba.android.arouter.facade.template.IProvider;
import com.google.gson.JsonElement;
import com.punuo.sip.dev.request.BaseDevSipRequest;

import org.zoolu.sip.message.Message;

/**
 * Created by han.chen.
 * Date on 2019-08-20.
 **/
public interface SipRequestServiceDev extends IProvider {

    void handleRequest(Message msg, JsonElement jsonElement);

    void handleTimeOut(BaseDevSipRequest baseSipRequest);
}
