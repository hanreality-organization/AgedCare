package com.punuo.sip.user.request;

import com.punuo.sip.SipConfig;

import org.zoolu.sip.address.NameAddress;
import org.zoolu.sip.address.SipURL;

/**
 * Created by han.chen.
 * Date on 2019-09-21.
 **/
public class SipByeRequest extends BaseUserSipRequest {

    private final String mTargetDevId;

    public SipByeRequest(String targetDevId) {
        setSipRequestType(SipRequestType.Bye);
        mTargetDevId = targetDevId;
        setHasResponse(false);
    }

    @Override
    public NameAddress getDestNameAddress() {
        String devID = mTargetDevId.substring(0, mTargetDevId.length() - 4).concat("0160"); //设备id后4位替换成0160
        SipURL sipURL = new SipURL(devID, SipConfig.getServerIp(), SipConfig.getUserPort());
        return new NameAddress(devID, sipURL);
    }
}
