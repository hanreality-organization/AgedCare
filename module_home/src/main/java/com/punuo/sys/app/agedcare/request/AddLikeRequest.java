package com.punuo.sys.app.agedcare.request;

import com.punuo.sys.sdk.httplib.BaseRequest;

/**
 * Created by han.chen.
 * Date on 2019-06-11.
 **/
public class AddLikeRequest extends BaseRequest<String> {

    public AddLikeRequest() {
        setRequestType(RequestType.GET);
        setRequestPath("/posts/addLikes");
    }
}
