package com.punuo.sys.app.agedcare.ui;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.punuo.sys.app.agedcare.R;
import com.punuo.sys.app.agedcare.adapter.ShortMovieRecyclerViewAdapter;
import com.punuo.sys.app.agedcare.model.ShortMovie;
import org.greenrobot.eventbus.EventBus;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import tcking.github.com.giraffeplayer.GiraffePlayerActivity;
import static com.punuo.sys.app.agedcare.sip.SipInfo.movies;
import static com.punuo.sys.app.agedcare.sip.SipInfo.serverIp;

/**
 * Created by 23578 on 2018/11/24.
 */

public class ShortMovieFragment extends Fragment {

    @Bind(R.id.rv_short_movie)
    RecyclerView rv_short_movie;
    @Bind(R.id.gank_swipe_refresh_layout)
    SwipeRefreshLayout gank_swipe_refresh_layout;
    private Context mContext;
    private ShortMovieRecyclerViewAdapter adapter;
    GridLayoutManager glm;
    HashMap<String, Integer> stringIntegerHashMap = new HashMap<>();
    private static final int REFRESH_COMPLETE = 0x01;
    private final String typepath = "http://" + serverIp + ":8000/xiaoyupeihu/public/index.php/video/getVideoList";
    private Handler setAdapterHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what) {
                case 0X222:
                    initView();
                    setEvent();
                    break;
                case REFRESH_COMPLETE:
                    gank_swipe_refresh_layout.setRefreshing(false);
                    break;

            }
            return false;
        }
    });
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.moviefragment, container, false);
        ButterKnife.bind(this, view);
        mContext = this.getActivity();
        putdata();
        glm=new GridLayoutManager(mContext,2);
        rv_short_movie.setLayoutManager(glm);
        stringIntegerHashMap.put(MemberFragment.RecyclerViewSpacesItemDecoration.TOP_DECORATION,15);//top间距
        stringIntegerHashMap.put(MemberFragment.RecyclerViewSpacesItemDecoration.BOTTOM_DECORATION,10);//底部间距
        stringIntegerHashMap.put(MemberFragment.RecyclerViewSpacesItemDecoration.LEFT_DECORATION,15);//左间距
        stringIntegerHashMap.put(MemberFragment.RecyclerViewSpacesItemDecoration.RIGHT_DECORATION,15);//右间距
        rv_short_movie.addItemDecoration(new ShortMovieFragment.RecyclerViewSpacesItemDecoration(stringIntegerHashMap));
        gank_swipe_refresh_layout.setSize(SwipeRefreshLayout.LARGE);
        gank_swipe_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // do something, such as re-request from server or other
                putdata();
                adapter.notifyDataSetChanged();
                setAdapterHandler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 1500);
            }

        });

        return view;
    }

    private void initView() {


        adapter = new ShortMovieRecyclerViewAdapter(mContext, glm);
        rv_short_movie.setAdapter(adapter);


    }

    private void putdata() {

        sendRequestWithOkHttp();
    }

    private void setEvent() {
        adapter.setmOnItemClickListener(new ShortMovieRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, final int position) {

                GiraffePlayerActivity.configPlayer(getActivity()).setTitle(movies.get(position).getTitle()).play("http://" + serverIp + ":8000/static/video/" + movies.get(position).getId() + ".mp4");
                EventBus.getDefault().post(new MessageEvent("movieplaying"));
            }
        });
    }
    private void sendRequestWithOkHttp() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("1111", "run: ");
                    OkHttpClient client = new OkHttpClient();
                    Request request1 = new Request.Builder()
                            .url(typepath)
                            .build();
                    Log.d("1111", "run:1 " + client.newCall(request1).execute().body().string());

                    Response response = client.newCall(request1).execute();
                    String responseData = response.body().string();

                    parseJSONWithGSON(responseData);
                    Log.d("1111", "run:3 " + responseData);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private void parseJSONWithGSON(String responseData) {
//        Log.d("1111", "run:3 ");
        String jsonData = "[" + responseData.split("\\[")[1].split("\\]")[0] + "]";
        Log.d("3333", "run:2" + jsonData);
        Gson gson = new Gson();

        try {
            movies = gson.fromJson(jsonData, new TypeToken<List<ShortMovie>>(){}.getType());
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }

        Log.d("3333", "run:4" + movies);
        new Thread(new Runnable() {
            @Override
            public void run() {
                setAdapterHandler.sendEmptyMessage(0X222);
            }
        }).start();

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(!movies.isEmpty())
        {

            movies.clear();
        }
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
