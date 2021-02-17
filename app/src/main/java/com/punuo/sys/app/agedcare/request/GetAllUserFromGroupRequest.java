package com.punuo.sys.app.agedcare.request;

import com.punuo.sys.app.agedcare.httplib.BaseRequest;
import com.punuo.sys.app.agedcare.model.Device;
import com.punuo.sys.app.agedcare.request.model.DevInfo;
import com.punuo.sys.app.agedcare.request.model.DeviceModel;

import java.util.List;

/**
 * Created by han.chen.
 * Date on 2021/2/17.
 **/
public class GetAllUserFromGroupRequest extends BaseRequest<DeviceModel> {

    public GetAllUserFromGroupRequest() {
        setRequestPath("/groups/getAllUserFromGroup");
    }
}
