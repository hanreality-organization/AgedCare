package com.punuo.sip.dev;


import com.punuo.sip.dev.model.MediaData;

/**
 * Created by han.chen.
 * Date on 2019-09-18.
 **/
public class H264ConfigDev {
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
    public static int FRAME_RATE = 15;

    public static String rtpIp;
    public static int rtpPort;
    public static byte[] magic;

    public static void initMediaData(MediaData mediaData) {
        rtpIp = mediaData.getIp();
        rtpPort = mediaData.getPort();
        magic = mediaData.getMagic();
    }

    public static byte[] getMagic() {
        return magic;
    }
}
