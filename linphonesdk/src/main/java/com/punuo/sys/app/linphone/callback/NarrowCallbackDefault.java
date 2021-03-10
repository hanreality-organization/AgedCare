package com.punuo.sys.app.linphone.callback;

import android.app.Activity;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import com.punuo.sys.app.linphone.R;
import com.punuo.sys.app.linphone.utils.SettingsCompat;

import java.lang.ref.WeakReference;

/**
 * Created by dds on 2018/5/19.
 * android_shuai@163.com
 */

public class NarrowCallbackDefault implements NarrowCallback {

    private WeakReference<Activity> activityWeakReference;

    public NarrowCallbackDefault(Activity activity) {
        activityWeakReference = new WeakReference<>(activity);
    }

    @Override
    public void openSystemWindow() {
        final Activity activity = activityWeakReference.get();
        if (activity != null && !activity.isFinishing()) {
            AlertDialog.Builder localBuilder = new AlertDialog.Builder(activity);
            localBuilder.setTitle(R.string.voice_chat_tips);
            localBuilder.setMessage(R.string.voice_chat_tips_content);
            localBuilder.setPositiveButton(R.string.voice_chat_tips_ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                    //确定
                    SettingsCompat.manageDrawOverlays(activity);
                }
            });
            localBuilder.setCancelable(false).create();
            localBuilder.show();
        }


    }
}
