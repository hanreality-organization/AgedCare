package com.punuo.sip.user.request;

import com.punuo.sip.SipConfig;

import org.json.JSONException;
import org.json.JSONObject;
import org.zoolu.sip.address.NameAddress;
import org.zoolu.sip.address.SipURL;

import fr.arnaudguyon.xmltojsonlib.JsonToXml;

/**
 * Created by han.chen.
 * Date on 2021/2/23.
 **/
public class SipSuspendMonitorRequest extends BaseUserSipRequest {
    private final String mTargetDevId;
    public SipSuspendMonitorRequest(String targetDevId) {
        setSipRequestType(SipRequestType.Notify);
        mTargetDevId = targetDevId;
        setHasResponse(false);
    }

    @Override
    public String getBody() {
        JSONObject body = new JSONObject();
        JSONObject value = new JSONObject();
        try {
            value.put("devId", mTargetDevId);
            body.put("suspend_monitor", value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonToXml jsonToXml = new JsonToXml.Builder(body).build();
        return jsonToXml.toFormattedString();
    }

    @Override
    public NameAddress getDestNameAddress() {
        String devID = mTargetDevId.substring(0, mTargetDevId.length() - 4).concat("0160"); //设备id后4位替换成0160
        SipURL sipURL = new SipURL(devID, SipConfig.getServerIp(), SipConfig.getUserPort());
        return new NameAddress(devID, sipURL);
    }
}
