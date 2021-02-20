package com.punuo.sys.app.agedcare.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.punuo.sys.app.agedcare.R;
import com.punuo.sys.app.agedcare.tools.NetUtils;

/**
 * Created by Administrator on 2018/7/2.
 */

public class TitleLayout extends LinearLayout {
    public TitleLayout(Context context) {
        this(context, null);
    }

    public TitleLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.TitleLayout, defStyleAttr, 0);
        int type = typedArray.getInt(R.styleable.TitleLayout_view_type, 0);
        typedArray.recycle();
        init(type);
    }

    public void init(int type) {
        LayoutInflater.from(getContext()).inflate(R.layout.title_layout, this);
        TextView net = findViewById(R.id.net);
        View back = findViewById(R.id.back);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
               if (getContext() instanceof AppCompatActivity) {
                   ((AppCompatActivity) getContext()).finish();
               }
            }
        });
        View Icon = findViewById(R.id.logo);
        if (type == 0) {
            back.setVisibility(View.GONE);
            Icon.setVisibility(View.VISIBLE);
        } else if (type == 1) {
            back.setVisibility(View.VISIBLE);
            Icon.setVisibility(View.GONE);
        }
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


