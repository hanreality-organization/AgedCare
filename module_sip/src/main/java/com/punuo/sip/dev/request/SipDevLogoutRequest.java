package com.punuo.sip.dev.request;

import com.punuo.sip.user.request.SipRequestType;

import org.json.JSONException;
import org.json.JSONObject;

import fr.arnaudguyon.xmltojsonlib.JsonToXml;

/**
 * Created by han.chen.
 * Date on 2021/1/14.
 **/
public class SipDevLogoutRequest extends BaseDevSipRequest {

    public SipDevLogoutRequest() {
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
