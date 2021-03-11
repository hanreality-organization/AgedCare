package com.punuo.sys.app.agedcare.receiver;

        import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.punuo.sys.app.agedcare.sip.SipInfo;

public class SosReceiver extends BroadcastReceiver {
    private static final String TAG = "SosReceiver";
    public SosReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the CallBroadcastReceiver is receiving
        // an Intent broadcast.
//        String keyEvent = intent.getStringExtra("down");
//        Log.e(TAG,keyEvent);
        if(!SipInfo.devList.isEmpty()) {
            for (int position=0;position<SipInfo.devList.size();position++) {
                //TODO 报警
            }
        }
    }
}
