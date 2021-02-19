package com.punuo.sip.user.request;

import org.json.JSONException;
import org.json.JSONObject;

import fr.arnaudguyon.xmltojsonlib.JsonToXml;

/**
 * Created by han.chen.
 * Date on 2021/1/14.
 **/
public class SipUserLogoutRequest extends BaseUserSipRequest {

    public SipUserLogoutRequest() {
        setSipRequestType(SipRequestType.Notify);
        setHasResponse(false);
    }

    @Override
    public String getBody() {
        JSONObject body = new JSONObject();
        try {
            body.put("logout", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonToXml jsonToXml = new JsonToXml.Builder(body).build();
        return jsonToXml.toFormattedString();
    }
}
