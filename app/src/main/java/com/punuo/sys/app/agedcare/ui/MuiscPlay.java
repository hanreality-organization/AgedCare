package com.punuo.sys.app.agedcare.ui;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.danikula.videocache.HttpProxyCacheServer;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.punuo.sys.app.agedcare.R;
import com.punuo.sys.app.agedcare.application.AppContext;
import com.punuo.sys.app.agedcare.model.Music;
import com.punuo.sys.app.agedcare.model.Musicitem;
import com.punuo.sys.app.agedcare.view.CircleImageView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import static com.punuo.sys.app.agedcare.sip.SipInfo.lastmusictype;
import static com.punuo.sys.app.agedcare.sip.SipInfo.musicitems;
import static com.punuo.sys.app.agedcare.sip.SipInfo.musics;
import static com.punuo.sys.app.agedcare.sip.SipInfo.serverIp;

public class MuiscPlay extends Fragment {

    @Bind(R.id.CIV_avatar)
    CircleImageView CIVAvatar;
    @Bind(R.id.songname)
    TextView songname;
    @Bind(R.id.kuaitui)
    ImageView kuaitui;
    @Bind(R.id.stop)
    ImageView stop;
    @Bind(R.id.kuaijin)
    ImageView kuaijin;
    @Bind(R.id.recycler_view)
    RecyclerView recycler_view;
    @Bind(R.id.activity_muisc_play)
    LinearLayout activityMuiscPlay;
    @Bind(R.id.seekbar)
   SeekBar  mSeekBar;
    @Bind(R.id.text1)
    TextView mTextView;
//    private SeekBar mSeekBar;
//    private TextView mTextView;
    private Handler mHandler = new Handler();
    MusicitemAdapter musicitemAdapter;
    public MediaPlayer mMediaPlayer = new MediaPlayer();
    private int pausePosition;
    private int currentMusicIndex;
//    AppContext myApplication;
    HttpProxyCacheServer proxy;
    String proxyUrl;
    private final String typepath = "http://" + serverIp + ":8000/xiaoyupeihu/public/index.php/music/getMusicType";

    //进度条下面的当前进度文字，将毫秒化为m:ss格式
    private SimpleDateFormat time = new SimpleDateFormat("m:ss", Locale.CHINESE);

    //“绑定”服务的intent
    boolean isplay;
    public String TAG = "MusicPlay";
    private Handler setAdapterHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what) {
                case 0X222:
                    musicitemAdapter = new MusicitemAdapter(getActivity(), CIVAvatar);
                    recycler_view.setAdapter(musicitemAdapter);
                    break;
                case 0X111:
                    mMediaPlayer.reset();
                    Log.e("clicktype", musics.toString());
                    currentMusicIndex = 0;
                    iniMediaPlayerFile(currentMusicIndex);
                    play();
                    break;
            }
            return false;
        }
    });
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_muisc_play, container, false);
        ButterKnife.bind(this,view);
        EventBus.getDefault().register(this);
        getmusic();
        mSeekBar.setOnSeekBarChangeListener(new MySeekBar());
        mMediaPlayer.setOnCompletionListener(new InnerOnCompletionListener());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recycler_view.setLayoutManager(linearLayoutManager);
        recycler_view.addItemDecoration(new SpaceItemDecoration(32, 0));
//        myApplication = (AppContext) getApplication();
        proxy = AppContext.getProxy(getActivity());
        Log.d(TAG, currentMusicIndex + "aa");
        return view;
    }

    //缓存歌曲
    private void initAudioCache(String musicUrl) {
        proxyUrl = proxy.getProxyUrl(musicUrl, true);
//        if (proxy.isCached(musicUrl)) {
//            Toast.makeText(getActivity(), "已缓存", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(getActivity(), "未缓存", Toast.LENGTH_SHORT).show();
//        }
    }

    //获取到权限回调方法
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    bindService(MediaServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
                    iniMediaPlayerFile(currentMusicIndex);
                } else {
                    Toast.makeText(getActivity(), "权限不够获取不到音乐，程序将退出", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
                break;
            default:
                break;
        }
    }


    @OnClick({R.id.songname, R.id.kuaitui, R.id.stop, R.id.kuaijin,})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.stop:

                if (!isplay) {
                    stop.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.zaiting1));
                    play();


                } else {
                    stop.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.play1));
                    pause();
