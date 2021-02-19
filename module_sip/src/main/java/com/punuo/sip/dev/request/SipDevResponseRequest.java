package com.punuo.sip.dev.request;

import com.punuo.sip.user.request.SipRequestType;

/**
 * Created by han.chen.
 * Date on 2019-08-21.
 **/
public class SipDevResponseRequest extends BaseDevSipRequest {
    private String body;

    public SipDevResponseRequest() {
        setSipRequestType(SipRequestType.Response);
        setHasResponse(false);
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String getBody() {
        return body;
    }
}
