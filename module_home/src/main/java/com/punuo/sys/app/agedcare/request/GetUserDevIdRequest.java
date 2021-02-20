package com.punuo.sys.app.agedcare.request;

import com.punuo.sys.app.agedcare.request.model.UserDevModel;
import com.punuo.sys.sdk.httplib.BaseRequest;

/**
 * Created by han.chen.
 * Date on 2021/2/17.
 **/
public class GetUserDevIdRequest extends BaseRequest<UserDevModel> {

    public GetUserDevIdRequest() {
        setRequestPath("/devs/getUserDevId");
    }
}
