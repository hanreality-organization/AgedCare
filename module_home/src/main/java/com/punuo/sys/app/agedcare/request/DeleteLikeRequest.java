package com.punuo.sys.app.agedcare.request;

import com.punuo.sys.sdk.httplib.BaseRequest;

/**
 * Created by han.chen.
 * Date on 2019-06-12.
 **/
public class DeleteLikeRequest extends BaseRequest<String> {
    public DeleteLikeRequest() {
        setRequestType(RequestType.GET);
        setRequestPath("/posts/deleteLikes");
    }
}
