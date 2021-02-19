package com.punuo.sip.user.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by han.chen.
 * Date on 2019-09-29.
 **/
public class DevNotifyData {
    @SerializedName("login")
    public DevInfo mDevInfo;

    public static class DevInfo {
        @SerializedName("devid")
        public String devId; //设备号

        @SerializedName("live")
        public int live; //是否在线， 0 / 掉线  1 / 在线
    }
}
