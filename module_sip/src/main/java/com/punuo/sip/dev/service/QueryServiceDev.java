package com.punuo.sip.dev.service;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.punuo.sip.dev.request.BaseDevSipRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.zoolu.sip.message.Message;

import fr.arnaudguyon.xmltojsonlib.JsonToXml;

/**
 * Created by han.chen.
 * Date on 2021/1/29.
 **/
@Route(path = DevServicePath.PATH_QUERY)
public class QueryServiceDev extends NormalDevRequestService<Object> {

    @Override
    protected String getBody() {
        JSONObject body = new JSONObject();
        JSONObject value = new JSONObject();
        try {
            value.put("variable", "MediaInfo_Video");
            value.put("result", "0");
            value.put("video", "H.264");
            value.put("resolution", "MOBILE_S6");
            value.put("framerate", "25");
            value.put("bitrate", "256");
            value.put("bright", "51");
            value.put("contrast", "49");
            value.put("saturation", "50");
            body.put("query_response", value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonToXml jsonToXml = new JsonToXml.Builder(body).build();
        return jsonToXml.toFormattedString();
    }

    @Override
    protected void onSuccess(Message msg, Object result) {
        onResponse(msg);
    }

    @Override
    protected void onError(Exception e) {

    }

    @Override
    public void handleTimeOut(BaseDevSipRequest baseSipRequest) {

    }
}
