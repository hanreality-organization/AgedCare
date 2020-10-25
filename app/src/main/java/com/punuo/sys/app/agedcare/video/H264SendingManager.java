package com.punuo.sys.app.agedcare.video;

import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.os.StrictMode;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.punuo.sys.app.agedcare.groupvoice.G711;
import com.punuo.sys.app.agedcare.sip.SipInfo;
import com.punuo.sys.app.agedcare.tools.AECManager;
import com.punuo.sys.app.agedcare.tools.AvcEncoder;

import java.io.IOException;
import java.util.List;


/**
 * Author chzjy
 * Date 2016/12/19.
 */
public class H264SendingManager implements SurfaceHolder.Callback, Camera.PreviewCallback {
    SurfaceView h264suf;
    SurfaceHolder m_surfaceHolder;
    public static RTPSending rtpsending = null;
    private String TAG = H264SendingManager.class.getSimpleName();    //取得类名
    public static boolean G711Running = true;
    int frameSizeG711 = 160;
    private final int previewFrameRate = 10;  //演示帧率
    private final int previewWidth = 640;     //水平像素352
    private final int previewHeight = 480;     //垂直像素288
//    private final int previewWidth = 352;     //水平像素352
//    private final int previewHeight = 288;     //垂直像素288
    private AvcEncoder avcEncoder;
    Camera mCamera;
    private int numCamera;
    private int cameraId_front = -1;
    private int cameraId_back = -1;
    //外置摄像头的Id
    private int cameraId_out = -1;
    private boolean frontExist = false;
    byte[] rtppkt = new byte[VideoInfo.divide_length + 2];
    private boolean isStop = false;
    private long time = System.currentTimeMillis();   //以毫秒形式返回当前系统时间
    private int cameraState = 0;
    Camera.Parameters parame;
    private byte[] spsandpps={0x00,0x00,0x00,0x01,0x67,0x42,0x00,0x29,(byte) 0x8d,(byte) 0x8d,0x40,(byte) 0x50,0x1e,(byte)0xd0,0x0f,0x08,(byte)0x84,0x53,(byte)0x80,0x00,0x00,0x00,0x01,0x68,(byte) 0xca,0x43,(byte) 0xc8};
    public H264SendingManager(SurfaceView h264suf) {
        this.h264suf = h264suf;
    }
    boolean sendppsandsps=true;

    public void parameters(Camera camera) {
        List<Camera.Size> pictureSizes = camera.getParameters().getSupportedPictureSizes();
        List<Camera.Size> previewSizes = camera.getParameters().getSupportedPreviewSizes();
        Camera.Size psize;
        for (int i = 0; i < pictureSizes.size(); i++) {
            psize = pictureSizes.get(i);
            Log.i("pictureSize",psize.width+" x "+psize.height);
        }
        for (int i = 0; i < previewSizes.size(); i++) {
            psize = previewSizes.get(i);
            Log.i("previewSize",psize.width+" x "+psize.height);
        }
    }
    
