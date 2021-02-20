package com.punuo.sys.app.agedcare.request;

import com.punuo.sys.app.agedcare.request.model.MusicType;
import com.punuo.sys.sdk.httplib.BaseRequest;

import java.util.List;

/**
 * Created by han.chen.
 * Date on 2021/2/20.
 **/
public class GetAllMusicTypeRequest extends BaseRequest<List<MusicType>> {

    public GetAllMusicTypeRequest() {
        setRequestPath("/music/getMusicType");
    }
}
