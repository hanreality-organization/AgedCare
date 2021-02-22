package com.punuo.sip.user.request;

import com.punuo.sip.SipConfig;
import com.punuo.sip.user.H264ConfigUser;

import org.json.JSONException;
import org.json.JSONObject;
import org.zoolu.sip.address.NameAddress;
import org.zoolu.sip.address.SipURL;

import fr.arnaudguyon.xmltojsonlib.JsonToXml;

/**
 * Created by han.chen.
 * Date on 2021/1/29.
 **/
public class SipMediaRequest extends BaseUserSipRequest {
    private final String targetDevId;
    public SipMediaRequest(String targetDevId) {
        setHasResponse(true);
        setSipRequestType(SipRequestType.Invite);
        setTargetResponse("media");
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
            value.put("resolution", H264ConfigUser.resolution);
            value.put("video", "H264");
            value.put("audio", "G.711");
            value.put("kbps", "800");
            value.put("self", "192.168.1.129 UDP 5200");
            value.put("mode", "active");
            value.put("magic", "01234567890123456789012345678901");
            value.put("dev_type", "2");
            body.put("media", value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonToXml jsonToXml = new JsonToXml.Builder(body).build();
        return jsonToXml.toFormattedString();
    }
}
