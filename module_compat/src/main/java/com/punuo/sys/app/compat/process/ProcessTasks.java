package com.punuo.sys.app.compat.process;

import android.app.Application;

import com.punuo.sip.ISipConfig;
import com.punuo.sip.SipConfig;
import com.punuo.sip.dev.SipDevManager;
import com.punuo.sip.thread.SipInitThread;
import com.punuo.sip.user.SipUserManager;
import com.punuo.sys.sdk.account.AccountManager;
import com.punuo.sys.sdk.activity.ActivityLifeCycle;
import com.punuo.sys.sdk.httplib.HttpConfig;
import com.punuo.sys.sdk.httplib.HttpManager;
import com.punuo.sys.sdk.httplib.IHttpConfig;
import com.punuo.sys.sdk.util.DebugCrashHandler;
import com.punuo.sys.sdk.util.DeviceHelper;

import org.zoolu.sip.address.NameAddress;
import org.zoolu.sip.address.SipURL;


/**
 * Created by han.chen.
 * Date on 2019-06-15.
 **/
public class ProcessTasks {

    public static void commonLaunchTasks(Application app) {
        if (DeviceHelper.isApkInDebug()) {
            DebugCrashHandler.getInstance().init(); //崩溃日志收集
        }
        app.registerActivityLifecycleCallbacks(ActivityLifeCycle.getInstance());
        HttpConfig.init(new IHttpConfig() {
            @Override
            public String getHost() {
                return "sip.qinqingonline.com";
            }

            @Override
            public int getPort() {
                return 8000;
            }

            @Override
            public boolean isUseHttps() {
                return false;
            }

            @Override
            public String getUserAgent() {
                return "punuo";
            }

            @Override
            public String getPrefixPath() {
                return "/xiaoyupeihu/public/index.php";
            }
        });
        HttpManager.setDebug(true);
        HttpManager.init();
        SipConfig.init(new ISipConfig() {
            NameAddress mUserServerAddress;
            NameAddress mDevServerAddress;
            NameAddress mUserNormalAddress;
            NameAddress mDevNormalAddress;
            @Override
            public String getServerIp() {
                return "sip.qinqingonline.com";
            }

            @Override
            public int getUserPort() {
                return 6061;
            }

            @Override
            public int getDevPort() {
                return 6060;
            }

            @Override
            public NameAddress getUserServerAddress() {
                if (mUserServerAddress == null) {
                    SipURL remote = new SipURL(SipConfig.SERVER_ID, SipConfig.getServerIp(), SipConfig.getUserPort());
                    mUserServerAddress = new NameAddress(SipConfig.SERVER_NAME, remote);
                }
                return mUserServerAddress;
            }

            @Override
            public NameAddress getUserRegisterAddress() {
                SipURL local = new SipURL(SipConfig.REGISTER_ID, SipConfig.getServerIp(), SipConfig.getUserPort());
                return new NameAddress(AccountManager.getUserId(), local);
            }

            @Override
            public NameAddress getUserNormalAddress() {
                if (mUserNormalAddress == null) {
                    SipURL local = new SipURL(AccountManager.getUserId(), SipConfig.getServerIp(), SipConfig.getUserPort());
                    mUserNormalAddress = new NameAddress(AccountManager.getUserId(), local);
                }
                return mUserNormalAddress;
            }

            @Override
            public NameAddress getDevServerAddress() {
                if (mDevServerAddress == null) {
                    SipURL remote = new SipURL(SipConfig.SERVER_ID, SipConfig.getServerIp(), SipConfig.getDevPort());
                    mDevServerAddress = new NameAddress(SipConfig.SERVER_NAME, remote);
                }
                return mDevServerAddress;
            }

            @Override
            public NameAddress getDevRegisterAddress() {
                SipURL local = new SipURL(AccountManager.getDevId(), SipConfig.getServerIp(), SipConfig.getDevPort());
                return new NameAddress(AccountManager.getDevId(), local);
            }

            @Override
            public NameAddress getDevNormalAddress() {
                if (mDevNormalAddress == null) {
                    SipURL local = new SipURL(AccountManager.getDevId(), SipConfig.getServerIp(), SipConfig.getDevPort());
                    mDevNormalAddress = new NameAddress(AccountManager.getDevId(), local);
                }
                return mDevNormalAddress;
            }

            @Override
            public void reset() {
                mDevServerAddress = null;
                mUserServerAddress = null;
                mUserNormalAddress = null;
                mDevNormalAddress = null;
            }
        });
        SipUserManager.setContext(app);
        SipDevManager.setContext(app);
        new SipInitThread().start();
    }
}
