package com.punuo.sys.app.agedcare.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.punuo.sys.app.agedcare.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PropertyActivity extends HindebarActivity {
    @Bind(R.id.wuye_cancel)
    Button wuye_cancel;
    @Bind(R.id.wuye_call)
    Button wuye_call;
    String item;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property);
        ButterKnife.bind(this);
        wuye_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        SharedPreferences preferences=getSharedPreferences("data",MODE_PRIVATE);
         item = preferences.getString("物业电话","");
        wuye_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
