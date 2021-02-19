package com.punuo.sip.dev.request;

import com.punuo.sip.SipConfig;
import com.punuo.sip.user.request.SipRequestType;

import org.zoolu.sip.address.NameAddress;

/**
 * Created by han.chen.
 * Date on 2021/1/13.
 **/
public class SipDevRegisterRequest extends BaseDevSipRequest {

    public SipDevRegisterRequest() {
        setSipRequestType(SipRequestType.Register);
        setTargetResponse("negotiate_response");
    }

    @Override
    public NameAddress getLocalNameAddress() {
        return SipConfig.getDevRegisterAddress();
    }
}
