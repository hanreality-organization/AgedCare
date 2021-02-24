package com.punuo.sys.app.agedcare.tools;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import tech.shutu.jni.YuvUtils;

/**
 * Created by asus on 2017/6/15.
 */

public class AvcEncoder {
    private static final String TAG = "AvcEncoder";
    private MediaCodec mediaCodec;
    private BufferedOutputStream outputStream;
    private byte[] yuv420 = null;
    public byte[] outPut=null;
//    private final int previewWidth = 352;     //水平像素352
//    private final int previewHeight = 288;     //垂直像素
    private final int previewWidth = 640;     //水平像素352
    private final int previewHeight = 480;     //垂直像素
    YuvUtils yuvPic=new YuvUtils();
    public AvcEncoder() {
        //输出到本地
        File f = new File(Environment.getExternalStorageDirectory(), "DCIM/video_encoded.264");
        yuv420 = new byte[previewWidth*previewHeight*3/2];
        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(f));
            Log.i("AvcEncoder", "outputStream initialized");
        } catch (Exception e){
            e.printStackTrace();
        }
        try {
            mediaCodec = MediaCodec.createEncoderByType("video/avc");
        } catch (IOException e) {
            e.printStackTrace();
        }
        YuvUtils.allocateMemo(previewWidth*previewHeight*3/2,0,previewWidth*previewHeight*3/2);
        MediaFormat mediaFormat = MediaFormat.createVideoFormat("video/avc",previewWidth, previewHeight);
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 1000000);
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 20);
        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar);
        //COLOR_FormatYUV420SemiPlanar  s9
        //COLOR_FormatYUV420Planar      s6
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
        mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mediaCodec.start();
    }

    public void close() {
        try {
            mediaCodec.stop();
            mediaCodec.release();
            yuvPic.releaseMemo();
//            outputStream.flush();
//            outputStream.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    int mCount=0;

    // called from Camera.setPreviewCallbackWithBuffer(...) in other class
    public byte[] offerEncoder(byte[] input) {
        Log.i("AvcEncoder", "offerEncoder: ");
        swapYV12toI420(input,previewWidth,previewHeight);
        input = i420bytes;

        try {
            ByteBuffer[] inputBuffers = mediaCodec.getInputBuffers();
            ByteBuffer[] outputBuffers = mediaCodec.getOutputBuffers();
            int inputBufferIndex = mediaCodec.dequeueInputBuffer(-1);
            if (inputBufferIndex >= 0) {
                ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
                inputBuffer.clear();
                inputBuffer.put(input);
                mediaCodec.queueInputBuffer(inputBufferIndex, 0, input.length,  mCount * 1000000 / 15, 0);
                mCount++;
            }

            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo,0);
            System.out.println("outputBufferIndex = " + outputBufferIndex);
            Log.i(TAG, "outputFirst");

            ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
            outPut= new byte[bufferInfo.size];
            System.out.println("outData = " + outPut.length);

            outputBuffer.get(outPut);
            //输出到文件
            outputStream.write(outPut, 0, outPut.length);
            Log.i("AvcEncoder", outPut.length + " bytes written");
            mediaCodec.releaseOutputBuffer(outputBufferIndex, false);
            System.out.println("outputEnd");
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return outPut;
    }

    private void NV21ToNV12(byte[] nv21,byte[] nv12,int width,int height){
        if(nv21 == null || nv12 == null)return;
        int framesize = width*height;
        int i = 0,j = 0;
        System.arraycopy(nv21, 0, nv12, 0, framesize);
        for(i = 0; i < framesize; i++){
            nv12[i] = nv21[i];
        }
        for (j = 0; j < framesize/2; j+=2)
        {
            nv12[framesize + j-1] = nv21[j+framesize];
        }
        for (j = 0; j < framesize/2; j+=2)
        {
            nv12[framesize + j] = nv21[j+framesize-1];
        }
    }

    byte[] i420bytes = null;
    private byte[] swapYV12toI420(byte[] yv12bytes, int width, int height) {
        if (i420bytes == null)
            i420bytes = new byte[yv12bytes.length];
        for (int i = 0; i < width*height; i++)
            i420bytes[i] = yv12bytes[i];
        for (int i = width*height; i < width*height + (width/2*height/2); i++)
            i420bytes[i] = yv12bytes[i + (width/2*height/2)];
        for (int i = width*height + (width/2*height/2); i < width*height + 2*(width/2*height/2); i++)
            i420bytes[i] = yv12bytes[i - (width/2*height/2)];
        return i420bytes;
    }
    /**
     * NV21->YUV420SP
     * @param input
     * @param output
     * @param width
     * @param height
     */
    private void swapNV21to420sp(byte[] input, byte[] output,int width, int height)
    {
        //复制Y
        System.arraycopy(input, 0, output, 0, width * height);

        //交换UV
        byte temp;
        for(int i = width*height;i<input.length;i += 2){
            output[i] = input[i+1];
            output[i+1] = input[i];
        }
    }
    /**
     * NV21->YUV420P  YYYYYYYY|VUVU -> YYYYYYYY|UU|VV
     * @param input
     * @param output
     * @param width
     * @param height
     */
    void swapNV21to420p(byte[] input, byte[] output, int width,int height) {

        int nLenY = width * height;
        int nLenU = nLenY / 4;

        //复制Y分量
        System.arraycopy(input, 0, output, 0, width * height);

        for (int i = 0; i < nLenU; i++) {
            output[nLenY + i] = input[nLenY + 2 * i + 1];
            output[nLenY + nLenU + i] = input[nLenY + 2 * i];
        }
    }



}
