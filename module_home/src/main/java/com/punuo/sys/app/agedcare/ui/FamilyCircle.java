package com.punuo.sys.app.agedcare.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.punuo.sys.app.agedcare.R;
import com.punuo.sys.app.agedcare.R2;
import com.punuo.sys.app.agedcare.friendCircleMain.adapter.MyListAdapter;
import com.punuo.sys.app.agedcare.friendCircleMain.custonListView.CustomListView;
import com.punuo.sys.app.agedcare.friendCircleMain.domain.FirendMicroList;
import com.punuo.sys.app.agedcare.friendCircleMain.domain.FirendMicroListDatas;
import com.punuo.sys.app.agedcare.friendCircleMain.domain.FirendsMicro;
import com.punuo.sys.app.agedcare.friendcircle.PublishedActivity;
import com.punuo.sys.app.agedcare.http.GetPostUtil;
import com.punuo.sys.app.agedcare.model.Constant;
import com.punuo.sys.sdk.event.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FamilyCircle extends HindebarActivity implements MyListAdapter.PositionListener{
    @BindView(R2.id.iv_back7)
    Button ivBack7;
    @BindView(R2.id.titleset)
    TextView titleset;
    @BindView(R2.id.iv_fatie)
    ImageView ivFatie;

    TextView title;

    String SdCard = Environment.getExternalStorageDirectory().getAbsolutePath();

    private static final String TAG = "FamilyCircleActivity";
    int now = 0;

    private int count = 1;
    List<FirendMicroListDatas> listdatas = new ArrayList<FirendMicroListDatas>();//json数据
    public CustomListView listview;
    public MyListAdapter mAdapter;//这是真正的
    private static int i;
    private static int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_family_circle);
        ButterKnife.bind(this);
        init();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {//因为不是所有的系统都可以设置颜色的，在4.4以下就不可以。。有的说4.1，所以在设置的时候要检查一下系统版本是否是4.1以上
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.newbackground));
        }

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                if (msg.what == 0x111) {
                    getData(msg.obj.toString());
                } else if (msg.what == 0x222) {
                    getData(msg.obj.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    public void refresh() {
        new Thread() {
            @Override
            public void run() {
                count = 1;
                Constant.res = GetPostUtil.sendGet1111(Constant.URL_getPostList, "id=" +
                        Constant.id + "&currentPage=" + count + "&groupid=" + Constant.groupid);
                Log.i("jonsresponse...........", Constant.res + "");
                Message msg = handler.obtainMessage();
                msg.what = 0x111;
                msg.obj = "下拉刷新";
//                msg.obj = "上拉加载更多";
                handler.sendMessage(msg);
            }
        }.start();
    }

    private void init() {
        listview = (CustomListView) findViewById(R.id.list);
        listview.setVerticalScrollBarEnabled(false);
        listview.setDivider(null);
//        listview.addHeaderView(header);
        mAdapter = new MyListAdapter(this, listdatas);
        listview.setAdapter(mAdapter);
        mAdapter.setPositionListener(this);
        listdatas.clear();
        try {
            getMicroList(0, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        refresh();

//        listview.setOnScrollListener(new OnScrollListener() {
//
//            /**
//             * 滚动状态改变时调用
//             */
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                // 不滚动时保存当前滚动到的位置
//                if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
//                      position = listview.getFirstVisiblePosition();
//                }
//            }
//
//            /**
//             * 滚动时调用
//             */
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
// int totalItemCount) {
//            }
//        });
        //下拉刷新
        listview.setOnRefreshListener(new CustomListView.OnRefreshListener() {

            @Override
            public void onRefresh() {
                // TODO Auto-generated method stub
                new Thread() {
                    @Override
                    public void run() {
                        count = 1;
                        Constant.res = GetPostUtil.sendGet1111(Constant.URL_getPostList, "id=" +
                                Constant.id + "&currentPage=" + count + "&groupid=" + Constant
                                .groupid);
                        Log.i("jonsresponse...........", Constant.res + "");
                        Message msg = handler.obtainMessage();
                        msg.what = 0x111;
                        msg.obj = "下拉刷新";
                        handler.sendMessage(msg);

                    }
                }.start();
                //String s="下拉刷新";
                //getData(s);
            }

        });
        //上拉加载更多
        listview.setOnLoadListener(new CustomListView.OnLoadMoreListener() {

            public void onLoadMore() {
                new Thread() {
                    @Override
                    public void run() {
                        count++;
                        Constant.res = GetPostUtil.sendGet1111(Constant.URL_getPostList, "id=" +
                                Constant.id + "&currentPage=" + count + "&groupid=" + Constant
                                .groupid);
                        Log.i("jonsresponse...........", Constant.res);
                        Message msg = handler.obtainMessage();
                        msg.what = 0x222;
                        msg.obj = "上拉加载更多";
                        handler.sendMessage(msg);

                    }
                }.start();
//                String s = "上拉加载更多";
//                getData(s);

            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (event.getMessage().equals("刷新")) {

            // 更新界面
            refresh();
        } else if (event.getMessage().equals("刷新点赞")) {
            Log.e(TAG, "111message is " + event.getMessage());
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 得到主界面的列表
     *
     * @param i
     * @param has
     */
    private void getMicroList(final int i, boolean has) {
        if (TextUtils.isEmpty(Constant.res)) {
            return;
        }

        FirendsMicro fm = JSON.parseObject(Constant.res, FirendsMicro.class);
        //FirendMicroList fList=fm.getFriendPager();
        FirendMicroList fList = fm.getPostList();
        //if("0".equals(fm.getError())){

        if (i == 0) {
           listdatas.clear();
        }

        if (null == fList.getDatas() || fList.getDatas().size() == 0) {
            if (i == 0) {
                listview.onRefreshComplete();
            } else {
                listview.onLoadMoreComplete(false);
            }
        } else {
            if (i == 0) {
                listview.onRefreshComplete();
            } else {
                listview.onLoadMoreComplete();
            }
            listdatas.addAll(fList.getDatas());

        }
        int k = listdatas.size();
        now = k > 0 ? k - 1 : 0;
        mAdapter.notifyDataSetChanged();
        //}
    }

    @Override
    public void setPosition(int position) {
        this.i = position;
    }

    private void getData(String s) {
        // TODO Auto-generated method stub
        if ("下拉刷新".equals(s)) {

            getMicroList(0, true);
            listview.onRefreshComplete();
            listview.setSelection(i);
        } else {
            Log.e(TAG,"position"+i);
            getMicroList(now, true);

            listview.onLoadMoreComplete(); // 加载更多完成
        }
    }


    @OnClick({R2.id.iv_back7, R2.id.iv_fatie})
    public void onClock(View v) {
        int id = v.getId();
        if (id == R.id.iv_back7) {
            finish();
        } else if (id == R.id.iv_fatie) {
            startActivity(new Intent(this, PublishedActivity.class));
        }
    }

}
