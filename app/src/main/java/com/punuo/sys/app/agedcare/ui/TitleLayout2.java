package com.punuo.sys.app.agedcare.ui;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.punuo.sys.app.agedcare.R;
import com.punuo.sys.app.agedcare.tools.NetUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static com.punuo.sys.app.agedcare.sip.SipInfo.format;

/**
 * Created by 23578 on 2018/7/18.
 */

public class TitleLayout2 extends LinearLayout {
    private Button button;
    private Timer timer=new Timer();
    private TextView tv_time;
    private TextView net;
    private static final int msgKey1 = 1;
    private Context mcontext;
    public TitleLayout2(Context context, AttributeSet attributeSet)
    {
        super(context,attributeSet);
        LayoutInflater.from(context).inflate(R.layout.title2,this);
        tv_time = (TextView) findViewById(R.id.time);
        net=(TextView)findViewById(R.id.net);
        button=(Button)findViewById(R.id.back);
        mcontext=context;
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("title","back");

                    ((Activity)getContext()).finish();



            }
        });
        new TitleLayout2.TimeThread().start();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message=new Message();
                message.what=0;
                netCheckHandler.sendMessage(message);
            }},0,5000);

    }
    private Handler netCheckHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            if(message.what == 0){
                if(NetUtils.getNetWorkState(mcontext)==NetUtils.NETWORK_MOBILE)
                {
                    net.setText("4G");
                }else if (NetUtils.getNetWorkState(mcontext)==NetUtils.NETWORK_WIFI){
                    net.setText("WIFI");
                }else if (NetUtils.getNetWorkState(mcontext)==NetUtils.NETWORK_NONE){
                    net.setText("无网络");
                }else {
                    net.setText("有线网");
                }
            }
            return false;
        }
    });

    public class TimeThread extends Thread{
        @Override
        public void run() {
            super.run();
            do{
                try {
                    Thread.sleep(100);
                    Message msg = new Message();
                    msg.what = msgKey1;
                    mHandler.sendMessage(msg);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }while (true);
        }
    }
    private  Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case msgKey1:
                    long time = System.currentTimeMillis();
                    Date date = new Date(time);
                    format = new SimpleDateFormat("aa hh:mm");
                    tv_time.setText(format.format(date));
                    break;
                default:
                    break;
            }
        }
    };
    public boolean isWifiAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getContext().getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected() && networkInfo
                .getType() == ConnectivityManager.TYPE_WIFI);
    }

    public boolean is4GAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            TelephonyManager telephonyManager = (TelephonyManager)
                    getContext().getSystemService(
                            Context.TELEPHONY_SERVICE);
            int networkType = telephonyManager.getNetworkType();
            /** Current network is LTE */
            if (networkType == 13) {
                /**此时的网络是4G的*/
                return true;
            }
        }
        return false;
    }
//    public boolean isNetworkreachable() {
//        ConnectivityManager manager = (ConnectivityManager) getContext().getSystemService(CONNECTIVITY_SERVICE);
//        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
//        if (networkInfo == null) {
//            SipInfo.isNetworkConnected = false;
//        } else {
//            SipInfo.isNetworkConnected = networkInfo.getState() == NetworkInfo.State.CONNECTED;
//        }
//        return SipInfo.isNetworkConnected;
//    }
    /**
     * 判断以太网网络是否可用

     */
    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ethNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
//        NetworkInfo wifiNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);


        if (ethNetInfo != null && ethNetInfo.isConnected()) {
            return true;
        } else  {
            return false;
        }
    }
}

