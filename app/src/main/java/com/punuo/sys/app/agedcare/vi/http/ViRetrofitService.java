package com.punuo.sys.app.agedcare.vi.http;

import com.punuo.sys.app.agedcare.vi.bean.TuLingResult;
import com.punuo.sys.app.agedcare.vi.bean.ViOnlineSongLrc;
import com.punuo.sys.app.agedcare.vi.bean.ViSearchSong;
import com.punuo.sys.app.agedcare.vi.bean.ViSingerImg;
import com.punuo.sys.app.agedcare.vi.bean.ViSongLrc;
import com.punuo.sys.app.agedcare.vi.bean.ViSongUrl;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ViRetrofitService {
    // 图灵机器人
    @POST
    Observable<TuLingResult> getTuling(@Url String url, @Body RequestBody requestBody);

    // 搜索歌曲
    // URL Example ： https://c.y.qq.com/soso/fcgi-bin/client_search_cp?p=2&n=2&w=周杰伦&format=json
    @GET(ViAPI.SEARCH_SONG)
    Observable<ViSearchSong> search(@Query("w") String seek, @Query("p") int offset);

    // 歌曲播放地址
    // URL Example ： 变化的只有songmid，即{{}}所示
    /* https://u.y.qq.com/cgi-bin/musicu.fcg?format=json&data=%7B%22req_0%22%3A%7B%22module%22%3A%22vkey.GetVkeyServer%22%2C%22method%22%3A%22CgiGetVkey%22%2C%22param%22%3A%7B%22guid%22%3A%22358840384%22%2C%22       +
     * songmid%22%3A%5B%22{{003wFozn3V3Ra0}} +
     * %22%5D%2C%22songtype%22%3A%5B0%5D%2C%22uin%22%3A%221443481947%22%2C%22loginflag%22%3A1%2C%22platform%22%3A%2220%22%7D%7D%2C%22comm%22%3A%7B%22uin%22%3A%221443481947%22%2C%22format%22%3A%22json%22%2C%22ct%22%3A24%2C%22cv%22%3A0%7D%7D
     */
    @GET(ViAPI.SONG_URL)
    Observable<ViSongUrl> getSongUrl(@Query(value = "data", encoded = true) String data);

    // 根据songmid获取歌词
    // URL Example ： https://c.y.qq.com/lyric/fcgi-bin/fcg_query_lyric_new.fcg?songmid=000wocYU11tSzS&format=json&nobase64=1
    // Headers中的Referer是qq用来防盗链的
    @Headers(ViAPI.HEADER_REFERER)
    @GET(ViAPI.ONLINE_SONG_LRC)
    Observable<ViOnlineSongLrc> getOnlineSongLrc(@Query("songmid") String songId);

    // 搜索歌词
    // URL Example ： https://c.y.qq.com/soso/fcgi-bin/client_search_cp?p=1&n=1&w=说谎&format=json&t=7
    @GET(ViAPI.SONG_LRC)
    Observable<ViSongLrc> getLrc(@Query("w") String seek);

    // 歌手照片
    // URL Example ： 主要用于本地音乐：http://music.163.com/api/search/get/web?s=刘瑞琦&type=100
    @Headers(ViAPI.HEADER_USER_AGENT)
    @POST(ViAPI.SINGER_PIC)
    @FormUrlEncoded
    Observable<ViSingerImg> getSingerImg(@Field("s") String singer);
}