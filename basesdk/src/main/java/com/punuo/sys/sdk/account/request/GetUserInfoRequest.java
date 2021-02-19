package com.punuo.sys.sdk.account.request;

import com.punuo.sys.sdk.account.model.PNUserInfo;
import com.punuo.sys.sdk.httplib.BaseRequest;

/**
 * Created by han.chen.
 * Date on 2019/5/28.
 **/
public class GetUserInfoRequest extends BaseRequest<PNUserInfo> {

    public GetUserInfoRequest() {
        setRequestType(RequestType.GET);
        setRequestPath("/users/getUserInfo");
    }
}
