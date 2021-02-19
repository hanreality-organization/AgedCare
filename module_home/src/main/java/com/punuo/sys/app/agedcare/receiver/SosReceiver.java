package com.punuo.sys.app.agedcare.receiver;

        import android.content.BroadcastReceiver;
        import android.content.Context;
        import android.content.Intent;
        import com.punuo.sys.app.agedcare.sip.BodyFactory;
        import com.punuo.sys.app.agedcare.sip.SipInfo;
        import com.punuo.sys.app.agedcare.sip.SipMessageFactory;
        import org.zoolu.sip.address.NameAddress;
        import org.zoolu.sip.address.SipURL;
        import static com.punuo.sys.app.agedcare.sip.SipInfo.userId;

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
                SipURL remote = new SipURL(SipInfo.devList.get(position).getUserid(), SipInfo.serverIp, SipInfo.SERVER_PORT_USER);
                SipInfo.toUser = new NameAddress(SipInfo.devList.get(position).getUserid(), remote);
                SipInfo.sipUser.sendMessage(SipMessageFactory.createNotifyRequest(SipInfo.sipUser, SipInfo.toUser
                        , SipInfo.user_from, BodyFactory.createAlarm(userId)));
            }
        }
    }
}
