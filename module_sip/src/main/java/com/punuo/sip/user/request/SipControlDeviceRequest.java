package com.punuo.sip.user.request;

import com.punuo.sip.SipConfig;

import org.json.JSONException;
import org.json.JSONObject;
import org.zoolu.sip.address.NameAddress;
import org.zoolu.sip.address.SipURL;

import fr.arnaudguyon.xmltojsonlib.JsonToXml;

/**
 * Created by han.chen.
 * Date on 2019-08-17.
 **/
public class SipControlDeviceRequest extends BaseUserSipRequest {
    private String mOperate;
    private String mDevId;

    public SipControlDeviceRequest(String operate, String devId) {
        setSipRequestType(SipRequestType.Notify);
        mOperate = operate;
        mDevId = devId;
    }

    @Override
    public NameAddress getDestNameAddress() {
        SipURL remote = new SipURL(mDevId, SipConfig.getServerIp(), SipConfig.getUserPort());
        return new NameAddress(mDevId, remote);
    }

    @Override
    public String getBody() {
        JSONObject body = new JSONObject();
        JSONObject value = new JSONObject();
        try {
            value.put("operate", mOperate);
            body.put("direction_control", value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonToXml jsonToXml = new JsonToXml.Builder(body).build();
        return jsonToXml.toFormattedString();
    }
}
