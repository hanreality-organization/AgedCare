package com.punuo.sys.app.agedcare.request.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by han.chen.
 * Date on 2021/2/20.
 **/
public class ServiceNumberModel {

    @SerializedName("id")
    public String id;

    @SerializedName("devid")
    public String devId;

    @SerializedName("housekeep")
    public String houseKeepNumber;

    @SerializedName("orderfood")
    public String orderFoodNumber;

    @SerializedName("property")
    public String propertyNumber;

}
