package com.punuo.sip.user.request;

import com.punuo.sip.SipConfig;

import org.json.JSONException;
import org.json.JSONObject;
import org.zoolu.sip.address.NameAddress;
import org.zoolu.sip.address.SipURL;

import fr.arnaudguyon.xmltojsonlib.JsonToXml;

/**
 * Created by han.chen.
 * Date on 2021/3/11.
 **/
public class SipUserAlarmRequest extends BaseUserSipRequest {
    private final String targetUserId;
    public SipUserAlarmRequest(String targetUserId) {
        this.targetUserId = targetUserId;
        setHasResponse(false);
        setSipRequestType(SipRequestType.Notify);
    }

    @Override
    public String getBody() {
        JSONObject body = new JSONObject();
        JSONObject value = new JSONObject();
        try {
            value.put("userId", targetUserId);
            body.put("alarm", value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonToXml jsonToXml = new JsonToXml.Builder(body).build();
        return jsonToXml.toFormattedString();
    }

    @Override
    public NameAddress getDestNameAddress() {
        SipURL remote = new SipURL(targetUserId, SipConfig.getServerIp(), SipConfig.getDevPort());
        return new NameAddress(targetUserId, remote);
    }
}
