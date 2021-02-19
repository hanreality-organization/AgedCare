package com.punuo.sys.app.agedcare.request;

import com.punuo.sys.app.agedcare.request.model.DeviceModel;
import com.punuo.sys.sdk.httplib.BaseRequest;

/**
 * Created by han.chen.
 * Date on 2021/2/17.
 **/
public class GetAllUserFromGroupRequest extends BaseRequest<DeviceModel> {

    public GetAllUserFromGroupRequest() {
        setRequestPath("/groups/getAllUserFromGroup");
    }
}
