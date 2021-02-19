package com.punuo.sip.user.request;

import com.punuo.sip.SipConfig;

import org.zoolu.sip.address.NameAddress;

/**
 * Created by han.chen.
 * Date on 2019-08-12.
 * sip注册第一步
 **/
public class SipGetUserIdRequest extends BaseUserSipRequest {

    public SipGetUserIdRequest() {
        setSipRequestType(SipRequestType.Register);
        setTargetResponse("negotiate_response");
    }

    @Override
    public NameAddress getLocalNameAddress() {
        return SipConfig.getUserRegisterAddress();
    }
}
