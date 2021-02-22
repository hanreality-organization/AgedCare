package com.punuo.sys.app.agedcare.request;

import com.punuo.sys.app.agedcare.model.ShortMovie;
import com.punuo.sys.sdk.httplib.BaseRequest;

import java.util.List;

/**
 * Created by han.chen.
 * Date on 2021/2/20.
 **/
public class GetVideoListRequest extends BaseRequest<List<ShortMovie>> {

    public GetVideoListRequest() {
        setRequestPath("/video/getVideoList");
    }
}
