package com.punuo.sys.app.agedcare.tools;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Environment;

import com.punuo.sip.dev.H264ConfigDev;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;

import static android.media.MediaCodec.BUFFER_FLAG_CODEC_CONFIG;
import static android.media.MediaCodec.BUFFER_FLAG_KEY_FRAME;

/**
 * Created by han.chen.
 * Date on 2021/2/24.
 **/
public class H264VideoEncoder {
    private static final String TAG = "H264VideoEncoder";
    private static H264VideoEncoder sH264VideoDecoder;
    private RTPVideoSendSession mRTPVideoSendSession;

    public static H264VideoEncoder getInstance() {
        synchronized (H264VideoEncoder.class) {
            if (sH264VideoDecoder == null) {
                sH264VideoDecoder = new H264VideoEncoder();
            }
        }
        return sH264VideoDecoder;
    }

    private MediaCodec mediaCodec;
    private int mWidth = 0;
    private int mHeight = 0;
    private int mFrameRate = 15;
    private final int TIMEOUT_USEC = 12000;
    public byte[] configByte;


    public static ArrayBlockingQueue<byte[]> YUVQueue = new ArrayBlockingQueue<>(10);

    public void initEncoder(int width, int height, int frameRate) {
        YUVQueue.clear();
        mWidth = width;
        mHeight = height;
        mFrameRate = frameRate;
        mRTPVideoSendSession = new RTPVideoSendSession(H264ConfigDev.rtpIp, H264ConfigDev.rtpPort);
        MediaFormat mediaFormat = MediaFormat.createVideoFormat("video/avc", width, height);
        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar);
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, width * height * 5);
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, mFrameRate);
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
        try {
            mediaCodec = MediaCodec.createEncoderByType("video/avc");
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mediaCodec.start();
//        createFile();
    }

    private BufferedOutputStream outputStream;

    private void createFile() {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/test1.h264");
        if (file.exists()) {
            file.delete();
        }
        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isRuning = false;

    public void startEncoderThread() {
        Thread EncoderThread = new Thread(() -> {
            isRuning = true;
            byte[] input = null;

            while (isRuning) {
                //访问MainActivity用来缓冲待解码数据的队列
                if (YUVQueue.size() > 0) {
                    //从缓冲队列中取出一帧
                    input = YUVQueue.poll();
                    byte[] yuv420sp = new byte[mWidth * mHeight * 3 / 2];
                    //把待编码的视频帧转换为YUV420格式
                    swapYV12toI420(input,yuv420sp, mWidth, mHeight);
                    input = yuv420sp;
                }
                if (input != null) {
                    try {
                        //编码器输入缓冲区
                        ByteBuffer[] inputBuffers = mediaCodec.getInputBuffers();
                        //编码器输出缓冲区
                        ByteBuffer[] outputBuffers = mediaCodec.getOutputBuffers();
                        int inputBufferIndex = mediaCodec.dequeueInputBuffer(-1);
                        if (inputBufferIndex >= 0) {
                            pts = computePresentationTime(generateIndex);
                            ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
                            inputBuffer.clear();
                            //把转换后的YUV420格式的视频帧放到编码器输入缓冲区中
                            inputBuffer.put(input);
                            mediaCodec.queueInputBuffer(inputBufferIndex, 0, input.length, pts, 0);
                            generateIndex++;
                        }

                        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
                        int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, TIMEOUT_USEC);
                        while (outputBufferIndex >= 0) {
                            ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
                            byte[] outData = new byte[bufferInfo.size];
                            outputBuffer.get(outData);
                            if (bufferInfo.flags == BUFFER_FLAG_CODEC_CONFIG) {
                                configByte = new byte[bufferInfo.size];
                                configByte = outData;
                            } else if (bufferInfo.flags == BUFFER_FLAG_KEY_FRAME) {
                                byte[] keyframe = new byte[bufferInfo.size + configByte.length];
                                System.arraycopy(configByte, 0, keyframe, 0, configByte.length);
                                //把编码后的视频帧从编码器输出缓冲区中拷贝出来
                                System.arraycopy(outData, 0, keyframe, configByte.length, outData.length);
                                mRTPVideoSendSession.divideAndSendNal(keyframe);
//                                outputStream.write(keyframe, 0, keyframe.length);
                            } else {
                                //写到文件中
                                mRTPVideoSendSession.divideAndSendNal(outData);
//                                outputStream.write(outData, 0, outData.length);
                            }

                            mediaCodec.releaseOutputBuffer(outputBufferIndex, false);
                            outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, TIMEOUT_USEC);
                        }
                        mRTPVideoSendSession.setRtpHeartBeatData();

                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                } else {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        EncoderThread.start();
    }


    long pts = 0;
    long generateIndex = 0;

    public void close() {
        YUVQueue.clear();
        isRuning = false;
        try {
            mediaCodec.stop();
            mediaCodec.release();
//            outputStream.flush();
//            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void NV21ToNV12(byte[] nv21, byte[] nv12, int width, int height) {
        if (nv21 == null || nv12 == null) return;
        int frameSize = width * height;
        int i = 0, j = 0;
        System.arraycopy(nv21, 0, nv12, 0, frameSize);
        for (i = 0; i < frameSize; i++) {
            nv12[i] = nv21[i];
        }
        for (j = 0; j < frameSize / 2; j += 2) {
            nv12[frameSize + j - 1] = nv21[j + frameSize];
        }
        for (j = 0; j < frameSize / 2; j += 2) {
            nv12[frameSize + j] = nv21[j + frameSize - 1];
        }
    }

    private byte[] swapYV12toI420(byte[] yv12bytes,byte[] i420bytes, int width, int height) {
        if (i420bytes == null)
            i420bytes = new byte[yv12bytes.length];
        for (int i = 0; i < width*height; i++)
            i420bytes[i] = yv12bytes[i];
        int size = width * height + (width / 2 * height / 2);
        for (int i = width*height; i < size; i++)
            i420bytes[i] = yv12bytes[i + (width/2*height/2)];
        for (int i = size; i < width*height + 2*(width/2*height/2); i++)
            i420bytes[i] = yv12bytes[i - (width/2*height/2)];
        return i420bytes;
    }

    private long computePresentationTime(long frameIndex) {
        return 132 + frameIndex * 1000000 / mFrameRate;
    }
}
