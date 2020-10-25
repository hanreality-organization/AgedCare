package com.punuo.sys.app.agedcare.ui;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.punuo.sys.app.agedcare.R;
import com.punuo.sys.app.agedcare.adapter.FarmilyAdapter;
import com.punuo.sys.app.agedcare.db.MyDatabaseHelper;
import com.punuo.sys.app.agedcare.model.Farmilymember;
import com.punuo.sys.app.agedcare.tools.ActivityCollector;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import butterknife.Bind;
import butterknife.ButterKnife;

import static com.punuo.sys.app.agedcare.sip.SipInfo.dbHelper;
import static com.punuo.sys.app.agedcare.sip.SipInfo.farmilymemberList;




public class phonecallActivity extends HindebarActivity {
    @Bind(R.id.addnumber)
    Button addnumber;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;

    public FarmilyAdapter farmilyAdapter;
    Farmilymember far;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phonecall);
        ButterKnife.bind(this);
        ActivityCollector.addActivity(this);
        EventBus.getDefault().register(this);
        dbHelper=new MyDatabaseHelper(this,"member.db",null,2);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        farmilyAdapter=new FarmilyAdapter(this,farmilymemberList);
        recyclerView.setAdapter(farmilyAdapter);
//        putdata();
        addnumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(phonecallActivity.this, addressAddActivity.class));

            }
        });
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if(event.getMessage().equals("addcompelete")) {
            String TAG="phonecallActivity";
            Log.d(TAG,"receicer");
            farmilymemberList.clear();
//             putdata();
//            farmilyAdapter.refreshDatas(farmilymemberList);
        }
    }
//    private void putdata()
//    {
//        SQLiteDatabase db=dbHelper.getWritableDatabase();
//        Cursor cursor=db.query("Person",null,null,null,null,null,null);
//        Log.d("ton","11111");
//        if(cursor.moveToFirst())
//        {
//            do {
//                String name=cursor.getString(cursor.getColumnIndex("name"));
//                String phonenumber=cursor.getString(cursor.getColumnIndex("phonenumber"));
////                String avatorurl=cursor.getString(cursor.getColumnIndex("avatorurl"));
//                far = new Farmilymember(name, phonenumber);
//                farmilymemberList.add(far);
//                farmilyAdapter.refreshDatas(farmilymemberList);
//                Log.d("tongxunlu",name+" "+phonenumber);
//            }while (cursor.moveToNext());
//        }
//        cursor.close();
//    }
    @Override
    protected void onDestroy() {

        super.onDestroy();
        farmilymemberList.clear();
      EventBus.getDefault().unregister(this);
    }

}
