package com.punuo.sip.dev.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by han.chen.
 * Date on 2021/1/29.
 **/
public class OperationData {
    @SerializedName("devId")
    public String targetDevId; //双向视频对方的设备ID
    @SerializedName("userId")
    public String targetUserId; //双向视频对方的用户ID
}
