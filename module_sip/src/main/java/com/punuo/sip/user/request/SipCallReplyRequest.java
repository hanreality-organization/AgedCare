package com.punuo.sip.user.request;

import com.punuo.sip.SipConfig;

import org.json.JSONException;
import org.json.JSONObject;
import org.zoolu.sip.address.NameAddress;
import org.zoolu.sip.address.SipURL;

import fr.arnaudguyon.xmltojsonlib.JsonToXml;

/**
 * Created by han.chen.
 * Date on 2021/2/3.
 **/
public class SipCallReplyRequest extends BaseUserSipRequest {
    private final String operate;
    private final String targetDevId;
    public SipCallReplyRequest(String operate, String targetDevId) {
        setSipRequestType(SipRequestType.Notify);
        setHasResponse(false);
        this.operate = operate;
        this.targetDevId = targetDevId;
    }

    @Override
    public String getBody() {
        JSONObject body = new JSONObject();
        JSONObject value = new JSONObject();
        try {
            value.put("operate", operate);
            body.put("call_response", value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonToXml jsonToXml = new JsonToXml.Builder(body).build();
        return jsonToXml.toFormattedString();
    }

    @Override
    public NameAddress getDestNameAddress() {
        SipURL sipURL = new SipURL(targetDevId, SipConfig.getServerIp(), SipConfig.getUserPort());
        return new NameAddress(targetDevId, sipURL);
    }
}
