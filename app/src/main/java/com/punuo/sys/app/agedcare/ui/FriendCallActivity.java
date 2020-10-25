package com.punuo.sys.app.agedcare.ui;


import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.punuo.sys.app.agedcare.R;
import com.punuo.sys.app.agedcare.db.MyDatabaseHelper;
import com.punuo.sys.app.agedcare.http.GetPostUtil;
import com.punuo.sys.app.agedcare.model.Constant;
import com.punuo.sys.app.agedcare.model.Farmilymember;
import com.punuo.sys.app.agedcare.adapter.PhoneRecyclerViewAdapter;
import com.punuo.sys.app.agedcare.sip.SipInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.HashMap;
import static com.punuo.sys.app.agedcare.sip.SipInfo.dbHelper;
import static com.punuo.sys.app.agedcare.sip.SipInfo.farmilymemberList;



public class FriendCallActivity extends HindebarActivity {

    private RecyclerView rv;
    private Context mContext;
    private String TAG="FriendCallActivity";
    private PhoneRecyclerViewAdapter adapter;
    private HashMap<Integer, float[]> xyMap=new HashMap<Integer, float[]>();//所有子项的坐标
    private int screenWidth;//屏幕宽度
    private int screenHeight;//屏幕高度

    private Handler Handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what) {

                case 0X111:
                   adapter.notifyDataSetChanged();

                    break;
            }
            return false;
        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_call);
        mContext=this;
        EventBus.getDefault().register(this);
        putdata();
        initView();
        dbHelper=new MyDatabaseHelper(this,"member.db",null,2);
//       initData();
        setEvent();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if(event.getMessage().equals("addcompelete")) {
             String TAG="FriendCallActivity";
            Log.d(TAG,"receicer");
            putdata();
        }
    }
    private void putdata()
    {
        new Thread(getuserinfo).start();
    }
    String response = "";
    private Runnable getuserinfo = new Runnable() {
        @Override
        public void run() {
            response = GetPostUtil.sendGet1111(Constant.URL_getAddressbook, "userid=" + SipInfo.userAccount);
            Log.e(TAG, response);
            if ((response != null) && !("".equals(response))) {
                 farmilymemberList = JSON.parseArray(response, Farmilymember.class);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Handler.sendEmptyMessage(0X111);
                    }
                }).start();
           }
        }
    };
    @Override
    protected void onResume() {
        super.onResume();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth =  dm.widthPixels;
        screenHeight = dm.heightPixels;
    }

    /**
     * recyclerView item点击事件
     */
    private void setEvent() {
        adapter.setmOnItemClickListener(new PhoneRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(Intent.ACTION_CALL);
               Uri data = Uri.parse("tel:" + farmilymemberList.get(position).getTelnum());
                intent.setData(data);
                intent.putExtra("position", position);
                xyMap.clear();//每一次点击前子项坐标都不一样，所以清空子项坐标
                //子项前置判断，是否在屏幕内，不在的话获取屏幕边缘坐标
                View view0=rv.getChildAt(0);
                int position0=rv.getChildAdapterPosition(view0);
                if(position0>0)
                {
                    for(int j=0;j<position0;j++)
                    {
                        float[] xyf=new float[]{(1/6.0f+(j%3)*(1/3.0f))*screenWidth,0};//每行3张图，每张图的中心点横坐标自然是屏幕宽度的1/6,3/6,5/6
                        xyMap.put(j, xyf);
                    }
                }
                //其余子项判断
                for(int i=position0;i<rv.getAdapter().getItemCount();i++)
                {
                    View view1=rv.getChildAt(i-position0);
                    if(rv.getChildAdapterPosition(view1)==-1)//子项末尾不在屏幕部分同样赋值屏幕底部边缘
                    {
                        float[] xyf=new float[]{(1/6.0f+(i%3)*(1/3.0f))*screenWidth,screenHeight};
                        xyMap.put(i, xyf);
                    }
                    else
                    {
                        int[] xy = new int[2];
                        view1.getLocationOnScreen(xy);
                        float[] xyf=new float[]{xy[0]*1.0f+view1.getWidth()/2,xy[1]*1.0f+view1.getHeight()/2};
                        xyMap.put(i, xyf);
                    }
                }
                intent.putExtra("xyMap",xyMap);
                startActivity(intent);
            }
        });
    }

    private void initView()
    {
        rv=(RecyclerView)findViewById(R.id.rv);
        GridLayoutManager glm=new GridLayoutManager(mContext,4);//定义3列的网格布局
        rv.setLayoutManager(glm);
        rv.addItemDecoration(new FriendCallActivity.RecyclerViewItemDecoration(10,4));//初始化子项距离和列数
        adapter=new PhoneRecyclerViewAdapter(mContext,glm);
        rv.setAdapter(adapter);
        FloatingActionButton fab=(FloatingActionButton)findViewById(R.id.add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FriendCallActivity.this, addressAddActivity.class));
            }
        });
    }

    public class RecyclerViewItemDecoration extends RecyclerView.ItemDecoration
    {
        private int itemSpace;//定义子项间距
        private int itemColumnNum;//定义子项的列数

         RecyclerViewItemDecoration(int itemSpace, int itemColumnNum) {
            this.itemSpace = itemSpace;
            this.itemColumnNum = itemColumnNum;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.bottom=itemSpace;//底部留出间距
            if(parent.getChildAdapterPosition(view)%itemColumnNum==0)//每行第一项左边不留间距，其他留出间距
            {
                outRect.left=0;
            }
            else
            {
                outRect.left=itemSpace;
            }
        }
    }

    /**
     * 重写startActivity方法，禁用activity默认动画
     */
    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(0,0);
    }

    @Override
    protected void onDestroy() {
        farmilymemberList.clear();
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
