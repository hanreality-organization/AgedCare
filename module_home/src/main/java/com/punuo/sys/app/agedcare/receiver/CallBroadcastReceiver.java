package com.punuo.sys.app.agedcare.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.punuo.sys.app.agedcare.ui.VideoReply;

public class CallBroadcastReceiver extends BroadcastReceiver {
//    public CallBroadcastReceiver() {
//
//    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the CallBroadcastReceiver is receiving
        // an Intent broadcast.
//        throw new UnsupportedOperationException("Not yet implemented");
//        Toast.makeText(context, "received in MyBroadcastReceiver", Toast.LENGTH_SHORT).show();
//        Bundle bundle = intent.getExtras();
        Intent intent1 = new Intent(context.getApplicationContext(), VideoReply.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent1);

    }
}

