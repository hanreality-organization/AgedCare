package com.punuo.sys.app.agedcare.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.common.BitmapUtils;
import com.punuo.sys.app.agedcare.R;
import com.punuo.sys.app.agedcare.ftp.Ftp;
import com.punuo.sys.app.agedcare.ftp.FtpListener;
import com.punuo.sys.app.agedcare.sip.SipInfo;
import com.punuo.sys.app.agedcare.tools.VersionXmlParse;
import com.punuo.sys.app.agedcare.update.AutoUpdateService;
import com.punuo.sys.app.agedcare.util.IntentUtil;
import com.punuo.sys.app.agedcare.view.CustomProgressDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.punuo.sys.app.agedcare.model.Constant.FTP_DOWN_LOADING;
import static com.punuo.sys.app.agedcare.model.Constant.FTP_DOWN_SUCCESS;

public class CodeActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.code_image)
    ImageView codeImage;
    @Bind(R.id.settingbutton)
    Button settingbutton;
    @Bind(R.id.update)
    Button update;
    @Bind(R.id.code_download)
    ImageView code_download;
    private final String TAG = getClass().getName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code);
        ButterKnife.bind(this);
        settingbutton.setOnClickListener(this);

        hideStatusBarNavigationBar();
        initView();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.settingbutton:
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                startActivity(intent);
                break;


        }
    }
    private void initView() {
        String content = SipInfo.devId;
        String str=SipInfo.userId;
        String Port=SipInfo.port;
        String downloadurl="http://sip.qinqingonline.com:8888/html/text.html";
        Bitmap bitmap1 ;
        Bitmap bitmap2;
        Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.codeload);

        try {
            bitmap1 = BitmapUtils.create2DCode(content+" "+Port+" "+str);
            codeImage.setImageBitmap(bitmap1);
            bitmap2=BitmapUtils.create2DCode(downloadurl);
            code_download.setImageBitmap(addLogo(bitmap2,logo));
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    //给二维码添加图片
    //第一个参数为原二维码，第二个参数为添加的logo
    private static Bitmap addLogo(Bitmap src, Bitmap logo) {
        //如果原二维码为空，返回空
        if (src ==null ) {
            return null;
        }
        //如果logo为空，返回原二维码
        if (logo ==null) {
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
            canvas.drawBitmap(logo, (srcWidth - logoWidth) / 2, (srcHeight - logoHeight) / 2,null );

            canvas.save();
            canvas.restore();
        } catch (Exception e) {
            bitmap = null;
            e.getStackTrace();
        }

        return bitmap;
    }

    public void hideStatusBarNavigationBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags( WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags( WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        }
    }
    @OnClick(R.id.update)
    public void onClick() {
        if (SipInfo.isNetworkConnected) {
            Intent intent = new Intent(this, AutoUpdateService.class);
            intent.putExtra("needToast", true);
            IntentUtil.startServiceInSafeMode(this, intent);
        } else {
            Toast.makeText(this, "当前无网络", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected int getLayoutRes() {
        return R.layout.activity_main;
    }
}
