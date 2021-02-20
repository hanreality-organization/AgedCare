package com.punuo.sys.app.agedcare.request;

import com.punuo.sys.app.agedcare.request.model.MusicItem;
import com.punuo.sys.sdk.httplib.BaseRequest;

import java.util.List;

/**
 * Created by han.chen.
 * Date on 2021/2/20.
 **/
public class GetMusicListRequest extends BaseRequest<List<MusicItem>> {

    public GetMusicListRequest() {
        setRequestPath("/music/getMusicList");
    }
}
