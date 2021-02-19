package com.punuo.sip.user;

import com.punuo.sip.user.model.MediaData;
import com.punuo.sip.user.model.QueryResponse;

/**
 * Created by han.chen.
 * Date on 2019-09-18.
 **/
public class H264ConfigUser {

    /**
     * video width
     */
    public static int VIDEO_WIDTH = 640;

    /**
     * video height
     */
    public static int VIDEO_HEIGHT = 480;

    /**
     * video frame rate
     */
    public static int FRAME_RATE = 10;
    /**
     * video type
     */
    public static int VIDEO_TYPE = 2;
    /**
     * Default video rtmp address
     */
    public static String RTMP_STREAM = "rtmp://101.69.255.130:1936/hls/310023005801930001";

    public static String rtpIp = "101.69.255.134";
    public static int rtpPort;
    public static byte[] magic;

    public static String resolution;

    public static void initQueryData(QueryResponse queryData) {
        resolution = queryData.resolution;
        switch (queryData.resolution) {
            case "CIF":
                H264ConfigUser.VIDEO_WIDTH = 352;
                H264ConfigUser.VIDEO_HEIGHT = 288;
                H264ConfigUser.VIDEO_TYPE = 2;

                break;
            case "QCIF_MOBILE_SOFT":
                H264ConfigUser.VIDEO_WIDTH = 176;
                H264ConfigUser.VIDEO_HEIGHT = 144;
                H264ConfigUser.VIDEO_TYPE = 3;
                break;
            case "MOBILE_S6":
                H264ConfigUser.VIDEO_WIDTH = 320;
                H264ConfigUser.VIDEO_HEIGHT = 240;
                H264ConfigUser.VIDEO_TYPE = 4;
                break;
            case "MOBILE_S9":
                H264ConfigUser.VIDEO_WIDTH = 320;
                H264ConfigUser.VIDEO_HEIGHT = 240;
                H264ConfigUser.VIDEO_TYPE = 5;
                break;
            default:
                break;
        }
    }

    public static void initMediaData(MediaData mediaData) {
        rtpIp = mediaData.getIp();
        rtpPort = mediaData.getPort();
        magic = mediaData.getMagic();
    }

    public static byte[] getMagic() {
        return magic;
    }
}
