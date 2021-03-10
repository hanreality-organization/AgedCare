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
public class H264VideoDecoder {
    private static final String TAG = "H264VideoDecoder";
    private MediaCodec mCodec;
    private final static String MIME_TYPE = "video/avc";
    private final static int VIDEO_WIDTH = 648;
    private final static int VIDEO_HEIGHT = 480;
    private final static int TIME_INTERNAL = 10;
    private int mCount = 0;

    private static H264VideoDecoder sH264VideoDecoder;
    public static H264VideoDecoder getInstance() {
        synchronized (H264VideoDecoder.class) {
            if (sH264VideoDecoder == null) {
                sH264VideoDecoder = new H264VideoDecoder();
            }
        }
        return sH264VideoDecoder;
    }

    public void initDecoder(Surface surface) {
        try {
            mCodec = MediaCodec.createDecoderByType(MIME_TYPE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        MediaFormat mediaFormat = MediaFormat.createVideoFormat(MIME_TYPE, VIDEO_WIDTH, VIDEO_HEIGHT);
        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible);
        mCodec.configure(mediaFormat, surface, null, 0);
        mCodec.start();
        Log.i(TAG, "MediaCodec init success");
    }

    public boolean onFrame(byte[] buf, int offset, int length) {
        ByteBuffer[] inputBuffers = mCodec.getInputBuffers();
        int inputBufferIndex = mCodec.dequeueInputBuffer(-1);
        if (inputBufferIndex >= 0) {
            ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
            inputBuffer.clear();
            inputBuffer.put(buf, offset, length);
            mCodec.queueInputBuffer(inputBufferIndex, 0, length, mCount * TIME_INTERNAL, 0);
            mCount++;
        } else {
            return false;
        }
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        int outputBufferIndex = mCodec.dequeueOutputBuffer(bufferInfo, 0);
        while (outputBufferIndex >= 0) {
            mCodec.releaseOutputBuffer(outputBufferIndex, true);
            outputBufferIndex = mCodec.dequeueOutputBuffer(bufferInfo, 0);
        }
        return true;
    }

    public void stopCodec() {
        try {
            mCodec.stop();
            mCodec.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mCodec = null;
        sH264VideoDecoder = null;
        Log.i(TAG, "MediaCodec release success");
    }
}
