package com.punuo.sys.app.agedcare;

import android.accounts.AccountManager;
import android.app.Application;

import com.punuo.sys.app.agedcare.httplib.HttpConfig;
import com.punuo.sys.app.agedcare.httplib.HttpManager;
import com.punuo.sys.app.agedcare.httplib.IHttpConfig;

/**
 * Created by han.chen.
 * Date on 2019-06-15.
 **/
public class ProcessTasks {

    public static void commonLaunchTasks(Application app) {
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
        HttpManager.setContext(app);
        HttpManager.init();

    }
}
