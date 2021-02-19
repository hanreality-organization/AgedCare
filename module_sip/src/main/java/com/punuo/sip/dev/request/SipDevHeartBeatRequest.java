package com.punuo.sip.dev.request;

import com.punuo.sip.user.request.SipRequestType;

import org.json.JSONException;
import org.json.JSONObject;

import fr.arnaudguyon.xmltojsonlib.JsonToXml;

/**
 * Created by han.chen.
 * Date on 2019-08-12.
 * 心跳请求
 **/
public class SipDevHeartBeatRequest extends BaseDevSipRequest {

    public SipDevHeartBeatRequest() {
        setSipRequestType(SipRequestType.Register);
        setTargetResponse("login_response");
    }

    @Override
    public String getBody() {
        JSONObject body = new JSONObject();
        try {
            body.put("heartbeat_request", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonToXml jsonToXml = new JsonToXml.Builder(body).build();
        return jsonToXml.toFormattedString();
    }
}
