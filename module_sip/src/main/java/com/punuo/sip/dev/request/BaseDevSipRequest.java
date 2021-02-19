package com.punuo.sip.dev.request;

import android.util.Log;

import com.punuo.sip.SipConfig;
import com.punuo.sip.dev.message.SipDevMessageFactory;
import com.punuo.sip.dev.service.SipDevServiceManager;
import com.punuo.sip.user.request.SipRequestType;

import org.zoolu.sip.address.NameAddress;
import org.zoolu.sip.message.Message;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by han.chen.
 * Date on 2019-08-12.
 **/
public abstract class BaseDevSipRequest {
    private SipRequestType mSipRequestType;
    private Message mMessage;
    private int mCode;
    private String mReason;
    private boolean hasResponse = true; //sip请求是否有response
    private String mTargetResponse = "";
    private Timer mTimer = new Timer();

    public BaseDevSipRequest() {
    }

    public void setSipRequestType(SipRequestType sipRequestType) {
        mSipRequestType = sipRequestType;
    }

    public void setHasResponse(boolean hasResponse) {
        this.hasResponse = hasResponse;
    }

    public boolean hasResponse() {
        return hasResponse;
    }

    public void setTargetResponse(String targetResponse) {
        mTargetResponse = targetResponse;
    }

    public String getTargetResponse() {
        return mTargetResponse;
    }

    public void startCounting() {
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.d("设备", mTargetResponse + "请求超时");
                SipDevServiceManager.getInstance().handleTimeOut(mTargetResponse, BaseDevSipRequest.this);
            }
        }, 3 * 1000);
    }

    public void response() {
        try {
            mTimer.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setResponse(Message message, int code, String reason) {
        mMessage = message;
        mCode = code;
        mReason = reason;
    }

    /**
     * 获取远程目标地址
     *
     * @return NameAddress
     */
    public NameAddress getDestNameAddress() {
        return SipConfig.getDevServerAddress();
    }

    /**
     * 获取本地地址
     *
     * @return NameAddress
     */
    public NameAddress getLocalNameAddress() {
        return SipConfig.getDevNormalAddress();
    }

    /**
     * 获取消息体
     *
     * @return String
     */
    public String getBody() {
        return null;
    }

    public Message build() {
        switch (mSipRequestType) {
            case Register:
                return SipDevMessageFactory.createRegisterRequest(getDestNameAddress(), getLocalNameAddress(), getBody());
            case Subscribe:
                return SipDevMessageFactory.createSubscribeRequest(getDestNameAddress(), getLocalNameAddress(), getBody());
            case Notify:
                return SipDevMessageFactory.createNotifyRequest(getDestNameAddress(), getLocalNameAddress(), getBody());
            case Invite:
                return SipDevMessageFactory.createInviteRequest(getDestNameAddress(), getLocalNameAddress(), getBody());
            case Options:
                return SipDevMessageFactory.createOptionsRequest(getDestNameAddress(), getLocalNameAddress(), getBody());
            case Bye:
                return SipDevMessageFactory.createByeRequest(getDestNameAddress(), getLocalNameAddress());
            case Response:
                return SipDevMessageFactory.createResponse(mMessage, mCode, mReason, getBody());
            default:
                return null;
        }
    }
}
