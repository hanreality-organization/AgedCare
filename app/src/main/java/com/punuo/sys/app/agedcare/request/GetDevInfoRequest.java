package com.punuo.sys.app.agedcare.request;

import com.punuo.sys.app.agedcare.httplib.BaseRequest;
import com.punuo.sys.app.agedcare.request.model.DevInfo;
import com.punuo.sys.app.agedcare.request.model.DevModel;

/**
 * Created by han.chen.
 * Date on 2021/2/17.
 **/
public class GetDevInfoRequest extends BaseRequest<DevModel> {

    public GetDevInfoRequest() {
        setRequestPath("/devs/getDevInfo");
    }
}
