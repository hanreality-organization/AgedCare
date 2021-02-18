package com.punuo.sys.app.agedcare.vi.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.util.Log;

import androidx.annotation.Nullable;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;
import com.punuo.sys.app.agedcare.http.LogUtils;
import com.punuo.sys.sdk.util.ToastUtils;
import com.punuo.sys.app.agedcare.vi.activity.ChatActivity;
import com.punuo.sys.app.agedcare.vi.bean.VoiceEvent;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class SpeechService extends Service implements RecognitionListener {
    EventManager asr;
    EventManager wp;
    private SpeechSynthesizer mSpeechSynthesizer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initWp();
        return super.onStartCommand(intent, flags, startId);
    }

    private void initWp() {
        wp = EventManagerFactory.create(this, "wp");
        EventListener wpListener = new EventListener() {
            @Override
            public void onEvent(String name, String params, byte[] data, int
                    offset, int length) {
                Log.d("SpeechService", String.format("event: name=%s, params=%s", name, params));
                // 唤醒事件
                if (name.equals("wp.data")) {
                    try {
                        JSONObject json = new JSONObject(params);
                        int errorCode = json.getInt("errorCode");
                        if (errorCode == 0) {
                            // 唤醒成功
                            Log.i("SpeechService", " - 唤醒成功");
                            wp.send(SpeechConstant.WAKEUP_STOP, null, null, 0, 0);
                            wp = null;
                            VoiceEvent event = new VoiceEvent();
                            event.setCode(1001);
                            EventBus.getDefault().post(event);
                            speakTTS("请问有什么可以帮到您？");
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            initAsr();
                        } else {
                            // 唤醒失败
                            Log.i("SpeechService", " - 唤醒失败");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if ("wp.exit".equals(name)) {
                    // 唤醒已停止
                    Log.i("SpeechService", " - 唤醒停止");
                }
            }
        };
        wp.registerListener(wpListener);
        HashMap map = new HashMap();
        map.put(SpeechConstant.WP_WORDS_FILE, "assets://WakeUp.bin");
        String json = null; // 这里可以替换成你需要测试的json
        json = new JSONObject(map).toString();
        wp.send(SpeechConstant.WAKEUP_START, json, null, 0, 0);
    }

    private void initAsr() {
        asr = EventManagerFactory.create(this, "asr");
        EventListener asrListener = new EventListener() {
            @Override
            public void onEvent(String name, String params, byte[] data, int offset, int length) {
                Log.d("SpeechService", String.format("event: name=%s, params=%s", name, params));
                if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_READY)) {
                    // 引擎就绪，可以说话，一般在收到此事件后用户可以说话
                    Log.i("SpeechService", " - 引擎就绪");

                }
                if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_FINISH)) {
                    // 识别结束
                    Log.i("SpeechService", " - 识别结束");
                    asr.unregisterListener(this);
                    asr = null;
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
                        LogUtils.e("SpeechService", params);
                        //speakTTS("识别成功，你说的是" + finalRes);
                        VoiceEvent event = new VoiceEvent();
                        event.setCode(1003);
                        event.setContent(finalRes);
                        EventBus.getDefault().post(event);
                        if (finalRes.startsWith("我想听")) {
                            try {
                                VoiceEvent messageEvent = new VoiceEvent();
                                messageEvent.setContent(finalRes.substring(3));
                                messageEvent.setCode(1002);
                                EventBus.getDefault().post(messageEvent);
                                speakTTS("正在为您搜索");
                            } catch (Exception e) {
                                speakTTS("抱歉");
                            }
                        } else if (finalRes.contains("退出播放")) {
                            try {
                                VoiceEvent messageEvent = new VoiceEvent();
                                messageEvent.setContent(finalRes.substring(3));
                                messageEvent.setCode(1004);
                                EventBus.getDefault().post(messageEvent);
                                speakTTS("好的");
                            } catch (Exception e) {
                                speakTTS("抱歉");
                            }
                        } else if (finalRes.contains("暂停播放")) {
                            try {
                                VoiceEvent messageEvent = new VoiceEvent();
                                messageEvent.setContent(finalRes.substring(3));
                                messageEvent.setCode(1005);
                                EventBus.getDefault().post(messageEvent);
                                speakTTS("好的");
                            } catch (Exception e) {
                                speakTTS("抱歉");
                            }
                        } else if (finalRes.contains("继续播放")) {
                            try {
                                VoiceEvent messageEvent = new VoiceEvent();
                                messageEvent.setContent(finalRes.substring(3));
                                messageEvent.setCode(1006);
                                EventBus.getDefault().post(messageEvent);
                                speakTTS("好的");
                            } catch (Exception e) {
                                speakTTS("抱歉");
                            }
                        }
                        //其他页面交互
                        else if (finalRes.contains("聊天")) {
                            try {
                                Intent intent = new Intent(getBaseContext(), ChatActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                getApplication().startActivity(intent);
                                speakTTS("好的");
                            } catch (Exception e) {
                                speakTTS("抱歉");
                            }
                        } else {
                            try {
                                speakTTS("抱歉，我不太明白");
                            } catch (Exception e) {
                                e.printStackTrace();
                                speakTTS("抱歉，出错了");
                            }
                            VoiceEvent messageEvent = new VoiceEvent();
                            messageEvent.setCode(2001);
                            EventBus.getDefault().post(messageEvent);
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

    // 播音函数
    public void speakTTS(String s) {
        String AppId = "18760367";
        String AppKey = "mbpYmuKU2bFEorfCCDagOK5G";
        String AppSecret = "irYofDXXVRnPczd7yeB01Ofd4WcnU1TB";

        mSpeechSynthesizer = SpeechSynthesizer.getInstance();
        mSpeechSynthesizer.setContext(this);
        mSpeechSynthesizer.setAppId(AppId);
        mSpeechSynthesizer.setApiKey(AppKey, AppSecret);
        mSpeechSynthesizer.setSpeechSynthesizerListener(new SpeechSynthesizerListener() {
            // SpeechSynthesizerListener
            @Override
            public void onSynthesizeStart(String s) {
                Log.i("SpeechService", " - 合成开始" + s);
            }

            @Override
            public void onSynthesizeDataArrived(String s, byte[] bytes, int i, int i1) {
                Log.i("SpeechService", " - 语音内容" + s);
            }

            @Override
            public void onSynthesizeFinish(String s) {
                Log.i("SpeechService", " - 合成完成" + s);
            }

            @Override
            public void onSpeechStart(String s) {
                Log.i("SpeechService", " - 开始播放" + s);

            }

            @Override
            public void onSpeechProgressChanged(String s, int i) {

            }

            @Override
            public void onSpeechFinish(String s) {
                Log.i("SpeechService", " - 播放完成" + s);
                mSpeechSynthesizer.release();//这个地方释放

            }

            @Override
            public void onError(String s, SpeechError speechError) {
                Log.e("SpeechService", " - 合成出错" + s);
            }
        });
        // 语音合成模式：在线
        mSpeechSynthesizer.auth(TtsMode.ONLINE);
        // 设置发声的人声音，在线生效
        mSpeechSynthesizer.setParam(com.baidu.tts.client.SpeechSynthesizer.PARAM_SPEAKER, "0");
        // 初始化在线模式
        mSpeechSynthesizer.initTts(TtsMode.ONLINE);
        mSpeechSynthesizer.speak(s);
    }

    @Override
    public void onReadyForSpeech(Bundle params) {
        Log.e("SpeechService", "类名==ChatActivity" + "方法名==onReadyForSpeech=====:" + "");
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.e("SpeechService", "类名==ChatActivity" + "方法名==onBeginningOfSpeech=====:" + "");
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        Log.e("SpeechService", "类名==ChatActivity" + "方法名==onRmsChanged=====:" + rmsdB);
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.e("SpeechService", "类名==ChatActivity" + "方法名==onBufferReceived=====:" + "");
    }

    @Override
    public void onEndOfSpeech() {
        Log.e("SpeechService", "类名==ChatActivity" + "方法名==onEndOfSpeech=====:" + "");
    }

    // 错误提示
    @Override
    public void onError(int error) {
        StringBuilder sb = new StringBuilder();
        switch (error) {
            case SpeechRecognizer.ERROR_AUDIO:
                sb.append("存在音频问题");
                ToastUtils.showToast("存在音频问题");
                speakTTS("存在音频问题");
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                sb.append("没有语音输入");
                ToastUtils.showToast("没有语音输入");
                speakTTS("没有语音输入");
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                sb.append("其它客户端错误");
                ToastUtils.showToast("其它客户端错误");
                speakTTS("其它客户端错误");
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                sb.append("权限不足");
                ToastUtils.showToast("权限不足");
                speakTTS("权限不足");
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                sb.append("网络问题");
                ToastUtils.showToast("网络问题");
                speakTTS("网络问题");
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                sb.append("没有匹配的识别结果");
                ToastUtils.showToast("没有匹配的识别结果");
                speakTTS("没有匹配的识别结果");
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                sb.append("引擎忙");
                ToastUtils.showToast("引擎忙");
                speakTTS("引擎忙");
                break;
            case SpeechRecognizer.ERROR_SERVER:
                sb.append("服务端错误");
                ToastUtils.showToast("服务端错误");
                speakTTS("服务端错误");
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                sb.append("连接超时");
                ToastUtils.showToast("连接超时");
                speakTTS("连接超时");
                break;
        }
    }

    // 最终结果回调
    @Override
    public void onResults(Bundle results) {
        // 获取截取到的词的集合
        ArrayList<String> nbest = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (nbest != null) {
            Log.e("SpeechService", "类名==ChatActivity" + "方法名==onResults=====nbest:" + nbest);
        }

        float[] array = results.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES);
        if (array != null) {
            String str = "";
            for (float anArray : array) {
                str += anArray;
            }
            Log.e("SpeechService", "类名==ChatActivity" + "方法名==onResults=====array:" + str);
        } else {
            Log.e("SpeechService", "类名==ChatActivity" + "方法名==onResults=====array:" + "为空");
        }
        // 获取到JSON数据
        String json = results.getString("origin_result");
        Log.e("SpeechService", "类名==ChatActivity" + "方法名==onResults=====:" + json);
    }

    // 临时结果处理,这里可以截取到一些关键词 @param results 保存着一些说话的关键词
    @Override
    public void onPartialResults(Bundle partialResults) {
        // 获取截取到的词的集合
        ArrayList<String> nbest = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (nbest != null) {
            Log.e("SpeechService", "类名==ChatActivity" + "方法名==onPartialResults=====nbest:" + nbest);
        }

        // 获取到不知道干嘛的东西，好像是认证的分数，但是一直为空
        float[] array = partialResults.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES);
        if (array != null) {
            Log.e("SpeechService", "类名==ChatActivity" + "方法名==onPartialResults=====array:" + array.toString());
        } else {
            Log.e("SpeechService", "类名==ChatActivity" + "方法名==onPartialResults=====array:" + "为空");
        }

        // 获取到Json数据
        String json = partialResults.getString("origin_result");
        Log.e("SpeechService", "类名==ChatActivity" + "方法名==onPartialResults=====:" + json);

    }

    // 处理事件回调，保留数据后续使用 @param eventType 事件类型 params 这个可能和上面回调结果的一样，用同样的key去获取
    @Override
    public void onEvent(int eventType, Bundle params) {
        Log.e("SpeechService", "类名==ChatActivity" + "方法名==onEvent=====:" + eventType);
    }
}