    public void init() {

        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        if (rtpsending != null) {
            rtpsending = null;
        }
        rtpsending = new RTPSending();
        // 得到SurfaceHolder对象
        SurfaceHolder holder = h264suf.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        //设置回调函数
        holder.addCallback(H264SendingManager.this);   //添加回调接口
        //设置风格
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        long Ssrc = (VideoInfo.media_info_magic[15] & 0x000000ff) | ((VideoInfo.media_info_magic[14] << 8) & 0x0000ff00) | ((VideoInfo.media_info_magic[13] << 16) & 0x00ff0000) | ((VideoInfo.media_info_magic[12] << 24) & 0xff000000);
        rtpsending.rtpSession2.setSsrc(Ssrc);
        G711Running = true;
        G711_recored();
        VideoInfo.handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                G711Running = true;
                G711_recored();
            }
        };
        Log.d(TAG, "创建成功");

        avcEncoder = new AvcEncoder();
        SipInfo.flag = false;
    }

    /**
     * g711采集编码线程
     */
    private void G711_recored() {
        new Thread(G711_encode).start();
    }

    Runnable G711_encode = new Runnable() {
        public void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO);
            AudioRecord record = getAudioRecord();
            //int frame_size = 160;
            short[] audioData = new short[frameSizeG711];
            byte[] encodeData = new byte[frameSizeG711];
            int numRead = 0;

            while (G711Running) {
                numRead = record.read(audioData, 0, frameSizeG711);
                if (numRead <= 0) continue;
                calc2(audioData, 0, numRead);
                //进行pcmu编码
                G711.linear2ulaw(audioData, 0, encodeData, numRead);
                
                if(rtpsending == null)break;
                
                rtpsending.rtpSession2.payloadType(0x45);
                rtpsending.rtpSession2.sendData(encodeData);
            }
            record.stop();
            record.release();
            Log.i("zlj", "G711_encode stopped!");
        }
    };

    void calc2(short[] lin, int off, int len) {
        int i, j;

        for (i = 0; i < len; i++) {
            j = lin[i + off];
            lin[i + off] = (short) (j >> 1);
        }
    }

    /**
     * 取得音频采集对象引用
     */
    private AudioRecord getAudioRecord() {
        int samp_rate = 8000;
        int min = AudioRecord.getMinBufferSize(samp_rate,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        Log.e(TAG, "min buffer size:" + min);

        AudioRecord record = null;
        record = new AudioRecord(
                MediaRecorder.AudioSource.MIC,//the recording source
                samp_rate, //采样频率，一般为8000hz/s
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                min);
        if(AECManager.isDeviceSupport()){
            AECManager.getInstance().initAEC(record.getAudioSessionId());
        }
        record.startRecording();
        return record;
    }

    public void deInit() {
        AECManager.getInstance().release();
        VideoInfo.handler = null;
        if (mCamera != null)  //没有背面摄像头的情况
        {
            mCamera.setPreviewCallback(null);//must do this，停止接收回叫信号
            mCamera.stopPreview();   //停止捕获和绘图
            mCamera.release();   //断开与摄像头的连接，并释放摄像头资源
            mCamera = null;
        }
        avcEncoder.close();
        rtpsending = null;
        SipInfo.flag = true;
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.e(TAG, "surfaceCreated: ");
        m_surfaceHolder = holder;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.e(TAG, "surfaceChanged: ");
        numCamera = Camera.getNumberOfCameras();
        Log.i(TAG, "摄像头个数为" + numCamera);
        Camera.CameraInfo info = new Camera.CameraInfo();
        for (int i = 0; i < numCamera; i++) {
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId_back = i;     //获取后置摄像头的Id
            } else if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId_front = i;    //获取前置摄像头的Id
                frontExist = true;
            } else {
                cameraId_out = i;
            }
        }
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);//停止接收回调信号
            mCamera.stopPreview();//停止捕获和绘图
            mCamera.release();
            mCamera = null;
        }
        try {
            mCamera = Camera.open(frontExist ? cameraId_front : cameraId_out);
            cameraState = cameraId_out;
        } catch (Exception e) {
            try {
                mCamera = Camera.open(cameraId_back);
                cameraState = cameraId_back;
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        try {
            if (mCamera == null) return;

            parameters(mCamera);
            mCamera.setPreviewDisplay(m_surfaceHolder);
            mCamera.setPreviewCallback(this);
            mCamera.setDisplayOrientation(0);
            parame = mCamera.getParameters();    //获取配置参数对象
            parame.setPreviewFrameRate(previewFrameRate);    //设置Camera的演示帧率
            parame.setPreviewFormat(ImageFormat.YV12);
            parame.setPreviewSize(previewWidth, previewHeight);    //设置屏幕分辨率
            parame.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            //android2.3.3以后无需下步
            mCamera.setParameters(parame);
            //开始对演示帧进行捕获和绘图到surface
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.e(TAG, "surfaceDestroyed: ");
    }


    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        Log.i(TAG, "网络连接状况:" + (SipInfo.isNetworkConnected ? "正常" : "不正常"));
        Log.e(TAG, "onPreviewFrame: " + data.length);
        if (SipInfo.isNetworkConnected) {
            if (VideoInfo.endView) {    //收到BYE命令，关闭当前视频采集功能，重新回到注册之后的等待邀请界面
                VideoInfo.nalfirst = 0; //0表示未收到首包，1表示收到
                VideoInfo.index = 0;
                VideoInfo.query_response = false;
                VideoInfo.endView = false;
                G711Running = false;
                isStop = true;
                H264SendingManager.this.deInit();
            }
            if (!isStop) {
//                硬解码
                byte[] encodeResult = avcEncoder.offerEncoder(data); //进行编码，将编码结果存放进数组

                if (encodeResult != null && encodeResult.length > 0) {
                    Log.e(TAG, "encode len:" + encodeResult.length);//打印编码结果的长度
                    setSSRC_PAYLOAD();
                    DivideAndSendNal(encodeResult);
                }
            }
        } else {
            VideoInfo.nalfirst = 0; //0表示未收到首包，1表示收到
            VideoInfo.index = 0;
            VideoInfo.query_response = false;
            isStop = true;
            G711Running = false;
            H264SendingManager.this.deInit();
        }
    }

    /**
     * 设置ssrc与payload
     */
    public void setSSRC_PAYLOAD() {
        byte msg[] = new byte[20];
        long Ssrc = 0;
        msg[0] = 0x00;
        msg[1] = 0x01;
        msg[2] = 0x00;
        msg[3] = 0x10;
        try {
            System.arraycopy(VideoInfo.media_info_magic, 0, msg, 4, 16);  //生成RTP心跳保活包，即在Info.media_info_megic之前再加上0x00 0x01 0x00 0x10
        } catch (Exception e) {
            Log.d("ZR", "System.arraycopy failed!");
        }
        rtpsending.rtpSession1.payloadType(0x7a);    //设置RTP包的负载类型为0x7a

        //取Info.media_info_megic的后四组设为RTP的同步源码（Ssrc）
        Ssrc = (VideoInfo.media_info_magic[15] & 0x000000ff) | ((VideoInfo.media_info_magic[14] << 8) & 0x0000ff00) | ((VideoInfo.media_info_magic[13] << 16) & 0x00ff0000) | ((VideoInfo.media_info_magic[12] << 24) & 0xff000000);
        rtpsending.rtpSession1.setSsrc(Ssrc);
        for (int i = 0; i < 2; i++) {
            rtpsending.rtpSession1.sendData(msg);
        }
    }

    /**
     * 分片、发送方法
     */
    public void DivideAndSendNal(byte[] h264) {

        if (h264.length > 0) {  //有数据才进行分片发送操作
            if (h264.length > VideoInfo.divide_length) {
                VideoInfo.dividingFrame = true;
                VideoInfo.status = true;
                VideoInfo.firstPktReceived = false;
                VideoInfo.pktflag = 0;

                while (VideoInfo.status) {
                    if (!VideoInfo.firstPktReceived) {  //首包
                        sendFirstPacket(h264);
                    } else {
                        if (h264.length - VideoInfo.pktflag > VideoInfo.divide_length) {  //中包
                            sendMiddlePacket(h264);
                        } else {   //末包
                            sendLastPacket(h264);
                        }
                    } //end of 首包
                }//end of while
            } else {   //不分片包
                sendCompletePacket(h264);
            }
        }
    }

    /**
     * 发送首包
     */
    public void sendFirstPacket(byte[] h264) {
        Log.d("H264Sending", "发送首包");
        rtppkt[0] = (byte) (h264[0] & 0xe0);
        rtppkt[0] = (byte) (rtppkt[0] + 0x1c);
        rtppkt[1] = (byte) (0x80 + (h264[0] & 0x1f));
        rtpsending.rtpSession1.payloadType(0x62);

        //发送打包数据
        if(sendppsandsps) {
            for (int i = 0; i < 3; i++) {
                rtpsending.rtpSession1.sendData(spsandpps, 936735038);
            }//发送打包数据
            sendppsandsps=false;
        }
        try {
            System.arraycopy(h264, 0, rtppkt, 2, VideoInfo.divide_length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        VideoInfo.pktflag = VideoInfo.pktflag + VideoInfo.divide_length;
        VideoInfo.firstPktReceived = true;
        //设置RTP包的负载类型为0x62
        rtpsending.rtpSession1.payloadType(0x62);
        //发送打包数据
        rtpsending.rtpSession1.sendData(rtppkt);   //发送打包数据
        try {
            Thread.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送中包
     */
    public void sendMiddlePacket(byte[] h264) {
        Log.d("H264Sending", "发送中包");
        rtppkt[0] = (byte) (h264[0] & 0xe0);
        rtppkt[0] = (byte) (rtppkt[0] + 0x1c);
        rtppkt[1] = (byte) (0x00 + (h264[0] & 0x1f));

        try {
            System.arraycopy(h264, VideoInfo.pktflag, rtppkt, 2, VideoInfo.divide_length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        VideoInfo.pktflag = VideoInfo.pktflag + VideoInfo.divide_length;
        //设置RTP包的负载类型为0x62
        rtpsending.rtpSession1.payloadType(0x62);
        //发送打包数据
        rtpsending.rtpSession1.sendData(rtppkt);   //发送打包数据   //发送打包数据
        try {
            Thread.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送末包
     */
    public static void sendLastPacket(byte[] h264) {
        Log.d("H264Sending", "发送末包");
        byte[] rtppktLast = new byte[h264.length - VideoInfo.pktflag + 2];
        rtppktLast[0] = (byte) (h264[0] & 0xe0);
        rtppktLast[0] = (byte) (rtppktLast[0] + 0x1c);
        rtppktLast[1] = (byte) (0x40 + (h264[0] & 0x1f));
        try {
            System.arraycopy(h264, VideoInfo.pktflag, rtppktLast, 2, h264.length - VideoInfo.pktflag);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //设置RTP包的负载类型为0x62
        rtpsending.rtpSession1.payloadType(0x62);
        //发送打包数据
        rtpsending.rtpSession1.sendData(rtppktLast);   //发送打包数据  //发送打包数据
        try {
            Thread.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        VideoInfo.status = false;  //打包组包结束，下一步进行解码
        VideoInfo.dividingFrame = false;  //一帧分片打包完毕，时间戳改下一帧
    }

    /**
     * 发送完整包
     */
    public void sendCompletePacket(byte[] h264) {
        Log.d("H264Sending", "发送单包");
        //设置RTP包的负载类型为0x62
        rtpsending.rtpSession1.payloadType(0x62);
        //发送打包数据
        rtpsending.rtpSession1.sendData(h264);   //发送打包数据   //发送打包数据
        try {
            Thread.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
