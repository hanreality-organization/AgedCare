package com.punuo.sys.app.agedcare.request;

import com.punuo.sys.app.agedcare.request.model.DevModel;
import com.punuo.sys.sdk.httplib.BaseRequest;

/**
 * Created by han.chen.
 * Date on 2021/2/17.
 **/
public class GetDevInfoRequest extends BaseRequest<DevModel> {

    public GetDevInfoRequest() {
        setRequestPath("/devs/getDevInfo");
    }
}
