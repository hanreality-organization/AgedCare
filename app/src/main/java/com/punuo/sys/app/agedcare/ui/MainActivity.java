package com.punuo.sys.app.agedcare.ui;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.punuo.sys.app.agedcare.ProcessTasks;
import com.punuo.sys.app.agedcare.R;
import com.punuo.sys.app.agedcare.groupvoice.GroupInfo;
import com.punuo.sys.app.agedcare.http.GetPostUtil;
import com.punuo.sys.app.agedcare.model.Constant;
import com.punuo.sys.app.agedcare.sip.KeepAlive;
import com.punuo.sys.app.agedcare.sip.SipDev;
import com.punuo.sys.app.agedcare.sip.SipInfo;
import com.punuo.sys.app.agedcare.sip.SipMessageFactory;
import com.punuo.sys.app.agedcare.sip.SipUser;
import com.punuo.sys.app.agedcare.tools.ActivityCollector;
import com.punuo.sys.sdk.util.ToastUtils;
import com.punuo.sys.app.agedcare.view.LoginProgressDialog;

import org.zoolu.sip.address.NameAddress;
import org.zoolu.sip.address.SipURL;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.punuo.sys.app.agedcare.model.Constant.id;
import static java.lang.Thread.sleep;

public class MainActivity extends HindebarActivity implements View.OnClickListener{
    @BindView(R.id.setting)
    Button setting;
    @BindView(R.id.login)
    Button login;
    private String SdCard;
//    Loading_view loading_view;
    private static final String TAG = "MainActivity";
//    public static SharedPreferences pref;
    private LoginProgressDialog logining;
//    private SharedPreferences.Editor editor;
    private Handler handler = new Handler();
    private Handler clicklogin=new Handler();
    //计时时间
    public int timeing;
    //点击按钮的标志
    public boolean flag;
    //账号不存在
//    private AlertDialog accountNotExistDialog;
    //网络连接失败窗口
    private AlertDialog newWorkConnectedDialog;
    //登陆超时
    private AlertDialog timeOutDialog;
    //配置文件路径
    private String configPath;
    Runnable runnable =new Runnable() {
        @Override
        public void run() {
            if(timeing<20){
                Log.d(TAG,"isNetworkConnected1: "+SipInfo.isNetworkConnected);
                if (!SipInfo.isNetworkConnected) {
                    isNetworkreachable();
                    Log.e(TAG,"isNetworkConnected2: "+SipInfo.isNetworkConnected);
                    timeing++;
                    clicklogin.postDelayed(this,1000);
                }else {
                    Log.e(TAG, "isNetworkConnected: " + SipInfo.isNetworkConnected);
                    timeing++;
                    init();
                }

            }else{
                handler.post(timeOut);
                clicklogin.removeCallbacks(this);
                flag = false;
                timeing = 0;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        ProcessTasks.commonLaunchTasks(getApplication());
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        hideNavigationBar();
        logining=new LoginProgressDialog(this);
//        logining.setCanceledOnTouchOutside(false);
//        loading_view=new Loading_view(this,R.style.CustomDialog);
        ButterKnife.bind(this);
        setting.setOnClickListener(this);
        login.setOnClickListener(this);
        //初始化
//      init();

    }

    //初始化
    private void init() {

       isNetworkreachable();
        login();
    }

    //登录前的准备
    private void beforeLogin() {
        SipInfo.isAccountExist = true;
        SipInfo.passwordError = false;
        SipInfo.userLogined = false;
        SipInfo.loginTimeout = true;
        SipURL local = new SipURL(SipInfo.REGISTER_ID, SipInfo.serverIp, SipInfo.SERVER_PORT_USER);
        SipURL remote = new SipURL(SipInfo.SERVER_ID, SipInfo.serverIp, SipInfo.SERVER_PORT_USER);
        SipInfo.user_from = new NameAddress(SipInfo.userAccount, local);
        SipInfo.user_to = new NameAddress(SipInfo.SERVER_NAME, remote);
        SipInfo.devLogined = false;
        SipInfo.dev_loginTimeout = true;
        SipURL local_dev = new SipURL(SipInfo.devId, SipInfo.serverIp, SipInfo.SERVER_PORT_DEV);
        SipURL remote_dev = new SipURL(SipInfo.SERVER_ID, SipInfo.serverIp, SipInfo.SERVER_PORT_DEV);
        SipInfo.dev_from = new NameAddress(SipInfo.devId, local_dev);
        SipInfo.dev_to = new NameAddress(SipInfo.SERVER_NAME, remote_dev);
    }

    private void login() {
        if (SipInfo.isNetworkConnected) {
            clicklogin.removeCallbacks(runnable);
            SdCard = Environment.getExternalStorageDirectory().getAbsolutePath() + "/qinqingshixian";
            configPath = SdCard + "/config.properties";
            //创建根目录文件夹
            createDirs(SdCard);
            //第一次烧写时保留这四行，然后重新烧写注释以下四行
            // 存储账号密码到本地
//            SipInfo.userAccount = "310023000001953992";
//            SipInfo.devId = "310023000201950001";
//            SipInfo.port = "7035";
//           saveAcountAndPasswordAndPortToLocal();

            //加载本地存储的账号密码
            loadProperties();
            beforeLogin();
            new Thread(connecting).start();
        }
//        else {
//
//            //弹出网络连接失败窗口
//            handler.post(networkConnectedFailed);
//            logining.dismiss();
//        }
    }
    //创建文件夹
    private boolean createDirs(String dir) {
        try {
            File dirPath = new File(dir);
            if (!dirPath.exists()) {
                dirPath.mkdirs();
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 读取配置文件
     */
    private Properties loadConfig(String file) {
        Properties properties = new Properties();
        try {
            FileInputStream s = new FileInputStream(file);
            properties.load(s);
        } catch (Exception e) {
            return null;
        }
        return properties;
    }

    private void loadProperties() {
        //读取配置文件
        Properties properties;
        File config = new File(configPath);
        if (config.exists()) {
            properties = loadConfig(configPath);
            if (properties != null) {
                //配置信息
                SipInfo.userAccount = properties.getProperty("userAccount");
                Log.d(TAG, "userAccount: "+ SipInfo.userAccount);
                SipInfo.devId = properties.getProperty("devId");
                Log.d(TAG, "devId: "+ SipInfo.devId);
                SipInfo.port = properties.getProperty("port");
                GroupInfo.port = Integer.parseInt(SipInfo.port);
                Log.d(TAG, "port: "+ SipInfo.port);
            }
        }
    }

    private void saveAcountAndPasswordAndPortToLocal() {
        Properties config = loadConfig(configPath);
        if (config == null) {
            config = new Properties();
        }
        config.put("userAccount", SipInfo.userAccount );
        config.put("devId", SipInfo.devId);
        config.put("port", SipInfo.port);
        saveConfig(configPath, config);
    }

    /**
     * 保存配置文件
     */
    public boolean saveConfig(String configPath, Properties properties) {
        try {
            File config = new File(configPath);
            if (!config.exists())
                config.createNewFile();
            FileOutputStream s = new FileOutputStream(config);
            properties.store(s, "");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    //用户注册
    Runnable connecting =   new Runnable() {
        @Override
        public void run() {
            try {
                int hostPort = new Random().nextInt(5000) + 2000;
                SipInfo.sipUser = new SipUser(null, hostPort, MainActivity.this);
                org.zoolu.sip.message.Message register = SipMessageFactory.createRegisterRequest(
                        SipInfo.sipUser, SipInfo.user_to, SipInfo.user_from);
                SipInfo.sipUser.sendMessage(register);
                sleep(1000);
                Log.e(TAG+"1","请求中....");
                for (int i = 0; i < 2; i++) {
                    if (!SipInfo.isAccountExist) {
                        //用户账号不存在
                        logining.dismiss();
                        break;
                    }
                    if (SipInfo.passwordError) {
                        //密码错误
                        logining.dismiss();
                        break;
                    }
                    if (!SipInfo.loginTimeout) {
                        //没有超时
                        break;
                    }
                    SipInfo.sipUser.sendMessage(register);
                    Log.e(TAG+"2","请求中....");
                    sleep(1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                if (!SipInfo.isAccountExist) {
                    /**账号不存在提示*/
                    Log.d(TAG, "账号不存在");
                    logining.dismiss();
                    handler.post(timeOut);
                    clicklogin.removeCallbacks(this);
                    flag = false;
                    timeing = 0;
                }
                else if (SipInfo.passwordError) {
                    //密码错误提示
                    Log.d(TAG, "密码错误");
                    logining.dismiss();
                    handler.post(timeOut);
                    clicklogin.removeCallbacks(this);
                    flag = false;
                    timeing = 0;
//                    Toast.makeText(MainActivity.this,"密码错误",Toast.LENGTH_SHORT).show();
                }
            else if (SipInfo.loginTimeout) {
                    //超时
                    Log.d(TAG, "超时");
                    handler.post(timeOut);
                    clicklogin.removeCallbacks(this);
                    flag = false;
                    timeing = 0;
                } else {
                    if (SipInfo.userLogined) {
                        Log.i(TAG, "用户登录成功!");
                        //开启用户保活心跳包
                        SipInfo.keepUserAlive = new KeepAlive();
                        SipInfo.keepUserAlive.setType(0);
                        SipInfo.keepUserAlive.startThread();
                        //启动设备注册线程
                        new Thread(devConnecting).start();
                    }
                }
            }
        }
    };

    //设备注册线程
    private Runnable devConnecting = new Runnable() {
        @Override
        public void run() {
            try {
                int hostPort = new Random().nextInt(5000) + 2000;
                SipInfo.sipDev = new SipDev(MainActivity.this, null, hostPort);//无网络时在主线程操作会报异常
                org.zoolu.sip.message.Message register = SipMessageFactory.createRegisterRequest(
                        SipInfo.sipDev, SipInfo.dev_to, SipInfo.dev_from);
                for (int i = 0; i < 3; i++) {//如果没有回应,最多重发2次
                    SipInfo.sipDev.sendMessage(register);
                    sleep(2000);
                    if (!SipInfo.dev_loginTimeout) {
                        break;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                if (SipInfo.devLogined) {
                    Log.d(TAG, "设备注册成功!");
                    Log.d(TAG, "设备心跳包发送!");
                    startActivity(new Intent(MainActivity.this, MenuActivity.class));
                    logining.dismiss();
                    //启动设备心跳线程
                    SipInfo.keepDevAlive = new KeepAlive();
                    SipInfo.keepDevAlive.setType(1);
                    SipInfo.keepDevAlive.startThread();
                    new Thread(getuserinfo).start();
                } else {
                    logining.dismiss();
                    handler.post(noDevId);
                    clicklogin.removeCallbacks(this);
                    flag = false;
                    timeing = 0;
                    Log.e(TAG, "设备注册失败!");

                }
            }
        }
    };
    //获取用户数据线程
    String response = "";
    private Runnable getuserinfo = new Runnable() {
        @Override
        public void run() {
            response = GetPostUtil.sendGet1111(Constant.URL_GetUserInfo, "userid=" + SipInfo.userAccount);
//        LocalUserInfo.getInstance(LoginActivity.this).setUserInfo("tiezi",
//                Constant.res);
            Log.i("jonsresponse...........", response);
            if ((response != null) && !("".equals(response))) {
                JSONObject obj = JSON.parseObject(response);
                String msg = obj.getString("msg");
                if ("success".equals(msg)) {
                    JSONObject user = obj.getJSONObject("user");
                    Constant.nick = user.getString("nickname");
                    Constant.avatar = user.getString("avatar");
                    id = user.getString("id");
                    Constant.phone = user.getString("name");
                    Log.e("jonsresponse...........", id);

                } else {
                    Looper.prepare();
                    ToastUtils.showToast("获取用户数据失败请重试");
                    logining.dismiss();
                    handler.post(timeOut);
                    clicklogin.removeCallbacks(this);
                    flag = false;
                    timeing = 0;
                    Looper.loop();
                }
            } else {
                Looper.prepare();
                ToastUtils.showToast("获取用户数据失败请重试");
                logining.dismiss();
                handler.post(timeOut);
                clicklogin.removeCallbacks(this);
                flag = false;
                timeing = 0;
                Looper.loop();
            }
        }
    };
    private Runnable timeOut =  new Runnable() {
        @Override
        public void run() {
            if (timeOutDialog == null || !timeOutDialog.isShowing()) {
                timeOutDialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("连接超时,请检查网络")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .create();
                timeOutDialog.show();
                timeOutDialog.setCancelable(false);
                timeOutDialog.setCanceledOnTouchOutside(false);
                logining.dismiss();
            }
        }
    };
    private Runnable noDevId =  new Runnable() {
        @Override
        public void run() {
            if (timeOutDialog == null || !timeOutDialog.isShowing()) {
                timeOutDialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("设备号违规或不存在")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .create();
                timeOutDialog.show();
                timeOutDialog.setCancelable(false);
                timeOutDialog.setCanceledOnTouchOutside(false);
                logining.dismiss();
            }
        }
    };
    // 网络是否连接
    private Runnable networkConnectedFailed = new Runnable() {

        @Override
        public void run() {
            if (newWorkConnectedDialog == null || !newWorkConnectedDialog.isShowing()) {
                newWorkConnectedDialog = new AlertDialog.Builder(MainActivity.this)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent mIntent = new Intent(Settings.ACTION_SETTINGS);
                                startActivity(mIntent);
                            }
                        })
                        .setTitle("当前无网络,请检查网络连接")
                        .create();
                newWorkConnectedDialog.setCancelable(false);
                newWorkConnectedDialog.setCanceledOnTouchOutside(false);
                newWorkConnectedDialog.show();
            }
        }
    };


    //检查网络是否连接
    public boolean isNetworkreachable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info == null) {
            SipInfo.isNetworkConnected = false;
        } else {
            SipInfo.isNetworkConnected = info.getState() == NetworkInfo.State.CONNECTED;
        }
        return SipInfo.isNetworkConnected;
    }

    @Override
    public void onBackPressed() {
        //屏蔽返回键
    }

    @Override
    protected void onDestroy() {
        try{
            if (logining!=null){
            logining.dismiss();}
        }catch (Exception e) {
            System.out.println("myDialog取消，失败！");

        }
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting:
                Intent mIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                startActivity(mIntent);
                break;
            case R.id.login:
                logining.setTitle("登录中...");
                logining.dismiss();
                logining.show();
                if(!flag){
                    clicklogin.postDelayed(runnable, 1000);
                }
                flag=true;
                break;
            default:
                break;
        }
    }
    //实在去不掉用这个
    public void hideNavigationBar() {
        int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                | View.SYSTEM_UI_FLAG_FULLSCREEN; // hide status bar

        if (android.os.Build.VERSION.SDK_INT >= 19) {
            uiFlags |= View.SYSTEM_UI_FLAG_IMMERSIVE;//0x00001000; // SYSTEM_UI_FLAG_IMMERSIVE_STICKY: hide
        } else {
            uiFlags |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
        }

        try {
            getWindow().getDecorView().setSystemUiVisibility(uiFlags);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}
