package com.punuo.sys.app.agedcare.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.handmark.pulltorefresh.library.PullToRefreshRecyclerView;
import com.punuo.sys.app.agedcare.R;
import com.punuo.sys.app.agedcare.R2;
import com.punuo.sys.app.agedcare.adapter.ShortMovieRecyclerViewAdapter;
import com.punuo.sys.app.agedcare.model.ShortMovie;
import com.punuo.sys.app.agedcare.request.GetVideoListRequest;
import com.punuo.sys.sdk.httplib.HttpManager;
import com.punuo.sys.sdk.httplib.RequestListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 23578 on 2018/11/24.
 */

public class ShortMovieFragment extends Fragment {
    @BindView(R2.id.pull_to_refresh)
    PullToRefreshRecyclerView mPullToRefreshRecyclerView;
    private ShortMovieRecyclerViewAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.movie_fragment, container, false);
        ButterKnife.bind(this, view);
        RecyclerView recyclerView = mPullToRefreshRecyclerView.getRefreshableView();
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        adapter = new ShortMovieRecyclerViewAdapter(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        mPullToRefreshRecyclerView.setOnRefreshListener(refreshView -> getVideoList());
        getVideoList();
        return view;
    }

    private void getVideoList() {
        GetVideoListRequest request = new GetVideoListRequest();
        request.setRequestListener(new RequestListener<List<ShortMovie>>() {
            @Override
            public void onComplete() {
                mPullToRefreshRecyclerView.onRefreshComplete();
            }

            @Override
            public void onSuccess(List<ShortMovie> result) {
                if (result != null && !result.isEmpty()) {
                    adapter.appendData(result);
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });
        HttpManager.addRequest(request);
    }
}