//                    isplay = false;
                }
                break;
            case R.id.kuaijin:
                next();
                break;
            case R.id.kuaitui:
                previous();
                break;
        }
    }

    /*进度条处理*/
    public class MySeekBar implements SeekBar.OnSeekBarChangeListener {

        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {

        }

        /*滚动时,应当暂停后台定时器*/
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        /*滑动结束后，重新设置值*/
        public void onStopTrackingTouch(SeekBar seekBar) {

            mMediaPlayer.seekTo(seekBar.getProgress());
        }

    }


    //暂停
    private void pause() {
        //直接调用MediaPlay 中的暂停方法
        if (mMediaPlayer.isPlaying()) {
            //如果还没开始播放，就开始
            mMediaPlayer.pause();
        }
        //获取暂停的位置（音乐进度）
        pausePosition = mMediaPlayer.getCurrentPosition();
        //切换为播放的按钮（按钮为android系统自带的按钮，可直接用）
        isplay = false;
    }

    //播放上一曲
    private void previous() {
        if (musics != null) {
            //判断是否为第一首歌曲，若为第一首歌曲，则播放最后一首
            //当前音乐播放位置--（上一曲）
            currentMusicIndex--;
            Log.d(TAG, currentMusicIndex + "previous");
            if (currentMusicIndex < 0) {
//                Toast.makeText(this, "已经是第一首了", Toast.LENGTH_SHORT).show();
                mMediaPlayer.reset();
                stop.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.zaiting1));
                currentMusicIndex = musics.size() - 1;
                iniMediaPlayerFile(currentMusicIndex);
                Log.d(TAG, currentMusicIndex + "previous111");
                //音乐进度置为0
                pausePosition = 0;
                //播放
                play();
            } else {
                mMediaPlayer.reset();
                stop.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.zaiting1));
                iniMediaPlayerFile(currentMusicIndex);
                Log.d(TAG, currentMusicIndex + "previous111");
                //音乐进度置为0
                pausePosition = 0;
                //播放
                play();
            }
        }
    }

    //播放下一曲（与上一曲类似）
    private void next() {
        if (musics != null) {
            currentMusicIndex++;
            Log.d(TAG, currentMusicIndex + "next");
            if (currentMusicIndex >= musics.size()) {
//                Toast.makeText(this, "已经是最后一首了", Toast.LENGTH_SHORT).show();
                pausePosition = 0;
                mMediaPlayer.reset();
                stop.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.zaiting1));
                currentMusicIndex = 0;
                Log.d(TAG, currentMusicIndex + "eee");
                iniMediaPlayerFile(currentMusicIndex);
                play();
            } else {
                pausePosition = 0;
                mMediaPlayer.reset();
                stop.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.zaiting1));
                iniMediaPlayerFile(currentMusicIndex);
                play();
            }
        }
    }


    //播放音乐
    private void play() {
        stop.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.zaiting1));
        if (musics != null) {
            try {
                //设置音乐文件来源
//                        mMediaPlayer.setDataSource(musics.get(currentMusicIndex).getPath());
//                        //准备（缓冲文件）
//                        mMediaPlayer.prepare();
                //将进度设置到“音乐进度”
                if (!mMediaPlayer.isPlaying()) {
                    //播放开始
                    mMediaPlayer.start();
                    pausePosition = mMediaPlayer.getCurrentPosition();
                    mMediaPlayer.seekTo(pausePosition);
                    //获取音乐进度


                    //用当前界面的 TextView显示当前播放的音乐
                    songname.setText(musics.get(currentMusicIndex).getName());
                    mSeekBar.setMax(mMediaPlayer.getDuration());

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        isplay = true;
    }

    private final class InnerOnCompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            next();

        }
    }

    private void iniMediaPlayerFile(int dex) {
        try {
            Log.d(TAG, "http://" + serverIp + ":8000/static/music/" + musics.get(dex).getTime() + ".mp3");
            //获取文件路径
            initAudioCache("http://" + serverIp + ":8000/static/music/" + musics.get(dex).getTime() + ".mp3");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {

            Log.d(TAG, proxyUrl + "");
            mMediaPlayer.setDataSource(proxyUrl);
            //让MediaPlayer对象准备
            mMediaPlayer.prepare();
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {
                    mSeekBar.setMax(mMediaPlayer.getDuration());
                    mHandler.post(mRunnable);
//                    musicLength.setText(format.format(mediaPlayer.getDuration())+"");
                    mTextView.setText(R.string.zero_time);

                }
            });

        } catch (ClassCastException e) {
            e.printStackTrace();
        } catch (Exception e) {
            Log.d(TAG, "设置资源，准备阶段出错");
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
        mHandler.removeCallbacks(mRunnable);
        musics.clear();
        EventBus.getDefault().unregister(this);
        getActivity().finish();
    }

    /**
     * 更新ui的runnable
     */
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mSeekBar.setProgress(mMediaPlayer.getCurrentPosition());
            mTextView.setText(time.format(mMediaPlayer.getCurrentPosition()));
            mHandler.postDelayed(mRunnable, 1000);
        }
    };

    public void getmusic() {

        sendRequestWithOkHttp();

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
                    Log.e("musicplay", "run:1 " + client.newCall(request1).execute().body().string());

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
        Log.d("1111", "run:2" + jsonData);
        Gson gson = new Gson();
        try {
            musicitems = gson.fromJson(jsonData, new TypeToken<List<Musicitem>>() {
            }.getType());
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }

        Log.d(TAG + "11", "" + musicitems.size());
        Log.d("11111", "run:4" + musicitems);
        new Thread(new Runnable() {
            @Override
            public void run() {
                setAdapterHandler.sendEmptyMessage(0X222);
            }
        }).start();

    }

    public class MusicitemAdapter extends RecyclerView.Adapter<MusicitemAdapter.ViewHolder> {
        //        private List<Musicitem> mMusicitemList;
        ImageView imageView;
        private Context context;

        private String TAG = "musicplayer";

        public class ViewHolder extends RecyclerView.ViewHolder {
            View musicView;
            ImageView musicimage;

            public ViewHolder(View view) {
                super(view);
                musicView = view;
                musicimage = (ImageView) view.findViewById(R.id.music_item);
            }
        }


         MusicitemAdapter(Context context, ImageView imageView) {

            this.imageView = imageView;
            this.context = context;

        }

        @NonNull
        @Override
        public MusicitemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.musicitem, parent, false);
            final MusicitemAdapter.ViewHolder holder = new MusicitemAdapter.ViewHolder(view);
            ImageLoader.getInstance().displayImage("http://" + serverIp + ":8000/static/musicListCover/" + musicitems.get(lastmusictype).getType() + ".png", imageView);

            holder.musicimage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = holder.getAdapterPosition();
                    ImageLoader.getInstance().displayImage("http://" + serverIp + ":8000/static/musicListCover/" + musicitems.get(position).getType() + ".png", imageView);
                    sendMusicRequestWithOkHttp("http://" + serverIp + ":8000/xiaoyupeihu/public/index.php/music/getMusicList?type=" + musicitems.get(position).getType());
                    lastmusictype = position;


                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(MusicitemAdapter.ViewHolder holder, int position) {
            Log.d(TAG, musicitems.size() + "");
//            holder.musicimage.setImageResource(musicitem.getImagedId());

            Log.d(TAG, "geturl");
            Log.d(TAG, musicitems.get(position).getType());
            ImageLoader.getInstance().displayImage("http://" + serverIp + ":8000/static/musicListCover/" + musicitems.get(position).getType() + ".png", holder.musicimage);
            sendMusicRequestWithOkHttp("http://" + serverIp + ":8000/xiaoyupeihu/public/index.php/music/getMusicList?type=" + musicitems.get(lastmusictype).getType());

        }

        @Override
        public int getItemCount() {
            return musicitems.size();
        }
    }

    private void sendMusicRequestWithOkHttp(final String path) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("2222", "run: ");
                    OkHttpClient client = new OkHttpClient();
                    Request request1 = new Request.Builder()
                            .url(path)
                            .build();
                    Log.d("2222", "run:1 " + client.newCall(request1).execute().body().string());
                    Response response = client.newCall(request1).execute();
                    String responseData = response.body().string();
                    String jsonData = "[" + responseData.split("\\[")[1].split("\\]")[0] + "]";
                    Gson gson = new Gson();
                    try {
                        musics = gson.fromJson(jsonData, new TypeToken<List<Music>>() {
                        }.getType());
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            setAdapterHandler.sendEmptyMessage(0X111);
                        }
                    }).start();
                    Log.d(TAG + "22", "" + musics.size());
                    Log.d("2222", "run:4" + musics);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }


    public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

        private int leftRight;
        private int topBottom;

        //leftRight为横向间的距离 topBottom为纵向间距离
      SpaceItemDecoration(int leftRight, int topBottom) {
            this.leftRight = leftRight;
            this.topBottom = topBottom;
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            super.onDraw(c, parent, state);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
            //竖直方向的
            if (layoutManager.getOrientation() == LinearLayoutManager.VERTICAL) {
                //最后一项需要 bottom
                if (parent.getChildAdapterPosition(view) == layoutManager.getItemCount() - 1) {
                    outRect.bottom = topBottom;
                }
                outRect.top = topBottom;
                outRect.left = leftRight;
                outRect.right = leftRight;
            } else {
                //最后一项需要right
                if (parent.getChildAdapterPosition(view) == layoutManager.getItemCount() - 1) {
                    outRect.right = leftRight;
                }
                outRect.top = topBottom;
                outRect.left = leftRight;
                outRect.bottom = topBottom;
            }
        }

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        switch (event.getMessage())
        {
            case "等待通话":
                stop.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.play1));
                pause();
                break;
            case "movieplaying":
                stop.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.play1));
                pause();
                break;
            case "callstart":
                stop.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.play1));
                pause();
                break;
            default:
                break;
        }

    }
}
