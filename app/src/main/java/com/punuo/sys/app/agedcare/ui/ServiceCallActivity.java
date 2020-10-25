package com.punuo.sys.app.agedcare.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.zxing.common.Runnable;
import com.punuo.sys.app.agedcare.R;
import com.punuo.sys.app.agedcare.http.GetPostUtil;
import com.punuo.sys.app.agedcare.model.Constant;
import com.punuo.sys.app.agedcare.sip.SipInfo;
import com.punuo.sys.app.agedcare.view.CircleImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Method;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ServiceCallActivity extends HindebarActivity implements View.OnClickListener {
   @Bind(R.id.jiazheng)
   CircleImageView jiazheng;
    @Bind(R.id.wuye)
    CircleImageView wuye;
    @Bind(R.id.dingcan)
    CircleImageView dingcan;
    String item_jiazheng;
    String item_wuye;
    String item_dingcan;
    String housekeep;
    String orderfood;
    String property;
    public static final int UPDATECALLNUMBER=1;
    private Handler handler = new Handler()
    {

    };
    java.lang.Runnable runnable =new java.lang.Runnable() {
        @Override
        public void run() {
            try {
                // 延迟5秒后自动挂断电话
                // 首先拿到TelephonyManager
                TelephonyManager telMag = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                Class<TelephonyManager> c = TelephonyManager.class;

                // 再去反射TelephonyManager里面的私有方法 getITelephony 得到 ITelephony对象
                Method mthEndCall = c.getDeclaredMethod("getITelephony", (Class[]) null);
                //允许访问私有方法
                mthEndCall.setAccessible(true);
                final Object obj = mthEndCall.invoke(telMag, (Object[]) null);

                // 再通过ITelephony对象去反射里面的endCall方法，挂断电话
                Method mt = obj.getClass().getMethod("endCall");
                //允许访问私有方法
                mt.setAccessible(true);
                mt.invoke(obj);
                Toast.makeText(ServiceCallActivity.this, "挂断电话！", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_call);
        ButterKnife.bind(this);
        jiazheng.setOnClickListener(this);
        wuye.setOnClickListener(this);
        dingcan.setOnClickListener(this);
        EventBus.getDefault().register(this);
//        SharedPreferences preferences1 = getSharedPreferences("data", MODE_PRIVATE);
//        item_jiazheng = preferences1.getString("家政电话", "");
//        SharedPreferences preferences2 = getSharedPreferences("data", MODE_PRIVATE);
//        item_wuye = preferences2.getString("物业电话", "");
//        SharedPreferences preferences3 = getSharedPreferences("data", MODE_PRIVATE);
//        item_dingcan = preferences3.getString("订餐电话", "");
        new  Thread(getservicenumber).start();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.jiazheng:
                call(housekeep);
                break;
            case R.id.wuye:
                call(property);
                break;
            case R.id.dingcan:
                call(orderfood);
            default:
                break;
        }
    }
    private void call( String item) {

        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + item);
        intent.setData(data);
        startActivity(intent);
//        handler.postDelayed(runnable,10*1000);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        switch (event.getMessage())
        {

            case "callend":
                Log.e("calling","..");
                handler.removeCallbacks(runnable);

                break;
            default:
                break;
        }

    }

    String response = "";
    private java.lang.Runnable getservicenumber = new java.lang.Runnable() {
        @Override
        public void run() {
            response = GetPostUtil.sendGet1111(Constant.URL_getservicenumber, "devid=" + SipInfo.devId);
            Log.e("getservicenumber ;",response);
            JSONArray jsonArray = JSONObject.parseArray(response);
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                housekeep = jsonObject2.getString("housekeep");
                orderfood = jsonObject2.getString("orderfood");
                property = jsonObject2.getString("property");
            }
            Message message = new Message();
            message.what = UPDATECALLNUMBER;
            handler.sendMessage(message);


        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
        finish();
    }
}
