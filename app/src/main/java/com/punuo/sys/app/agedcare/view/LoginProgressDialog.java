package com.punuo.sys.app.agedcare.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.punuo.sys.app.agedcare.R;

/**
 * Created by 23578 on 2018/9/22.
 */

public class LoginProgressDialog extends Dialog {
    public LoginProgressDialog(Context context, int themeResId) {
        super(context, themeResId);
    }
    public LoginProgressDialog(Context context) {
        this(context, R.style.CustomProgressDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginprogressdialog);
    }
}

