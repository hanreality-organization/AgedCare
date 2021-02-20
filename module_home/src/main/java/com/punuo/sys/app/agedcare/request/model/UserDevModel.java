package com.punuo.sys.app.agedcare.request.model;

import com.google.gson.annotations.SerializedName;
import com.punuo.sys.sdk.model.PNBaseModel;

/**
 * Created by han.chen.
 * Date on 2021/2/19.
 **/
public class UserDevModel extends PNBaseModel {
    @SerializedName("devid")
    public String devId;
}
