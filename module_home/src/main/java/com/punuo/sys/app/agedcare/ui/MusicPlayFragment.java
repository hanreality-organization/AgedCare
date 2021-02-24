package com.punuo.sys.app.agedcare.ui;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.danikula.videocache.HttpProxyCacheServer;
import com.danikula.videocache.file.FileNameGenerator;
import com.punuo.sys.app.agedcare.R;
import com.punuo.sys.app.agedcare.R2;
import com.punuo.sys.app.agedcare.request.GetAllMusicTypeRequest;
import com.punuo.sys.app.agedcare.request.GetMusicListRequest;
import com.punuo.sys.app.agedcare.request.model.MusicItem;
import com.punuo.sys.app.agedcare.request.model.MusicType;
import com.punuo.sys.sdk.PnApplication;
import com.punuo.sys.sdk.event.CloseOtherMediaEvent;
import com.punuo.sys.sdk.fragment.BaseFragment;
import com.punuo.sys.sdk.httplib.HttpConfig;
import com.punuo.sys.sdk.httplib.HttpManager;
import com.punuo.sys.sdk.httplib.RequestListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MusicPlayFragment extends BaseFragment {

    @BindView(R2.id.CIV_avatar)
    ImageView CIVAvatar;
    @BindView(R2.id.songname)
    TextView mSongName;
    @BindView(R2.id.kuaitui)
    ImageView kuaitui;
    @BindView(R2.id.stop)
    ImageView stop;
    @BindView(R2.id.kuaijin)
    ImageView kuaijin;
    @BindView(R2.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R2.id.seekbar)
    SeekBar mSeekBar;
    @BindView(R2.id.text1)
    TextView mTextView;
    private final Handler mHandler = new Handler();
    MusicTypeAdapter mMusicTypeAdapter;
    public MediaPlayer mMediaPlayer = new MediaPlayer();
    private int seekBarProgress;
    private int currentMusicIndex;
    private final SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("m:ss", Locale.CHINESE);
    private boolean isPlaying;
    public String TAG = "MusicPlay";
    private HttpProxyCacheServer proxy;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_muisc_play, container, false);
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        mSeekBar.setOnSeekBarChangeListener(new MySeekBar());
        mMediaPlayer.setOnCompletionListener(new InnerOnCompletionListener());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mMusicTypeAdapter = new MusicTypeAdapter(getActivity());
        mRecyclerView.setAdapter(mMusicTypeAdapter);
        proxy = new HttpProxyCacheServer
                    .Builder(getActivity())
                    .maxCacheFilesCount(300)
                    .cacheDirectory(new File(PnApplication.getInstance().getExternalFilesDir("music"), "audio-cache"))
                    .fileNameGenerator(new FileNameGenerator() {
                        @Override
                        public String generate(String url) {
                            return url;
                        }
                    }).build();
        getAllMusicType();
        return view;
    }


    @OnClick({R2.id.songname, R2.id.kuaitui, R2.id.stop, R2.id.kuaijin,})
    public void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.stop) {
            if (!isPlaying) {
                play();
            } else {

                pause();
            }
        } else if (id == R.id.kuaijin) {
            next();
        } else if (id == R.id.kuaitui) {
            previous();
        }
    }

    /*进度条处理*/
    public class MySeekBar implements SeekBar.OnSeekBarChangeListener {

        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
        }

        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            mMediaPlayer.seekTo(seekBar.getProgress());
        }

    }

    //暂停
    public void pause() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
        seekBarProgress = mMediaPlayer.getCurrentPosition();
        isPlaying = false;
        stop.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.play1));
    }

    //播放上一曲
    private void previous() {
        if (mMusicItems != null && !mMusicItems.isEmpty()) {
            currentMusicIndex--;
            seekBarProgress = 0;
            stop.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.zaiting1));
            if (currentMusicIndex < 0) {
                currentMusicIndex = mMusicItems.size() - 1;
            }
            mSongName.setText(mMusicItems.get(currentMusicIndex).songName);
            iniMediaPlayerFile(currentMusicIndex);
        }
    }

    //播放下一曲
    private void next() {
        if (mMusicItems != null && !mMusicItems.isEmpty()) {
            currentMusicIndex++;
            seekBarProgress = 0;
            stop.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.zaiting1));
            if (currentMusicIndex >= mMusicItems.size()) {
                currentMusicIndex = 0;
            }
            mSongName.setText(mMusicItems.get(currentMusicIndex).songName);
            iniMediaPlayerFile(currentMusicIndex);
        }
    }

    //播放音乐
    public void play() {
        if (mMusicItems != null && !mMusicItems.isEmpty()) {
            try {
                if (!mMediaPlayer.isPlaying()) {
                    mMediaPlayer.start();
                    seekBarProgress = mMediaPlayer.getCurrentPosition();
                    mMediaPlayer.seekTo(seekBarProgress);
                    mSeekBar.setMax(mMediaPlayer.getDuration());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        isPlaying = true;
        stop.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.zaiting1));
    }

    private final class InnerOnCompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            next();
        }
    }

    private void iniMediaPlayerFile(int index) {
        try {
            String musicUrl = "http://" + HttpConfig.getHost() + ":8000/static/music/" + mMusicItems.get(index).time + ".mp3";
            String proxyUrl = proxy.getProxyUrl(musicUrl, true);
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(proxyUrl);
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnPreparedListener(mp -> {
                mSeekBar.setMax(mMediaPlayer.getDuration());
                mHandler.post(updateSeekBar);
                mTextView.setText(R.string.zero_time);
                play();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }
        mHandler.removeCallbacks(updateSeekBar);
        EventBus.getDefault().unregister(this);
    }

    /**
     * 更新ui的runnable
     */
    private final Runnable updateSeekBar = new Runnable() {
        @Override
        public void run() {
            mSeekBar.setProgress(mMediaPlayer.getCurrentPosition());
            mTextView.setText(mSimpleDateFormat.format(mMediaPlayer.getCurrentPosition()));
            mHandler.postDelayed(updateSeekBar, 1000);
        }
    };

    public void getAllMusicType() {
        GetAllMusicTypeRequest request = new GetAllMusicTypeRequest();
        request.setRequestListener(new RequestListener<List<MusicType>>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onSuccess(List<MusicType> result) {
                if (result != null) {
                    mMusicTypeAdapter.appendData(result);
                    getMusicList(result.get(0).type);
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });
        HttpManager.addRequest(request);
    }

    private List<MusicItem> mMusicItems;

    private void getMusicList(String type) {
        GetMusicListRequest request = new GetMusicListRequest();
        request.addUrlParam("type", type);
        request.setRequestListener(new RequestListener<List<MusicItem>>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onSuccess(List<MusicItem> result) {
                mMusicItems = result;
                if (mMusicItems != null && !mMusicItems.isEmpty()) {
                    Glide.with(getContext()).load("http://" + HttpConfig.getHost() + ":8000/static/musicListCover/" + mMusicItems.get(0).type + ".png")
                            .into(CIVAvatar);
                    mSongName.setText(mMusicItems.get(0).songName);
                    resetPlayEngine();
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });
        HttpManager.addRequest(request);
    }

    private void resetPlayEngine() {
        currentMusicIndex = 0;
        iniMediaPlayerFile(0);
    }

    public class MusicTypeAdapter extends RecyclerView.Adapter<MusicTypeAdapter.ViewHolder> {
        private final Context mContext;
        private final List<MusicType> mMusicTypes;

        public class ViewHolder extends RecyclerView.ViewHolder {
            View musicView;
            ImageView musicImage;

            public ViewHolder(View view) {
                super(view);
                musicView = view;
                musicImage = (ImageView) view.findViewById(R.id.music_item);
            }
        }

        public MusicTypeAdapter(Context context) {
            this.mContext = context;
            mMusicTypes = new ArrayList<>();
        }

        public void appendData(List<MusicType> musicTypes) {
            mMusicTypes.clear();
            mMusicTypes.addAll(musicTypes);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public MusicTypeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.musicitem, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MusicTypeAdapter.ViewHolder holder, int position) {
            final MusicType musicType = mMusicTypes.get(position);
            Glide.with(mContext).load("http://" + HttpConfig.getHost() + ":8000/static/musicListCover/" + musicType.type + ".png")
                    .into(holder.musicImage);
            holder.musicImage.setOnClickListener(v -> getMusicList(musicType.type));
        }

        @Override
        public int getItemCount() {
            return mMusicTypes.size();
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(CloseOtherMediaEvent event) {
        stop.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.play1));
        pause();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }
}
