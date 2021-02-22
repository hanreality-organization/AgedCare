package com.punuo.sip.user.request;

import com.punuo.sip.SipConfig;

import org.json.JSONException;
import org.json.JSONObject;
import org.zoolu.sip.address.NameAddress;
import org.zoolu.sip.address.SipURL;

import fr.arnaudguyon.xmltojsonlib.JsonToXml;

/**
 * Created by han.chen.
 * Date on 2021/1/29.
 **/
public class SipQueryRequest extends BaseUserSipRequest {
    private final String targetDevId;
    public SipQueryRequest(String targetDevId) {
        setHasResponse(true);
        setSipRequestType(SipRequestType.Options);
        setTargetResponse("query_response");
        this.targetDevId = targetDevId;
    }

    @Override
    public NameAddress getDestNameAddress() {
        String devId = targetDevId.substring(0, targetDevId.length() - 4).concat("0160"); //设备id后4位替换成0160
        SipURL sipURL = new SipURL(devId, SipConfig.getServerIp(), SipConfig.getUserPort());
        return new NameAddress(devId, sipURL);
    }

    @Override
    public String getBody() {
        JSONObject body = new JSONObject();
        JSONObject value = new JSONObject();
        try {
            value.put("variable", "MediaInfo_Video");
            value.put("dev_type", "2");
            body.put("query", value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonToXml jsonToXml = new JsonToXml.Builder(body).build();
        return jsonToXml.toFormattedString();
    }
}
