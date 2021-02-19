package com.punuo.sys.app.agedcare.request.model;

import com.google.gson.annotations.SerializedName;
import com.punuo.sys.app.agedcare.model.Device;

import java.util.List;

/**
 * Created by han.chen.
 * Date on 2021/2/17.
 **/
public class DeviceModel {
    @SerializedName("userList")
    public List<Device> mDevices;
}
