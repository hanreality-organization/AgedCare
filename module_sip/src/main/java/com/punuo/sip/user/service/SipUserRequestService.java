package com.punuo.sip.user.service;

import com.alibaba.android.arouter.facade.template.IProvider;
import com.google.gson.JsonElement;
import com.punuo.sip.user.request.BaseUserSipRequest;

import org.zoolu.sip.message.Message;

/**
 * Created by han.chen.
 * Date on 2019-08-20.
 **/
public interface SipUserRequestService extends IProvider {

    void handleRequest(Message msg, JsonElement jsonElement);

    void handleTimeOut(BaseUserSipRequest baseUserSipRequest);
}
