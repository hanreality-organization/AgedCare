package com.punuo.sys.app.agedcare.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import com.punuo.sys.app.agedcare.R;
import com.punuo.sys.app.agedcare.adapter.FarmilyRecyclerViewAdapter;
import com.punuo.sys.app.agedcare.model.Device;
import com.punuo.sys.app.agedcare.sip.BodyFactory;
import com.punuo.sys.app.agedcare.sip.SipDev;
import com.punuo.sys.app.agedcare.sip.SipInfo;
import com.punuo.sys.app.agedcare.sip.SipMessageFactory;

import org.zoolu.sip.address.NameAddress;
import org.zoolu.sip.address.SipURL;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


import static com.punuo.sys.app.agedcare.sip.SipInfo.devId;
import static com.punuo.sys.app.agedcare.sip.SipInfo.devName;
import static com.punuo.sys.app.agedcare.sip.SipInfo.devices;
import static com.punuo.sys.app.agedcare.sip.SipInfo.groupid;
import static com.punuo.sys.app.agedcare.sip.SipInfo.isanswering;
import static com.punuo.sys.app.agedcare.sip.SipInfo.netuserdevid;
import static com.punuo.sys.app.agedcare.sip.SipInfo.serverIp;
import static com.punuo.sys.app.agedcare.sip.SipInfo.url;
import static com.punuo.sys.app.agedcare.sip.SipInfo.userId;

public class MemberFragment extends Fragment implements SipDev.NumberUpdateListener{
    private Context mContext;

