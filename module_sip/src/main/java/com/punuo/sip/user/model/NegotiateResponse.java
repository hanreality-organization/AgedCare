package com.punuo.sip.user.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by han.chen.
 * Date on 2019-08-12.
 **/
public class NegotiateResponse {

    /**
     * salt : 8b1fa
     * seed : 06aaeb48bccd11e9824b00163e1390bd
     * user_id : 321000000000594992
     * phone_num : 18758256058
     * real_name : None
     */

    @SerializedName("salt")
    public String salt;
    @SerializedName("seed")
    public String seed;
    @SerializedName("user_id")
    public String userId;
    @SerializedName("phone_num")
    public String phoneNum;
    @SerializedName("real_name")
    public String realName;
}
