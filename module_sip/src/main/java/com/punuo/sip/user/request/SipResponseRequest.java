package com.punuo.sip.user.request;

/**
 * Created by han.chen.
 * Date on 2019-08-21.
 **/
public class SipResponseRequest extends BaseUserSipRequest {
    private String body;

    public SipResponseRequest() {
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