    @Bind(R.id.farmily_rv)
    RecyclerView farmily_rv;
    @Bind(R.id.gank_swipe_refresh)
    SwipeRefreshLayout gank_swipe_refresh_layout;
    private FarmilyRecyclerViewAdapter adapter;
    GridLayoutManager glm;
    public  String ip = "http://"+serverIp+":8000/static/xiaoyupeihu/";
    HashMap<String, Integer> stringIntegerHashMap = new HashMap<>();
    public Handler handler=new Handler(new Handler.Callback(){
        @Override
        public boolean handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0X111:
                    initView();
                    break;
                case 0X222:
                    adapter=new FarmilyRecyclerViewAdapter(mContext,glm);
                    farmily_rv.setAdapter(adapter);
                    Toast.makeText(getActivity(),"当前无联系人或请下拉刷新",Toast.LENGTH_SHORT).show();
                    break;
            }
            return false;
        }
    });
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_member_fragment, container, false);
        view.setClickable(true);
        ButterKnife.bind(this, view);
        glm=new GridLayoutManager(mContext,4);
        farmily_rv.setLayoutManager(glm);
        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.TOP_DECORATION,0);//top间距
        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.BOTTOM_DECORATION,48);//底部间距
        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.LEFT_DECORATION,15);//左间距
        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.RIGHT_DECORATION,0);//右间距
        farmily_rv.addItemDecoration(new RecyclerViewSpacesItemDecoration(stringIntegerHashMap));//初始化子项距离和列数
        mContext=getActivity();
        try {
            SipInfo.sipDev.setNumberUpdateListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
       showPicture();
        gank_swipe_refresh_layout.setSize(SwipeRefreshLayout.LARGE);
        gank_swipe_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.e("showpicture","111111");
                showPicture();
                new Handler().postDelayed(new Runnable() {//模拟耗时操作
                    @Override
                    public void run() {
                        gank_swipe_refresh_layout.setRefreshing(false);//取消刷新

                    }
                },1500);

            }

        });
        return view;
    }
    private void setEvent()
    {
        adapter.setmOnItemClickListener(new FarmilyRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, final int position) {
                isanswering = true;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Looper.prepare();
                            OkHttpClient client = new OkHttpClient();
                            final Request request1 = new Request.Builder()
                                    .url("http://" + serverIp + ":8000/xiaoyupeihu/public/index.php/devs/getUserDevId?id=" + devices.get(position).getId() + "&groupid=" + groupid)
                                    .build();
                            netuserdevid = client.newCall(request1).execute().body().string().substring(10, 28);
                            Log.d("1111", "run: " + netuserdevid);
                            netuserdevid = netuserdevid.substring(0, netuserdevid.length() - 4).concat("0160");//设备id后4位替换成0160
                            SipURL sipURL = new SipURL(netuserdevid, serverIp, SipInfo.SERVER_PORT_USER);
                            SipInfo.toDev = new NameAddress(devName, sipURL);
                            new Thread() {
                                @Override
                                public void run() {

                                    org.zoolu.sip.message.Message request = SipMessageFactory.createNotifyRequest(SipInfo.sipUser, SipInfo.toDev,
                                            SipInfo.user_from, BodyFactory.createCallRequest("operate", devId, userId));
                                    SipInfo.sipUser.sendMessage(request);

                                    Intent intent = new Intent(getActivity(), VedioRequest.class);
                                    intent.putExtra("iconorder", position);
                                    startActivity(intent);
                                }
                            }.start();
                            Looper.loop();
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
    });

    }
    public void showPicture() {

        sendRequestWithOkHttp();
    }

    private void sendRequestWithOkHttp() {
        new Thread( new Runnable() {
            @Override
            public void run() {
                try {
//                    Log.d("1111", "run: ");
                    OkHttpClient client = new OkHttpClient();
                    Request request1 = new Request.Builder()
                            .url("http://"+serverIp+":8000/xiaoyupeihu/public/index.php/devs/getDevInfo?devid=" + SipInfo.devId)
                            .build();
                    Log.d("1111", "run: "+client.newCall(request1).execute().body().string());
                    if (client.newCall(request1).execute().body().string().split("\"groupid\":").length>=2) {
                        groupid = client.newCall(request1).execute().body().string().split("\"groupid\":")[1].split(",\"password\"")[0];
                        Log.d("1111", "run: "+groupid);
                    }
                    Request request2 = new Request.Builder()
                            .url("http://"+serverIp+":8000/xiaoyupeihu/public/index.php/groups/getAllUserFromGroup?groupid=" + groupid)
                            .build();
                    Response response = client.newCall(request2).execute();
                    String responseData = response.body().string();
//                    Log.d("1111", "run:1 ");
                    parseJSONWithGSON(responseData);
//                    Log.d("1111", "run:2 "+responseData);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private void parseJSONWithGSON(String responseData) {
//        Log.d("1111", "run:3 ");
        String jsonData = "[" + responseData.split("\\[")[1].split("\\]")[0] + "]";

        Gson gson = new Gson();
        try {
            devices = gson.fromJson(jsonData, new TypeToken<List<Device>>(){}.getType());
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }

        SipInfo.devList.addAll(devices);
        url = new String[devices.size()];
        Log.e("MemberFragment",!devices.isEmpty()+"" );
        for ( Device device : devices) {
            Log.e("MemberFragment", device.toString());
        }
        if (!devices.isEmpty()){
            handler.sendEmptyMessage(0X111);
        }else
        {
            handler.sendEmptyMessage(0X222);

        }
    }
    @Override
    public void numberUpdate() {
        showPicture();

    }
    private void initView()
    {
        Log.e("showPicture", devices+"");

        adapter=new FarmilyRecyclerViewAdapter(mContext,glm);
        farmily_rv.setAdapter(adapter);
        setEvent();
    }

    public class RecyclerViewSpacesItemDecoration extends RecyclerView.ItemDecoration {

        private HashMap<String, Integer> mSpaceValueMap;

         static final String TOP_DECORATION = "top_decoration";
         static final String BOTTOM_DECORATION = "bottom_decoration";
         static final String LEFT_DECORATION = "left_decoration";
         static final String RIGHT_DECORATION = "right_decoration";
        RecyclerViewSpacesItemDecoration(HashMap<String, Integer> mSpaceValueMap) {
            this.mSpaceValueMap = mSpaceValueMap;
        }
        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            if (mSpaceValueMap.get(TOP_DECORATION) != null)
            {
                outRect.top = mSpaceValueMap.get(TOP_DECORATION);}
            if (mSpaceValueMap.get(LEFT_DECORATION) != null)
            {
                outRect.left = mSpaceValueMap.get(LEFT_DECORATION);}
            if (mSpaceValueMap.get(RIGHT_DECORATION) != null)
            { outRect.right = mSpaceValueMap.get(RIGHT_DECORATION);}
            if (mSpaceValueMap.get(BOTTOM_DECORATION) != null)
            {
                outRect.bottom = mSpaceValueMap.get(BOTTOM_DECORATION);}
        }

    }

}