package com.punuo.sys.app.agedcare.vi.activity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.punuo.sys.app.agedcare.R;
import com.punuo.sys.app.agedcare.vi.bean.ViOnlineSongLrc;
import com.punuo.sys.app.agedcare.vi.bean.ViSearchSong;
import com.punuo.sys.app.agedcare.vi.bean.ViSongUrl;
import com.punuo.sys.app.agedcare.vi.bean.VoiceEvent;
import com.punuo.sys.app.agedcare.vi.http.ViAPI;
import com.punuo.sys.app.agedcare.vi.http.ViRequestUtils;
import com.punuo.sys.app.agedcare.vi.utils.ViCommonUtils;
import com.punuo.sys.app.agedcare.vi.view.ViDiscView;
import com.punuo.sys.app.agedcare.vi.view.lrcview.LrcView;

import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class PlayActivity extends VoiceUiActivity implements View.OnClickListener {
    private TextView tvSong, tvSinger;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private SeekBar seekBar;//进度条
    private TextView currentime;//当前时间
    private TextView durTime;//总时间
    private Thread thread;//线程
    private boolean isStop;//线程标志位
    private Button btnStop;
    private ViDiscView discView;
    private ImageView mDiscImg; //唱碟中的歌手头像
    private LrcView lrcView;
    private String lrcString;//歌词字符串
    private String url;
    private ViSearchSong searchSong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        initView();
        url = getIntent().getStringExtra("url");
        searchSong = (ViSearchSong) getIntent().getSerializableExtra("searchSong");
        ViSearchSong.DataBean.SongBean.ListBean listBean = searchSong.getData().getSong().getList().get(0);
        playViSong(url);
        getViSongLrc(listBean.getSongmid());
        setDiscViewPic(ViAPI.ALBUM_PIC + listBean.getAlbummid() + ".jpg");
        tvSinger.setText(listBean.getSinger().get(0).getName());
        tvSong.setText(listBean.getSongname());
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // 将SeekBar位置设置到当前播放位置
            seekBar.setProgress(msg.what);
            lrcView.updateTime(msg.what);
            //获得音乐的当前播放时间
            currentime.setText(ViTimeFormater(msg.what));
        }
    };

    @Subscribe
    public void getMessage(VoiceEvent event) {
        try {
            switch (event.getCode()) {

                case 1004://退出播放
                    finish();
                    hideVoice();
                    break;
                case 1005://暂停播放
                    btnStop.setSelected(false);
                    mediaPlayer.pause();
                    discView.pause();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            hideVoice();
                        }
                    }, 800);

                    break;
                case 1006://继续
                    if (!mediaPlayer.isPlaying()) {
                        btnStop.setSelected(true);
                        mediaPlayer.start();
                        discView.play();
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            hideVoice();
                        }
                    }, 800);

                    break;

            }
        } catch (Exception e) {
            Log.e("exc", e.getMessage());
        }
    }

    public void initView() {
        lrcView = findViewById(R.id.vi_lrcview);
        durTime = findViewById(R.id.vitv_durationtime);
        tvSong = findViewById(R.id.vitv_song);
        tvSinger = findViewById(R.id.vitv_singer);
        seekBar = findViewById(R.id.visb_musicseekbar);
        currentime = findViewById(R.id.vitv_currenttime);
        btnStop = findViewById(R.id.vibtn_palyer);
        discView = findViewById(R.id.vi_discview);
        mDiscImg = findViewById(R.id.viiv_discbackground);

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    btnStop.setSelected(false);
                    mediaPlayer.pause();
                    discView.pause();
                } else {
                    btnStop.setSelected(true);
                    mediaPlayer.start();
                    discView.play();
                }
            }
        });

        lrcView.setDraggable(true, time -> {
            mediaPlayer.seekTo((int) time);
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
                handler.sendEmptyMessage(mediaPlayer.getCurrentPosition());
            }
            return true;
        });

        discView.setOnClickListener(this);
        lrcView.setOnClickListener(this);
        findViewById(R.id.viiv_back).setOnClickListener(this);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                    lrcView.updateTime(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.vi_discview://点击唱片 唱片页面消失 显示歌词
                discView.setVisibility(View.GONE);
                lrcView.setVisibility(View.VISIBLE);
                break;
            case R.id.vi_lrcview://点击歌词 显示唱片
                lrcView.setVisibility(View.GONE);
                discView.setVisibility(View.VISIBLE);
                break;
            case R.id.viiv_back:
                finish();
                break;
        }
    }

    private void setDiscViewPic(String url) {

        Glide.with(this)
                .load(url)
                .placeholder(R.drawable.welcome)
                .error(R.drawable.welcome)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        mDiscImg.setImageDrawable(resource);
                        int marginTop = (int) (ViCommonUtils.SCALE_DISC_MARGIN_TOP * ViCommonUtils.getScreenHeight(PlayActivity.this));
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mDiscImg
                                .getLayoutParams();
                        layoutParams.setMargins(0, marginTop, 0, 0);

                        mDiscImg.setLayoutParams(layoutParams);
                        return false;
                    }
                });
    }

    private void playViSong(String musicUrl) {
        isStop = false;

        //重置，当切换音乐时不会放前一首歌的歌曲
        mediaPlayer.reset();
        try {
            // 设置音乐播放源
            mediaPlayer.setDataSource(musicUrl);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    // 启动
                    mediaPlayer.start();
                    discView.play();
                    // 设置seekbar的最大值
                    seekBar.setMax(mediaPlayer.getDuration());
                    durTime.setText(ViTimeFormater(mediaPlayer.getDuration()));
                    lrcView.loadLrc(TextUtils.isEmpty(lrcString) ? "" : lrcString);
                    btnStop.setSelected(true);
                    // 创建一个线程
                    thread = new Thread(new ViMuiscThread());
                    // 启动线程
                    thread.start();
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    lrcView.updateTime(0);
                    seekBar.setProgress(0);
                }
            });
            // 准备
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            Log.e("error", e.getMessage());
            e.printStackTrace();
        }
    }

    //建立一个子线程实现Runnable接口
    class ViMuiscThread implements Runnable {
        @Override
        //实现run方法
        public void run() {
            //判断音乐的状态，在不停止与不暂停的情况下向总线程发出信息
            while (mediaPlayer != null && isStop == false) {
                try {
                    // 每100毫秒更新一次位置
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    //发出的信息
                    if (mediaPlayer.isPlaying()) {
                        handler.sendEmptyMessage(mediaPlayer.getCurrentPosition());
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }
        }
    }

    //时间转换类，将得到的音乐时间毫秒转换为时分秒格式
    private String ViTimeFormater(int lengrh) {
        Date date = new Date(lengrh);
        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
        String totalTime = sdf.format(date);
        return totalTime;
    }

    public void getViSongUrl(String mid) {
        ViRequestUtils.getSongPlayUrl(this, ViAPI.SONG_URL_DATA_LEFT + mid + ViAPI.SONG_URL_DATA_RIGHT, new Observer<ViSongUrl>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ViSongUrl songUrl) {
                ViSongUrl.Req0Bean.DataBean data = songUrl.getReq_0().getData();
                List<String> sip = data.getSip();
                String PlayUrl = sip.get(0) + data.getMidurlinfo().get(0).getPurl();
                playViSong(PlayUrl);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    public void getViSongLrc(String mid) {
        ViRequestUtils.getSongLrc(this, mid, new Observer<ViOnlineSongLrc>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ViOnlineSongLrc onlineSongLrc) {
                if (TextUtils.isEmpty(onlineSongLrc.getLyric())) {
                    Toast.makeText(PlayActivity.this, "抱歉暂无歌词", Toast.LENGTH_SHORT).show();
                } else {
                    lrcString = onlineSongLrc.getLyric();
                }
                lrcView.loadLrc(TextUtils.isEmpty(lrcString) ? "" : lrcString);


            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isStop = true;
        if (thread != null) {
            thread.interrupt();
        }
        handler.removeCallbacksAndMessages(null);
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}
