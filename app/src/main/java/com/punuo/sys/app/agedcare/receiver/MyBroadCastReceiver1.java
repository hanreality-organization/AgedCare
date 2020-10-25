package com.punuo.sys.app.agedcare.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.punuo.sys.app.agedcare.ui.VideoStart;

/**
 * Created by maojianhui on 2018/7/11.
 */

public class MyBroadCastReceiver1 extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent){
//        Toast.makeText(context,"received in MyBroadcastReceiver",Toast.LENGTH_SHORT).show();
        Bundle bundle=intent.getExtras();
        Intent intent1=new Intent(context.getApplicationContext(),VideoStart.class);

        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.getApplicationContext().startActivity(intent1);
        context.startActivity(intent1);
    }
}
