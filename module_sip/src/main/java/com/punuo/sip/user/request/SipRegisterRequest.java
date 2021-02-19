package com.punuo.sip.user.request;

import com.punuo.sip.user.model.NegotiateResponse;
import com.punuo.sys.sdk.sercet.SHA1;

import org.json.JSONException;
import org.json.JSONObject;

import fr.arnaudguyon.xmltojsonlib.JsonToXml;

/**
 * Created by han.chen.
 * Date on 2019-08-12.
 * 注册第二步
 **/
public class SipRegisterRequest extends BaseUserSipRequest {
    private NegotiateResponse mNegotiateResponse;
    public SipRegisterRequest(NegotiateResponse data) {
        setSipRequestType(SipRequestType.Register);
        setTargetResponse("login_response");
        mNegotiateResponse = data;
    }

    @Override
    public String getBody() {
        if (mNegotiateResponse == null) {
            return null;
        }
        String password = "123456";
        password = SHA1.getInstance().hashData(mNegotiateResponse.salt + password);
        password = SHA1.getInstance().hashData(mNegotiateResponse.seed + password);
        JSONObject body = new JSONObject();
        JSONObject value = new JSONObject();
        try {
            value.put("password", password);
            body.put("login_request", value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonToXml jsonToXml = new JsonToXml.Builder(body).build();
        return jsonToXml.toFormattedString();
    }
}
