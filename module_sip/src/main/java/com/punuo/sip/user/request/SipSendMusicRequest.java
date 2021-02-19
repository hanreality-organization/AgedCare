package com.punuo.sip.user.request;

import org.json.JSONException;
import org.json.JSONObject;
import org.zoolu.sip.address.NameAddress;
import org.zoolu.sip.address.SipURL;

import fr.arnaudguyon.xmltojsonlib.JsonToXml;

/**
 * Created by han.chen.
 * Date on 2020-01-04.
 **/
public class SipSendMusicRequest extends BaseUserSipRequest {
    private String musicUrl;
    private String mDevId;

    public SipSendMusicRequest(String devId, String musicUrl) {
        setSipRequestType(SipRequestType.Notify);
        setHasResponse(true);
        setTargetResponse("play_music_response");
        this.musicUrl = musicUrl;
        mDevId = devId;
    }

    @Override
    public String getBody() {
        JSONObject body = new JSONObject();
        JSONObject value = new JSONObject();
        try {
            value.put("music_url", musicUrl);
            body.put("play_music", value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonToXml jsonToXml = new JsonToXml.Builder(body).build();
        return jsonToXml.toFormattedString();
    }

    @Override
    public NameAddress getDestNameAddress() {
        String devID = mDevId.substring(0, mDevId.length() - 4).concat("0160"); //设备id后4位替换成0160
        SipURL sipURL = new SipURL(devID, com.punuo.sip.SipConfig.getServerIp(), com.punuo.sip.SipConfig.getUserPort());
        return new NameAddress(devID, sipURL);
    }
}
