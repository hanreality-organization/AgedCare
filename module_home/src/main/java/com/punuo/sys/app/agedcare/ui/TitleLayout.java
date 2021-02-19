package com.punuo.sys.app.agedcare.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.punuo.sys.app.agedcare.R;
import com.punuo.sys.app.agedcare.tools.NetUtils;

/**
 * Created by Administrator on 2018/7/2.
 */

public class TitleLayout extends LinearLayout {
    public TitleLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        LayoutInflater.from(context).inflate(R.layout.title, this);
        TextView net = findViewById(R.id.net);
        if (NetUtils.getNetWorkState(getContext()) == NetUtils.NETWORK_MOBILE) {
            net.setText("4G");
        } else if (NetUtils.getNetWorkState(getContext()) == NetUtils.NETWORK_WIFI) {
            net.setText("WIFI");
        } else if (NetUtils.getNetWorkState(getContext()) == NetUtils.NETWORK_NONE) {
            net.setText("无网络");
        } else {
            net.setText("有线网");
        }
    }
}


