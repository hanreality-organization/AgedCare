package com.punuo.sip.dev.service;

import android.util.Log;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.punuo.sip.H264Config;
import com.punuo.sip.dev.H264ConfigDev;
import com.punuo.sip.dev.event.MonitorEvent;
import com.punuo.sip.dev.model.MediaData;
import com.punuo.sip.dev.request.BaseDevSipRequest;
import com.punuo.sip.user.H264ConfigUser;
import com.punuo.sip.user.SipUserManager;
import com.punuo.sip.user.request.SipQueryRequest;
import com.punuo.sys.sdk.account.AccountManager;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;
import org.zoolu.sip.message.Message;

import fr.arnaudguyon.xmltojsonlib.JsonToXml;

/**
 * Created by han.chen.
 * Date on 2021/1/29.
 **/
@Route(path = DevServicePath.PATH_MEDIA)
public class MediaServiceDev extends NormalDevRequestService<MediaData> {
    @Override
    protected String getBody() {
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

    @Override
    protected void onSuccess(Message msg, MediaData result) {
        H264ConfigDev.initMediaData(result);
        onResponse(msg);
        //TODO 启动视频编码
        if (H264Config.monitorType == H264Config.DOUBLE_MONITOR_NEGATIVE) {
            Log.v(TAG, "响应双向视频");
            SipQueryRequest request = new SipQueryRequest(AccountManager.getTargetDevId());
            SipUserManager.getInstance().addRequest(request);
        } else if (H264Config.monitorType == H264Config.DOUBLE_MONITOR_POSITIVE) {
            Log.v(TAG, "主动双向视频");
            EventBus.getDefault().post(new MonitorEvent(H264Config.DOUBLE_MONITOR_POSITIVE, AccountManager.getTargetDevId()));
        } else {
            EventBus.getDefault().post(new MonitorEvent(H264Config.SINGLE_MONITOR, AccountManager.getTargetDevId()));
        }

    }

    @Override
    protected void onError(Exception e) {

    }

    @Override
    public void handleTimeOut(BaseDevSipRequest baseSipRequest) {

    }
}
