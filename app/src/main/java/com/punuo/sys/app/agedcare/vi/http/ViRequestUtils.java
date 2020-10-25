package com.punuo.sys.app.agedcare.vi.http;

import android.app.Activity;

import com.punuo.sys.app.agedcare.vi.bean.TuLingResult;
import com.punuo.sys.app.agedcare.vi.bean.ViOnlineSongLrc;
import com.punuo.sys.app.agedcare.vi.bean.ViSearchSong;
import com.punuo.sys.app.agedcare.vi.bean.ViSongUrl;

import io.reactivex.Observer;
import okhttp3.RequestBody;

public class ViRequestUtils {
    public static void searchSong(Activity context, String key, Observer<ViSearchSong> observer) {
        ViRetrofitUtils.getViAPINService()
                .search(key, 1).compose(ViRxHelper.observableIO2Main(context))
                .subscribe(observer);
    }

    public static void getTuLing(Activity context, RequestBody requestBody, Observer<TuLingResult> observer) {
        ViRetrofitUtils.getViAPINService()
                .getTuling(ViAPI.TULING_API, requestBody).compose(ViRxHelper.observableIO2Main(context))
                .subscribe(observer);
    }

    // 请求播放链接
    public static void getSongPlayUrl(Activity context, String songmid, Observer<ViSongUrl> observer) {
        ViRetrofitUtils.getSongUrlServer()
                .getSongUrl(songmid).compose(ViRxHelper.observableIO2Main(context))
                .subscribe(observer);
    }

    // 请求歌词数据
    public static void getSongLrc(Activity context, String mid, Observer<ViOnlineSongLrc> observer) {
        ViRetrofitUtils.getSonglrc()
                .getOnlineSongLrc(mid).compose(ViRxHelper.observableIO2Main(context))
                .subscribe(observer);
    }
}