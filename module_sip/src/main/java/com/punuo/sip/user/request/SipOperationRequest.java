package com.punuo.sip.user.request;

import com.punuo.sip.SipConfig;
import com.punuo.sys.sdk.account.AccountManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.zoolu.sip.address.NameAddress;
import org.zoolu.sip.address.SipURL;

import fr.arnaudguyon.xmltojsonlib.JsonToXml;

/**
 * Created by han.chen.
 * Date on 2021/1/29.
 * 双向视频发起端第一步
 **/
public class SipOperationRequest extends BaseUserSipRequest {
    public SipOperationRequest() {
        setSipRequestType(SipRequestType.Notify);
        setHasResponse(false);
    }

    @Override
    public NameAddress getDestNameAddress() {
        SipURL sipURL = new SipURL(AccountManager.getTargetDevId(), SipConfig.getServerIp(), SipConfig.getUserPort());
        return new NameAddress(AccountManager.getTargetDevId(), sipURL);
    }

    @Override
    public String getBody(){
        JSONObject body = new JSONObject();
        JSONObject value = new JSONObject();
        try {
            value.put("operate", "request");
            value.put("devId", AccountManager.getDevId());
            value.put("userId", AccountManager.getUserId());
            body.put("operation", value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonToXml jsonToXml = new JsonToXml.Builder(body).build();
        return jsonToXml.toFormattedString();
    }
}
