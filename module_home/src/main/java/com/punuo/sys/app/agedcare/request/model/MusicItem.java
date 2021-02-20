package com.punuo.sys.app.agedcare.request.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 23578 on 2018/9/20.
 */

public class MusicItem {
    @SerializedName("id")
    public int id;
    @SerializedName("name")
    public String songName;
    @SerializedName("type")
    public String type;
    @SerializedName("cover")
    public String cover;
    @SerializedName("time")
    public String time;
}
