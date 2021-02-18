package com.punuo.sys.sdk.util;

import android.app.Activity;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.inputmethod.InputMethodManager;

import com.punuo.sys.sdk.PnApplication;

/**
 * Created by han.chen.
 * Date on 2019/4/4.
 **/
public class CommonUtil {

    public static int getWidth() {
        DisplayMetrics dm = PnApplication.getInstance().getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    public static int getHeight() {
        DisplayMetrics dm = PnApplication.getInstance().getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    public static int dip2px(float dpValue) {
        final float scale = PnApplication.getInstance().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static void hideKeyboard(Activity act) {
        if (act != null && act.getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) act.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(act.getCurrentFocus().getWindowToken(), 0);
        }
    }

    /**
     * 获取存储路径
     */
    public static String getDataPath() {
        String path;
        if (isExistSDcard())
            path = Environment.getExternalStorageDirectory().getPath() + "/albumSelect";
        else
            path = PnApplication.getInstance().getFilesDir().getPath();
        if (!path.endsWith("/"))
            path = path + "/";
        return path;
    }


    /**
     * 检测SDcard是否存在
     *
     * @return
     */
    public static boolean isExistSDcard() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED))
            return true;
        else {
            return false;
        }
    }
}
