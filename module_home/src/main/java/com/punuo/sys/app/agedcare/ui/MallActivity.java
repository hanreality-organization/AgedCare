package com.punuo.sys.app.agedcare.ui;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import com.punuo.sys.app.agedcare.R;
import com.punuo.sys.sdk.util.NetUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import static com.punuo.sys.app.agedcare.sip.SipInfo.format;

public class MallActivity extends HindebarActivity{
    public Button back;
    private TextView tv_time;
    public TextView net;
    public Button web_back;
    public Button web_forward;
    private  final int msgKey1 = 1;
    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mall);
        webView=(WebView)findViewById(R.id.wv);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setSupportZoom(true);
// 设置出现缩放工具
        webView.getSettings().setBuiltInZoomControls(true);
//扩大比例的缩放
        webView.getSettings().setUseWideViewPort(true);
//自适应屏幕
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.loadUrl("http://sip.qinqingonline.com:8888/shop_war2");//此处填链接
        tv_time = (TextView) findViewById(R.id.time);
        net=(TextView)findViewById(R.id.net);
        back=(Button)findViewById(R.id.web_mall_back);
        web_back=(Button)findViewById(R.id.web_back);
        web_forward=(Button)findViewById(R.id.web_forward);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               finish();
            }
        });
        web_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("title","back");
                if (webView.canGoBack()) {
                    webView.goBack();//返回上个页面

                } else {
                    finish();
                }
            }
        });
        web_forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webView.canGoForward())
                {
                    webView.goForward();
                }
            }
        });
        new MallActivity.TimeThread().start();
        if(NetUtils.getNetWorkState(getApplicationContext())==NetUtils.NETWORK_MOBILE)
        {
            net.setText(R.string.fourG);
        }else if (NetUtils.getNetWorkState(getApplicationContext())==NetUtils.NETWORK_WIFI){
            net.setText(R.string.wifi);
        }else if (NetUtils.getNetWorkState(getApplicationContext())==NetUtils.NETWORK_NONE){
            net.setText("无网络");
        }else {
            net.setText("有线网");
        }

    }


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
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

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
            return false;
        }
    });
    public boolean isWifiAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)this.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected() && networkInfo
                .getType() == ConnectivityManager.TYPE_WIFI);
    }

    public boolean is4GAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            TelephonyManager telephonyManager = (TelephonyManager)
                    this.getSystemService(
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
