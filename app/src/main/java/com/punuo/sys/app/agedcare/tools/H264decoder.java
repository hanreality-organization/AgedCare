package com.punuo.sys.app.agedcare.tools;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.util.Log;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by asus on 2017/6/16.
 */
//硬编码
public class H264decoder {
    private static final String TAG = "H264decoder";
    private MediaCodec mCodec;
    private final static String MIME_TYPE = "video/avc"; // H.264 Advanced Video
//    private final static int VIDEO_WIDTH = 1280;
//    private final static int VIDEO_HEIGHT = 720;
    private final static int VIDEO_WIDTH = 640;
    private final static int VIDEO_HEIGHT = 480;
    private final static int TIME_INTERNAL = 15;
    private final static int HEAD_OFFSET=512;
    int mCount = 0;
    public void initDecoder(Surface surface) {
        try {
            mCodec = MediaCodec.createDecoderByType(MIME_TYPE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        MediaFormat mediaFormat = MediaFormat.createVideoFormat(MIME_TYPE,
                VIDEO_WIDTH, VIDEO_HEIGHT);
//        byte[] header_sps = new byte[]{0x00, 0x00, 0x00, 0x01, 0x67, 0x42, (byte) 0x80, 0x1F, (byte) 0xDA, 0x01, 0x40, 0x16, (byte) 0xE8, (byte) 0x06, (byte) 0xD0, (byte) 0xA1, (byte) 0x35};
//        byte[] header_pps = new byte[]{0x00, 0x00, 0x00, 0x01, 0x68, (byte) 0xCE, 0x06, (byte) 0xE2};
        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible);
//        mediaFormat.setByteBuffer("csd-0", ByteBuffer.wrap(header_sps));
//        mediaFormat.setByteBuffer("csd-1", ByteBuffer.wrap(header_pps));
        mCodec.configure(mediaFormat, surface, null, 0);
        mCodec.start();
    }
        /*
        1.获取一个可用的inputBuffer的索引(出列)
　　    2.将一帧数据放入inputBuffer
　　    3.将inputBuffer入列进行解码
　　    4.获得一个outputBuffer的索引(出列)
　　    5.释放outputBuffer
　　    6.在4,5间循环直到没有outputBuffer可出列为止
        */
    public boolean onFrame2(byte[] buf, int offset, int length) {
        Log.e("Media", "onFrame start");
        Log.e("Media", "onFrame Thread:" + Thread.currentThread().getName());
        // Get input buffer index
        ByteBuffer[] inputBuffers = mCodec.getInputBuffers();
        Log.e("Media", "onFrame mid"+inputBuffers.length);
        int inputBufferIndex = mCodec.dequeueInputBuffer(1000);//yuanlaiwei -1 -1daibiao wuxiandengdai
        Log.e("Media", "onFrame index:" + inputBufferIndex);
        if (inputBufferIndex >= 0) {
            ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
            inputBuffer.clear();
            inputBuffer.put(buf, offset, length);
            mCodec.queueInputBuffer(inputBufferIndex, 0, length, mCount
                    * TIME_INTERNAL, 0);
            mCount++;
        } else {
            return false;
        }
        // Get output buffer index
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        int outputBufferIndex = mCodec.dequeueOutputBuffer(bufferInfo, 0);
//        Log.e("Media", "onFrame index2:" + outputBufferIndex);
        while (outputBufferIndex >= 0) {
            mCodec.releaseOutputBuffer(outputBufferIndex, true);
            outputBufferIndex = mCodec.dequeueOutputBuffer(bufferInfo, 0);
        }
        Log.e("Media", "onFrame end");
        return true;
    }
    public boolean onFrame(byte[] buf, int offset, int length) {
        Log.e("Media", "onFrame start");
        Log.e("Media", "onFrame Thread:" + Thread.currentThread().getName());
        // Get input buffer index
        int inputBufferIndex = mCodec.dequeueInputBuffer(-1);//yuanlaiwei -1 -1daibiao wuxiandengdai
        Log.e("Media", "onFrame index:" + inputBufferIndex);
        if (inputBufferIndex >= 0) {
//            ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
            ByteBuffer inputBuffer = mCodec.getInputBuffer(inputBufferIndex);
            inputBuffer.clear();
            inputBuffer.put(buf, offset, length);
            mCodec.queueInputBuffer(inputBufferIndex, 0, length, mCount
                    * TIME_INTERNAL, 0);
            mCount++;
        } else {
            mCount++;
        }


        // Get output buffer index
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        int outputBufferIndex = mCodec.dequeueOutputBuffer(bufferInfo, 2000);
        while (outputBufferIndex >= 0) {
            mCodec.releaseOutputBuffer(outputBufferIndex, true);
            outputBufferIndex = mCodec.dequeueOutputBuffer(bufferInfo, 2000);
        }
        Log.e("Media", "onFrame end");
        return true;
    }
}
