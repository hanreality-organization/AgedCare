package com.punuo.sys.app.agedcare.request;

import com.punuo.sys.app.agedcare.request.model.ServiceNumberModel;
import com.punuo.sys.sdk.httplib.BaseRequest;

import java.util.List;

/**
 * Created by han.chen.
 * Date on 2021/2/20.
 **/
public class GetServiceNumberRequest extends BaseRequest<List<ServiceNumberModel>> {

    public GetServiceNumberRequest() {
        setRequestPath("/users/getServiceNumber");
    }
}
