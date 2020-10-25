package com.punuo.sys.app.agedcare.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.punuo.sys.app.agedcare.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class JiazhengActivity extends HindebarActivity {
    @Bind(R.id.jiazheng_cancel)
    Button jiazheng_cancel;
    @Bind(R.id.jiazheng_call)
    Button jiazheng_call;
    String item;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jiazheng);
        ButterKnife.bind(this);
        jiazheng_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                rejectCall();
                finish();
            }
        });
        SharedPreferences preferences = getSharedPreferences("data", MODE_PRIVATE);
        item = preferences.getString("家政电话", "");
//       call();
        jiazheng_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("EATActivity", item + "");
                call();
            }
        });
    }
    private void call()
    {

        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + item);
        intent.setData(data);
        startActivity(intent);
    }

}
