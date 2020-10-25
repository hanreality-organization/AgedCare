package com.punuo.sys.app.agedcare.vi.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;
//import com.blankj.utilcode.util.LogUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.punuo.sys.app.agedcare.R;
import com.punuo.sys.app.agedcare.http.LogUtils;
import com.punuo.sys.app.agedcare.vi.bean.TuLingRequest;
import com.punuo.sys.app.agedcare.vi.bean.TuLingResult;
import com.punuo.sys.app.agedcare.vi.bean.ViRobotBean;
import com.punuo.sys.app.agedcare.vi.bean.ViSearchSong;
import com.punuo.sys.app.agedcare.vi.bean.ViSongUrl;
import com.punuo.sys.app.agedcare.vi.http.ViAPI;
import com.punuo.sys.app.agedcare.vi.http.ViRequestUtils;
import com.punuo.sys.app.agedcare.vi.view.ViAudioWaveView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.RequestBody;


public class ChatActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private BaseQuickAdapter<ViRobotBean, BaseViewHolder> adapter;
    private List<ViRobotBean> robotBeans = new ArrayList<>();
    private ImageView ivSpeak;
    private ViAudioWaveView ivSpeaking;
    private EventManager asr;
    private EventManager wp;
    private SpeechSynthesizer mSpeechSynthesizer;
    private Intent serverIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("ChatActivity", " - onResume");
        requestPermission();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("ChatActivity", " - onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("ChatActivity", " - onDestroy");
    }

    private void initWp() {
        wp = EventManagerFactory.create(this, "wp");
        EventListener wpListener = new EventListener() {
            @Override
            public void onEvent(String name, String params, byte[] data, int offset, int length) {
                Log.d("ChatActivity", String.format("event: name=%s, params=%s", name, params));
                // 唤醒
                if (name.equals("wp.data")) {
                    try {
                        JSONObject json = new JSONObject(params);
                        int errorCode = json.getInt("errorCode");
                        if (errorCode == 0) {
                            // 唤醒成功
                            Log.i("ChatActivity", " - 唤醒成功");
                            wp.send(SpeechConstant.WAKEUP_STOP, null, null, 0, 0);
                            ivSpeak.setVisibility(View.GONE);
                            ivSpeaking.setVisibility(View.VISIBLE);
                            robotBeans.add(new ViRobotBean("2", "请问有什么可以帮到您?"));
                            adapter.setNewData(robotBeans);
                            // 滚动调整
                            recyclerView.scrollToPosition(adapter.getItemCount());
                            speakTTS("请问有什么可以帮到您?");
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            initAsr();
                        } else {
                            Log.i("ChatActivity", " - 唤醒失败");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if ("wp.exit".equals(name)) {
                    Log.i("ChatActivity", " - 唤醒停止");
                }
            }
        };
        wp.registerListener(wpListener);
        HashMap hashMap = new HashMap();
        hashMap.put(SpeechConstant.WP_WORDS_FILE, "assets://WakeUp.bin");
        String json = null;
        json = new JSONObject(hashMap).toString();
        wp.send(SpeechConstant.WAKEUP_START, json, null, 0, 0);
    }

    public void requestPermission() {
        XXPermissions.with(this).permission(Permission.RECORD_AUDIO, Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE)
                .request(new OnPermission() {
                    @Override
                    public void hasPermission(List<String> granted, boolean all) {
                        if (all) {
                            initWp();
                        }
                    }

                    @Override
                    public void noPermission(List<String> denied, boolean quick) {
                        if (quick) {
                            XXPermissions.gotoPermissionSettings(ChatActivity.this);
                        }
                    }
                });
    }

    /*
     * 判断服务是否启动,context上下文对象 ，className服务的name
     */
    public static boolean isServiceRunning(Context mContext, String className) {

        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager
                .getRunningServices(30);

        if (!(serviceList.size() > 0)) {
            return false;
        }
        Log.e("OnlineService：", className);
        for (int i = 0; i < serviceList.size(); i++) {
            Log.e("serviceName：", serviceList.get(i).service.getClassName());
            if (serviceList.get(i).service.getClassName().contains(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    public void initView() {
        ivSpeak = findViewById(R.id.viiv_speak);
        ivSpeaking = findViewById(R.id.viiv_speaking);
        ivSpeaking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivSpeak.setVisibility(View.VISIBLE);
                ivSpeaking.setVisibility(View.GONE);
            }
        });
        ivSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initAsr();
                ivSpeak.setVisibility(View.GONE);
                ivSpeaking.setVisibility(View.VISIBLE);
            }
        });
        //文字消息定位在最后
        recyclerView = findViewById(R.id.virv_chat);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new BaseQuickAdapter<ViRobotBean, BaseViewHolder>(R.layout.layout_voicechat) {
            @Override
            protected void convert(BaseViewHolder helper, ViRobotBean item) {
                TextView view = helper.getView(R.id.vitv_voicechat);
                helper.setText(R.id.vitv_voicechat, item.getText());
                switch (item.getType()) {
                    case "1":
                        view.setGravity(Gravity.RIGHT);
                        break;
                    case "2":
                        view.setGravity(Gravity.LEFT);
                        break;
                }
            }
        };
        recyclerView.setAdapter(adapter);

        adapter.setNewData(robotBeans);
        recyclerView.scrollToPosition(adapter.getItemCount() - 1);
    }

    private void initAsr() {
        asr = EventManagerFactory.create(this, "asr");
        EventListener asrListener = new EventListener() {
            @Override
            public void onEvent(String name, String params, byte[] data, int offset, int length) {
                Log.d("ChatActivity", String.format("event: name=%s, params=%s", name, params));
                if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_READY)) {
                    // 引擎就绪，可以说话，一般在收到此事件后通过UI通知用户可以说话了
                    Log.i("ChatActivity", " - 开始说话");

                }
                if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_FINISH)) {
                    // 识别结束
                    Log.i("ChatActivity", " - 识别结束");
                    asr.unregisterListener(this);
                    asr = null;
                    ivSpeaking.setVisibility(View.GONE);
                    ivSpeak.setVisibility(View.VISIBLE);
                    initWp();

                }
                if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL)) {   //dao.add();
                    if (params != null && params.contains("\"final_result\"")) {
                        JSONObject jsonObject = null;
                        String finalRes = null;
                        try {
                            jsonObject = new JSONObject(params);
                            finalRes = jsonObject.getString("best_result");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        LogUtils.e("ChatActivity", params);
                        //speakTTS("识别成功，你说的是" + finalRes);
                        if (finalRes.startsWith("我想听") && finalRes.length() > 3
                                && !TextUtils.isEmpty(finalRes.substring(3))
                                && !finalRes.substring(3).equals(",")
                                && !finalRes.substring(3).equals("，")
                        ) {
                            robotBeans.add(new ViRobotBean("1", finalRes));
                            adapter.setNewData(robotBeans);
                            recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                            try {
                                getSongInfo(finalRes.substring(3));
                                //speakTTS("正在为您搜索");
                            } catch (Exception e) {
                                speakTTS("抱歉");
                            }
                        } else {
                            robotBeans.add(new ViRobotBean("1", finalRes));
                            adapter.setNewData(robotBeans);
                            recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                            getRobotText(finalRes);

                        }
                    }
                }
            }
        };
        asr.registerListener(asrListener);
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        params.put(SpeechConstant.PID, 1537);
        String json = null; // 可以替换成自己的json
        json = new JSONObject(params).toString(); // 这里可以替换成你需要测试的json
        asr.send(SpeechConstant.ASR_START, json, null, 0, 0);
    }

    //搜索歌曲
    public void getSongInfo(String singName) {
        ViRequestUtils.searchSong(this, singName, new Observer<ViSearchSong>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ViSearchSong searchSong) {
                Log.e("http", searchSong.toString());
                if (searchSong.getData() != null && searchSong.getData().getSong() != null && searchSong.getData().getSong().getList() != null && searchSong.getData().getSong().getList().size() != 0) {
                    getPlayUrl(searchSong);
                } else {
                    robotBeans.add(new ViRobotBean("2", "没有搜索到相应歌曲，无法为您播放。"));
                    adapter.setNewData(robotBeans);
                    recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    // 播放连接
    public void getPlayUrl(ViSearchSong searchSong) {
        ViSearchSong.DataBean.SongBean.ListBean listBean = searchSong.getData().getSong().getList().get(0);

        ViRequestUtils.getSongPlayUrl(this, ViAPI.SONG_URL_DATA_LEFT + listBean.getSongmid() + ViAPI.SONG_URL_DATA_RIGHT, new Observer<ViSongUrl>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ViSongUrl songUrl) {
                ViSongUrl.Req0Bean.DataBean data = songUrl.getReq_0().getData();
                List<String> sip = data.getSip();
                String PlayUrl = sip.get(0) + data.getMidurlinfo().get(0).getPurl();
                if (!TextUtils.isEmpty(data.getMidurlinfo().get(0).getPurl())) {
                    Intent intent = new Intent(ChatActivity.this, PlayActivity.class);
                    intent.putExtra("url", PlayUrl);
                    intent.putExtra("searchSong", searchSong);
                    startActivity(intent);
                } else {
                    robotBeans.add(new ViRobotBean("2", "VIP歌曲，暂时无法为您播放。"));
                    adapter.setNewData(robotBeans);
                    recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                    speakTTS("VIP歌曲，暂时无法为您播放。");
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    public void speakTTS(String s) {
        String AppId = "18760367";
        String AppKey = "YKrSxq9qA0H00kGsYXYBwqsS";
        String AppSecret = "irYofDXXVRnPczd7yeB01Ofd4WcnU1TB";

        mSpeechSynthesizer = SpeechSynthesizer.getInstance();
        mSpeechSynthesizer.setContext(this);

        mSpeechSynthesizer.setAppId(AppId);
        mSpeechSynthesizer.setApiKey(AppKey, AppSecret);
        mSpeechSynthesizer.setSpeechSynthesizerListener(new SpeechSynthesizerListener() {//SpeechSynthesizerListener接口
            @Override
            public void onSynthesizeStart(String s) {
                Log.i("ChatActivity", "hy语音合成开始 ---- " + s);
            }

            @Override
            public void onSynthesizeDataArrived(String s, byte[] bytes, int i, int i1) {

            }

            @Override
            public void onSynthesizeFinish(String s) {
                Log.i("ChatActivity", "语音合成完成 ---- " + s);
            }

            @Override
            public void onSpeechStart(String s) {
                Log.i("ChatActivity", "hy开始播放 ---- " + s);

            }

            @Override
            public void onSpeechProgressChanged(String s, int i) {

            }

            @Override
            public void onSpeechFinish(String s) {
                Log.i("ChatActivity", "hy播放完成 ---- " + s);
                mSpeechSynthesizer.release();//这个地方释放

            }

            @Override
            public void onError(String s, SpeechError speechError) {
                Log.e("ChatActivity", "语音合成出错---- " + s);
            }
        });
        mSpeechSynthesizer.auth(TtsMode.ONLINE); // 离在线混合
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "0"); // 设置发声的人声音，在线生效
        mSpeechSynthesizer.initTts(TtsMode.ONLINE); // 初始化离在线混合模式，如果只需要在线合成功能，使用 TtsMode.ONLINE
        mSpeechSynthesizer.speak(s);

    }

    // 图灵数据
    public void getRobotText(String text) {
        TuLingRequest tuLingRequestBean = new TuLingRequest();
        tuLingRequestBean.setReqType(0);
        TuLingRequest.PerceptionBean perception = new TuLingRequest.PerceptionBean();
        TuLingRequest.PerceptionBean.InputTextBean inputText = new TuLingRequest.PerceptionBean.InputTextBean();
        inputText.setText(text);
        TuLingRequest.PerceptionBean.SelfInfoBean selfInfo = new TuLingRequest.PerceptionBean.SelfInfoBean();
        TuLingRequest.PerceptionBean.SelfInfoBean.LocationBean location = new TuLingRequest.PerceptionBean.SelfInfoBean.LocationBean();
        location.setCity("杭州");
        selfInfo.setLocation(location);
        perception.setSelfInfo(selfInfo);
        perception.setInputText(inputText);
        TuLingRequest.UserInfoBean userInfo = new TuLingRequest.UserInfoBean();
        userInfo.setApiKey("e875d14c4dfd4725902c9e5086c50034");
        userInfo.setUserId("1");
        tuLingRequestBean.setUserInfo(userInfo);
        tuLingRequestBean.setPerception(perception);
        Gson gson = new Gson();
        String s = gson.toJson(tuLingRequestBean);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), s);
        ViRequestUtils.getTuLing(this, body, new Observer<TuLingResult>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(TuLingResult tuLingResultBean) {
                String text1 = tuLingResultBean.getResults().get(0).getValues().getText();
                ViRobotBean robotBean = new ViRobotBean("2", text1);
                robotBeans.add(robotBean);
                adapter.setNewData(robotBeans);
                recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                speakTTS(text1);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }


}