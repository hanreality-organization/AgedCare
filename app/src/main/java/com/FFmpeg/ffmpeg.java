package com.FFmpeg;


public class ffmpeg {

    public ffmpeg() {
        load();
    }

    public int init(int width, int height) {
        return Init(width, height);
    }

    public void load() {
        try {
            System.loadLibrary("ffmpeg");
            System.loadLibrary("myffmpeg");
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    public void close() {
        Destroy();
    }

    /**
     * 初始化
     * @param width
     * @param height
     * @return
     */
    public native int Init(int width, int height);

    public native int Destroy();

    /**
     * 解码
     * @param in 输入
     * @param insize 输入数组的长度
     * @param out 输出
     * @return
     */
    public native int DecoderNal(byte[] in, int insize, byte[] out);

}