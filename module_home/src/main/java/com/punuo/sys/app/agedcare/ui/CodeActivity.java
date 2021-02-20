package com.punuo.sys.app.agedcare.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.google.zxing.common.BitmapUtils;
import com.punuo.sys.app.agedcare.R;
import com.punuo.sys.app.agedcare.R2;
import com.punuo.sys.app.agedcare.sip.SipInfo;
import com.punuo.sys.app.router.HomeRouter;
import com.punuo.sys.sdk.account.AccountManager;
import com.punuo.sys.sdk.activity.BaseActivity;
import com.punuo.sys.sdk.update.AutoUpdateService;
import com.punuo.sys.sdk.util.CommonUtil;
import com.punuo.sys.sdk.util.IntentUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
@Route(path = HomeRouter.ROUTER_CODE_ACTIVITY)
public class CodeActivity extends BaseActivity implements View.OnClickListener {
    private final String TAG = getClass().getName();
    @BindView(R2.id.code_image)
    ImageView codeImage;
    @BindView(R2.id.settingbutton)
    Button settingButton;
    @BindView(R2.id.update)
    Button update;
    @BindView(R2.id.code_download)
    ImageView codeDownload;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code);
        ButterKnife.bind(this);
        settingButton.setOnClickListener(this);

        initView();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.settingbutton) {
            Intent intent = new Intent(Settings.ACTION_SETTINGS);
            startActivity(intent);
        }
    }

    private void initView() {
        int size = (CommonUtil.getWidth() - CommonUtil.dip2px(30f) * 4 - CommonUtil.dip2px(60f) - CommonUtil.dip2px(60f)) / 4;
        codeImage.getLayoutParams().width = size;
        codeImage.getLayoutParams().height = size;
        settingButton.getLayoutParams().width = size;
        settingButton.getLayoutParams().height = size;
        update.getLayoutParams().width = size;
        update.getLayoutParams().height = size;
        codeDownload.getLayoutParams().width = size;
        codeDownload.getLayoutParams().height = size;
        String content = AccountManager.getDevId();
        String str = AccountManager.getUserId();
        String Port = AccountManager.getGroupPort();
        String downloadUrl = "http://sip.qinqingonline.com:8000/static/apk/phone_ap/rlph_app.apk";
        Bitmap bitmap1;
        Bitmap bitmap2;
        Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.codeload);
        try {
            bitmap1 = BitmapUtils.create2DCode(content + " " + Port + " " + str);
            codeImage.setImageBitmap(bitmap1);
            bitmap2 = BitmapUtils.create2DCode(downloadUrl);
            codeDownload.setImageBitmap(addLogo(bitmap2, logo));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //给二维码添加图片
    //第一个参数为原二维码，第二个参数为添加的logo
    private static Bitmap addLogo(Bitmap src, Bitmap logo) {
        //如果原二维码为空，返回空
        if (src == null) {
            return null;
        }
        //如果logo为空，返回原二维码
        if (logo == null) {
            return src;
        }

        //这里得到原二维码bitmap的数据
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();
        //logo的Width和Height
        int logoWidth = logo.getWidth();
        int logoHeight = logo.getHeight();

        //同样如果为空，返回空
        if (srcWidth == 0 || srcHeight == 0) {
            return null;
        }
        //同样logo大小为0，返回原二维码
        if (logoWidth == 0 || logoHeight == 0) {
            return src;
        }

        //logo大小为二维码整体大小的1/5，也可以自定义多大，越小越好
        //二维码有一定的纠错功能，中间图片越小，越容易纠错
        float scaleFactor = srcWidth * 1.0f / 5 / logoWidth;
        Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(src, 0, 0, null);
            canvas.scale(scaleFactor, scaleFactor, srcWidth / 2, srcHeight / 2);
            canvas.drawBitmap(logo, (srcWidth - logoWidth) / 2, (srcHeight - logoHeight) / 2, null);

            canvas.save();
            canvas.restore();
        } catch (Exception e) {
            bitmap = null;
            e.getStackTrace();
        }

        return bitmap;
    }

    @OnClick(R2.id.update)
    public void onClick() {
        if (SipInfo.isNetworkConnected) {
            Intent intent = new Intent(this, AutoUpdateService.class);
            intent.putExtra("needToast", true);
            IntentUtil.startServiceInSafeMode(this, intent);
        } else {
            Toast.makeText(this, "当前无网络", Toast.LENGTH_SHORT).show();
        }
    }
}
