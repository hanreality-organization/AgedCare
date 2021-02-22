package com.punuo.sys.app.agedcare.request;

import com.punuo.sys.sdk.httplib.BaseRequest;

/**
 * Created by han.chen.
 * Date on 2019-06-12.
 **/
public class AddCommentRequest extends BaseRequest<String> {

    public AddCommentRequest() {
        setRequestType(RequestType.GET);
        setRequestPath("/posts/addComments");
    }
}
