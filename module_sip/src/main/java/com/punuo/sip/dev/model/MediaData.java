package com.punuo.sip.dev.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by han.chen.
 * Date on 2019-08-22.
 **/
public class MediaData {

    @SerializedName("peer")
    public String peer;

    @SerializedName("magic")
    public String magic = "";

    public String getIp() {
        String ip = "";
        try {
            ip = peer.substring(0, peer.indexOf("UDP")).trim();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ip;
    }

    public int getPort() {
        int port = 0;
        try {
            port = Integer.parseInt(peer.substring(peer.indexOf("UDP") + 3).trim());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return port;
    }

    public byte[] getMagic() {
        byte[] mediaInfoMagic = null;
        try {
            mediaInfoMagic = new byte[magic.length() / 2 + magic.length() % 2];
            for (int i = 0; i < mediaInfoMagic.length; i++) {
                mediaInfoMagic[i] = (byte) (0xff & Integer.parseInt(magic.substring(i * 2, i * 2 + 2), 16));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mediaInfoMagic;
    }
}
