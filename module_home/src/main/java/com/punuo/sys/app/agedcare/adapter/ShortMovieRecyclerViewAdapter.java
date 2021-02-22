package com.punuo.sys.app.agedcare.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.punuo.sys.app.agedcare.R;
import com.punuo.sys.app.agedcare.model.ShortMovie;
import com.punuo.sys.sdk.activity.BaseActivity;
import com.punuo.sys.sdk.httplib.HttpConfig;

import java.util.ArrayList;
import java.util.List;

import tcking.github.com.giraffeplayer.GiraffePlayerActivity;

/**
 * Created by 23578 on 2018/11/25.
 */

public class ShortMovieRecyclerViewAdapter extends RecyclerView.Adapter<ShortMovieRecyclerViewAdapter.MyViewHolder> {

    private Context mContext;
    private List<ShortMovie> mShortMovies = new ArrayList<>();

    public ShortMovieRecyclerViewAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void appendData(List<ShortMovie> movies) {
        mShortMovies.clear();
        mShortMovies.addAll(movies);
        notifyDataSetChanged();
    }

    @Override
    @NonNull
    public ShortMovieRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.rv_item_shortmovie, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ShortMovieRecyclerViewAdapter.MyViewHolder myViewHolder, final int i) {
        myViewHolder.bind(mShortMovies.get(i));
    }

    @Override
    public int getItemCount() {
        return mShortMovies.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView textView;
        private final TextView movieInfo;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.movie_item);
            textView = (TextView) itemView.findViewById(R.id.movie_name);
            movieInfo = (TextView) itemView.findViewById(R.id.info);
        }

        public void bind(ShortMovie item) {
            textView.setText(item.title);
            movieInfo.setText(item.info);
            Glide.with(itemView.getContext())
                    .load("http://" + HttpConfig.getHost() + ":8000/static/videoListCover/" + item.cover + ".png")
                    .apply(new RequestOptions().error(R.drawable.testcover)).into(imageView);
            imageView.setOnClickListener(v -> {
                GiraffePlayerActivity.configPlayer((BaseActivity) itemView.getContext()).
                        setTitle(item.title)
                        .play("http://" + HttpConfig.getHost() + ":8000/static/video/" + item.id + ".mp4");
            });
        }
    }

    /**
     * 子项点击接口
     */
    public interface OnItemClickListener {
        void onClick(View view, int position);

    }

    public interface OnLongItemClickListener {
        void onLongClick(View view, int position);
    }
}
