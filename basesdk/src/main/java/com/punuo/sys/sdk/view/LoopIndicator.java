package com.punuo.sys.sdk.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.punuo.sys.sdk.R;
import com.punuo.sys.sdk.util.CommonUtil;

/**
 * Created by han.chen.
 * Date on 2021/2/20.
 **/
public class LoopIndicator extends LinearLayout {

    private int mCurSize;

    public LoopIndicator(Context context) {
        this(context, null);
    }

    public LoopIndicator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_HORIZONTAL);
    }

    public void setData(int size) {
        this.removeAllViews();
        mCurSize = size;
        for (int i = 0; i < mCurSize; i++) {
            View view = LayoutInflater.from(getContext()).inflate(
                    R.layout.sdk_indicator_layout, this, false);
            LayoutParams layoutParams = new LayoutParams(CommonUtil.dip2px(18), CommonUtil.dip2px(6));
            if (i != 0) {
                layoutParams.setMargins(CommonUtil.dip2px(10), 0, 0, 0);
            }
            view.setLayoutParams(layoutParams);
            addView(view);
        }
    }

    public void select(int pos) {
        if (mCurSize <= 1) {
            return;
        }
        for (int i = 0; i < mCurSize; i++) {
            View view = getChildAt(i);
            if (i == pos) {
                view.setSelected(true);
            } else {
                view.setSelected(false);
            }
        }
    }
}
