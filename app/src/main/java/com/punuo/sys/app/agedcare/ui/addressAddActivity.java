package com.punuo.sys.app.agedcare.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.punuo.sys.app.agedcare.R;
import com.punuo.sys.app.agedcare.adapter.MyRecyclerViewAdapter;
import com.punuo.sys.app.agedcare.db.MyDatabaseHelper;
import com.punuo.sys.app.agedcare.http.GetPostUtil;
import com.punuo.sys.app.agedcare.model.Constant;
import com.punuo.sys.app.agedcare.model.FriendCallAvator;
import com.punuo.sys.app.agedcare.model.ShortMovie;
import com.punuo.sys.app.agedcare.sip.SipInfo;
import com.punuo.sys.app.agedcare.tools.FileUtil;
import com.punuo.sys.app.agedcare.tools.JsonParser;
import com.punuo.sys.app.agedcare.view.CircleImageView;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import android.os.Handler;
import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.punuo.sys.app.agedcare.model.Constant.FORMT;
import static com.punuo.sys.app.agedcare.sip.SipInfo.dbHelper;
import static com.punuo.sys.app.agedcare.sip.SipInfo.farmilymemberList;
import static com.punuo.sys.app.agedcare.sip.SipInfo.movies;
import static com.punuo.sys.app.agedcare.sip.SipInfo.userAccount;


public class addressAddActivity extends HindebarActivity implements View.OnClickListener {

    @Bind(R.id.edit_name)
    TextView edit_name;
    @Bind(R.id.edit_number)
    TextView edit_number;
    @Bind(R.id.add)
    Button add;
    @Bind(R.id.selectavator)
    CircleImageView selectavator;
    @Bind(R.id.takePhoto_avator)
    Button takePhoto_avator;
    String type1;
    String call1;
    private SpeechSynthesizer mTts;
//    private Context mContext = this;
//    private String voicer = "xiaoyan";
    // 缓冲进度
//    private int mPercentForBuffering = 0;
//    // 播放进度
//    private int mPercentForPlaying = 0;
//    // 云端/本地单选按钮
//    private RadioGroup mRadioGroup;
     //语音听写对象
    private SpeechRecognizer mIat;
    // 语音听写UI
    private String avatorurl=null;
    private RecognizerDialog mIatDialog;
    private HashMap<String, String> mIatResults = new LinkedHashMap<>();
    private Toast mToast;
    private SharedPreferences mSharedPreferences;
    private String savePath;
    private String mEngineType = SpeechConstant.TYPE_CLOUD;
    private PopupWindow popupWindow;
    private int from = 0;
    private Context mContext;
    private DisplayImageOptions options;
    private MyRecyclerViewAdapter adapter;
    private List<FriendCallAvator> avatorinfo=new ArrayList<>();
    private List<String> images=new ArrayList<>();
    private String TAG="addressAddActivity";
    String extra_name;
    String extra_phonenumber;
    String extra_avatorurl;
    String extra_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addressadd);
        SpeechUtility.createUtility(this, SpeechConstant.APPID +"=5b7bb8e0");
        ButterKnife.bind(this);
        mContext=this;
        sendRequestWithOkHttp();
        initview();

    }

    int ret = 0; // 函数调用返回值
    public void initview()
    {
        mIat = SpeechRecognizer.createRecognizer(addressAddActivity.this, mInitListener);
        mIatDialog = new RecognizerDialog(addressAddActivity.this, mInitListener);
        mSharedPreferences = getSharedPreferences("com.jredu.setting", Activity.MODE_PRIVATE);
//        mToast = Toast.makeText(this,"", Toast.LENGTH_SHORT);
        mTts = SpeechSynthesizer.createSynthesizer(addressAddActivity.this, mTtsInitListener);
//        mSharedPreferences = getSharedPreferences("com.jredu.setting", MODE_PRIVATE);
        mToast = Toast.makeText(this,"", Toast.LENGTH_SHORT);
        mEngineType = SpeechConstant.TYPE_CLOUD;
        dbHelper = new MyDatabaseHelper(this, "member.db", null, 2);
        add.setOnClickListener(this);
        edit_name.setOnClickListener(this);
        edit_number.setOnClickListener(this);
        selectavator.setOnClickListener(this);
        takePhoto_avator.setOnClickListener(this);
        takePhoto_avator.setVisibility(View.INVISIBLE);
//        pref = PreferenceManager.getDefaultSharedPreferences(this);
//        editor1 = getSharedPreferences("data", MODE_PRIVATE).edit();
        Intent intent=getIntent();
         extra_avatorurl=intent.getStringExtra("extra_avatorurl");
         extra_name=intent.getStringExtra("extra_name");
         extra_phonenumber=intent.getStringExtra("extra_phonenumber");
        extra_id=intent.getStringExtra("extra_id");


        edit_name.setText(extra_name);
        edit_number.setText(extra_phonenumber);

        try {
            if(!extra_avatorurl.equals("nopic")) {
                ImageLoader.getInstance().displayImage(extra_avatorurl, selectavator);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(extra_name!=null||extra_phonenumber!=null)
        {
            add.setText("修改");
        }else {
            add.setText("添加");
        }
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        if (id==R.id.selectavator)
        {
            Log.d("address","run: ");
            from=Location.RIGHT.ordinal();
            initPopupWindow();
        }
//        else if (id==R.id.takePhoto_avator)
//        {
//            startActivityForResult(new  Intent(MediaStore.ACTION_IMAGE_CAPTURE),1);
//        }

        else if (id==R.id.add) {
            type1 = edit_name.getText().toString();
            call1 = edit_number.getText().toString();

            Log.d("address",avatorurl+"");
            if (type1.equals("") || type1 == null) {
                Toast.makeText(this, "联系人为空", Toast.LENGTH_SHORT).show();
            } else if (call1.equals("") || call1 == null) {
                Toast.makeText(this, "电话号码为空", Toast.LENGTH_SHORT).show();
            } else {
                if (extra_name==null||extra_phonenumber==null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            GetPostUtil.sendGet1111(Constant.URL_insertAddressbook, "userid=" + userAccount + "&linkman=" +
                                    type1+ "&pic=" + avatorurl+ "&telnum=" + call1);
                            EventBus.getDefault().post(new MessageEvent("addcompelete"));
                        }
                    }).start();

                    Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
                    finish();
                }else {
                    add.setText("修改");
                    Log.d("addressedit","run:2 ");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                    if (avatorurl!=null) {
                        GetPostUtil.sendGet1111(Constant.URL_updateAddressbook, "id=" + extra_id + "&linkman=" +
                                type1+ "&pic=" + avatorurl+ "&telnum=" + call1);
                        EventBus.getDefault().post(new MessageEvent("addcompelete"));
                    }else {
                        GetPostUtil.sendGet1111(Constant.URL_updateAddressbook, "id=" + extra_id + "&linkman=" +
                                type1+ "&pic=" + extra_avatorurl+ "&telnum=" + call1);
                        EventBus.getDefault().post(new MessageEvent("addcompelete"));
                    }
                        }

                    }).start();
                    Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }else if(id==R.id.edit_name)
        {
            String text = "请输入姓名";
            setParam();
            int code = mTts.startSpeaking(text, mTtsListener);
            if (code != ErrorCode.SUCCESS) {
                if (code == ErrorCode.ERROR_COMPONENT_NOT_INSTALLED) {
                    //未安装则跳转到提示安装页面
                    showTip("未安装");
                } else {
                    showTip("语音合成失败,错误码: " + code);
                }
            }
            Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                /**
                 *要执行的操作
                 */
                edit_name.setText(null);// 清空显示内容
                mIatResults.clear();

                boolean isShowDialog = mSharedPreferences.getBoolean(
                        "", true);
                if (isShowDialog) {
                    // 显示听写对话框
                    mIatDialog.setListener(mRecognizerDialogListener);
                    mIatDialog.show();
//                    showTip("倾听中");
                } else {
                    // 不显示听写对话框
                    ret = mIat.startListening(mRecognizerListener);
                    if (ret != ErrorCode.SUCCESS) {
                        showTip("听写失败,错误码：" + ret);
                    } else {
                        showTip("");
                    }
                }
            }
        }, 1500);
        }else if (id==R.id.edit_number)
        {
            String number = "请输入号码";
                setParam();

                int code1 = mTts.startSpeaking(number, mTtsListener);
                if (code1 != ErrorCode.SUCCESS) {
                    if (code1 == ErrorCode.ERROR_COMPONENT_NOT_INSTALLED) {
                        //未安装则跳转到提示安装页面
                        showTip("未安装");
                    } else {
                        showTip("语音合成失败,错误码: " + code1);
                    }
                }
                    Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                /**
                 *要执行的操作
                 */
                edit_number.setText(null);
                initSpeech(addressAddActivity.this);
            }
        }, 1500);

        }
    }

    private InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            //  Log.d(TAG, "InitListener init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                showTip("初始化失败,错误码：" + code);
            }
        }
    };
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            // Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                showTip("初始化失败，错误码：" + code);
            }
        }
    };
    private SynthesizerListener mTtsListener = new SynthesizerListener() {

        @Override
        public void onSpeakBegin() {
//            showTip("开始播放");
        }
        @Override
        public void onSpeakPaused() {
//            showTip("暂停播放");
        }

        @Override
        public void onSpeakResumed() {
//            showTip("继续播放");
        }
        @Override
        public void onBufferProgress(int percent, int i1, int i2, String s) {
//            mPercentForBuffering = percent;
//           showTip(String.format("", mPercentForBuffering, mPercentForPlaying));
        }



        @Override
        public void onSpeakProgress(int percent, int i1, int i2) {
//            mPercentForPlaying = percent;
//            showTip(String.format("朗读中", mPercentForBuffering, mPercentForPlaying));
        }

        @Override
        public void onCompleted(SpeechError error) {
             if (error != null) {
                showTip(error.getPlainDescription(true));
            }
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }


    };
    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            showTip("开始说话");
        }
        @Override
        public void onError(SpeechError error) {
            // Tips：
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
            // 如果使用本地功能（语记）需要提示用户开启语记的录音权限。
            showTip(error.getPlainDescription(true));
        }
        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            showTip("结束说话");

        }
        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            printResult(results);

//            if (isLast) {
//                // TODO 最后的结果
//            }
        }
        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            showTip("当前正在说话，音量大小：" + volume);
            // Log.d(TAG, "返回音频数据："+data.length);
        }
        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //    if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //        String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //        Log.d(TAG, "session id =" + sid);
            //    }
        }
    };
    private void printResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());

        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        StringBuilder resultBuffer = new StringBuilder();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }

        edit_name.setText(resultBuffer.toString());
//        edit_name.setSelection(edit_name.length());
    }
    private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
        public void onResult(RecognizerResult results, boolean isLast) {
            printResult(results);
        }

        /**
         * 识别回调错误.
         */
        public void onError(SpeechError error) {
            showTip(error.getPlainDescription(true));
        }

    };

    private void showTip(String s) {
        mToast.setText(s);
        mToast.show();
    }

    private void setParam() {
        mIat.setParameter(SpeechConstant.PARAMS, null);

        // 设置听写引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

        String lag = mSharedPreferences.getString("iat_language_preference",
                "mandarin");
        if (lag.equals("en_us")) {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
        } else {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            // 设置语言区域
            mIat.setParameter(SpeechConstant.ACCENT, lag);
        }

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, mSharedPreferences.getString("iat_vadbos_preference", "3000"));

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, mSharedPreferences.getString("iat_vadeos_preference", "1000"));

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, mSharedPreferences.getString("iat_punc_preference", "0"));

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mIat.setParameter(SpeechConstant.AUDIO_FORMAT,"wav");
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/iat.wav");

        // 设置听写结果是否结果动态修正，为“1”则在听写过程中动态递增地返回结果，否则只在听写结束之后返回最终结果
        // 注：该参数暂时只对在线听写有效
        mIat.setParameter(SpeechConstant.ASR_DWA, mSharedPreferences.getString("iat_dwa_preference", "0"));
    }
    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "'IMG'_yyyyMMdd_HH-mm-ss");
        return dateFormat.format(date) + ".jpg";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 如果返回值是正常的话
        if (resultCode == Activity.RESULT_OK) {
            // 验证请求码是否一至，也就是startActivityForResult的第二个参数
            switch (requestCode) {
                case 1:
                   String  name=getPhotoFileName();
                    Bitmap bm = (Bitmap) data.getExtras().get("data");
//                   savePath = FileUtil.saveBitmap(name,bm);
                    selectavator.setImageBitmap(bm);
                    break;

                default:
                    break;
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 退出时释放连接
//        farmilymemberList.clear();
        dbHelper.close();

        mIat.cancel();
        mIat.destroy();
    }
    @Override
    protected void onResume() {
        // 开放统计 移动数据统计分析
        //FlowerCollector.onResume(IatDemo.this);
        //FlowerCollector.onPageStart(TAG);
        super.onResume();
    }
    @Override
    protected void onPause() {
        // 开放统计 移动数据统计分析
        /// FlowerCollector.onPageEnd(TAG);
        //FlowerCollector.onPause(IatDemo.this);
        super.onPause();
    }

    /**
     * 初始化语音识别
     */
    public void initSpeech(final Context context) {
        //1.创建RecognizerDialog对象
        RecognizerDialog mDialog = new RecognizerDialog(context, null);
        //2.设置accent、language等参数
       mDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mDialog.setParameter(SpeechConstant.ACCENT, "mandarin");
        mDialog.setParameter(SpeechConstant.VAD_EOS, mSharedPreferences.getString("iat_vadeos_preference", "1000"));
        //3.设置回调接口
        mDialog.setListener(new RecognizerDialogListener() {
            @Override
            public void onResult(RecognizerResult recognizerResult, boolean isLast) {
                if (!isLast) {
                    //解析语音
                    String result = parseVoice(recognizerResult.getResultString());
                    edit_number.setText(result);
                }
            }

            @Override
            public void onError(SpeechError speechError) {

            }
        });
        //4.显示dialog，接收语音输入
        mDialog.show();
    }

    /**
     * 解析语音json
     */
    public String parseVoice(String resultString) {
        Gson gson = new Gson();
        Voice voiceBean = gson.fromJson(resultString, Voice.class);

        StringBuilder sb = new StringBuilder();
        ArrayList<Voice.WSBean> ws = voiceBean.ws;
        for (Voice.WSBean wsBean : ws) {
            String word = wsBean.cw.get(0).w;
            sb.append(word);
        }
        return sb.toString();
    }

    /**
     * 语音对象封装
     */
    public class Voice {

         ArrayList<WSBean> ws;

         class WSBean {
            ArrayList<CWBean> cw;
        }

         class CWBean {
            String w;
        }
    }
    class popupDismissListener implements PopupWindow.OnDismissListener{

        @Override
        public void onDismiss() {
            backgroundAlpha(1f);
        }

    }
    protected void initPopupWindow(){
        final View popupWindowView = getLayoutInflater().inflate(R.layout.avatarchoose, null);
        //内容，高度，宽度
        if(Location.BOTTOM.ordinal() == from){
            popupWindow = new PopupWindow(popupWindowView, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT, true);
        }else{
            popupWindow = new PopupWindow(popupWindowView, ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.FILL_PARENT, true);
        }
        //动画效果
        if(Location.LEFT.ordinal() == from){
            popupWindow.setAnimationStyle(R.style.AnimationLeftFade);
        }else if(Location.RIGHT.ordinal() == from){
            popupWindow.setAnimationStyle(R.style.AnimationRightFade);
        }else if(Location.BOTTOM.ordinal() == from){
            popupWindow.setAnimationStyle(R.style.AnimationBottomFade);
        }
        //菜单背景色
        ColorDrawable dw = new ColorDrawable(0xffffffff);
        popupWindow.setBackgroundDrawable(dw);
        //宽度
        //popupWindow.setWidth(LayoutParams.WRAP_CONTENT);
        //高度
        //popupWindow.setHeight(LayoutParams.FILL_PARENT);
        //显示位置

        if(Location.LEFT.ordinal() == from){
            popupWindow.showAtLocation(getLayoutInflater().inflate(R.layout.activity_addressadd, null), Gravity.START, 0, 500);
        }else if(Location.RIGHT.ordinal() == from){
            popupWindow.showAtLocation(getLayoutInflater().inflate(R.layout.activity_addressadd, null), Gravity.END, 0, 500);
        }else if(Location.BOTTOM.ordinal() == from){
            popupWindow.showAtLocation(getLayoutInflater().inflate(R.layout.activity_addressadd, null), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
        }
        //设置背景半透明
        backgroundAlpha(0.5f);
        //关闭事件
        popupWindow.setOnDismissListener(new popupDismissListener());

        popupWindowView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
    /*if( popupWindow!=null && popupWindow.isShowing()){
     popupWindow.dismiss();
     popupWindow=null;
    }*/
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
                return false;
            }
        });

        RecyclerView recyclerView = (RecyclerView) popupWindowView.findViewById(R.id.rvavator);
        GridLayoutManager glm=new GridLayoutManager(mContext,3);//定义3列的网格布局
        recyclerView.setLayoutManager(glm);
        recyclerView.addItemDecoration(new addressAddActivity.RecyclerViewItemDecoration(10,3));//初始化子项距离和列数
        options=new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.pictureloading)
                .showImageOnLoading(R.drawable.pictureloading)
                .showImageOnFail(R.drawable.pictureloading)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .displayer(new FadeInBitmapDisplayer(1))
                .build();
        adapter=new MyRecyclerViewAdapter(images,mContext,options,glm);
        recyclerView.setAdapter(adapter);
        initData();
        adapter.setmOnItemClickListener(new MyRecyclerViewAdapter.OnItemClickListener() {

            @Override
            public void onClick(View view, int position) {
                 avatorurl=images.get(position);
                 Log.d("address111",""+avatorurl);
                 ImageLoader.getInstance().displayImage(avatorurl, selectavator);
                popupWindow.dismiss();
            }
        });
    }

    /**
     * 设置添加屏幕的背景透明度
     */
    public void backgroundAlpha(float bgAlpha)
    {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
    }
    /**
     * 菜单弹出方向
     *
     */
    public enum Location {
        LEFT,
        RIGHT,
        TOP,
        BOTTOM
    }
    public class RecyclerViewItemDecoration extends RecyclerView.ItemDecoration
    {
        private int itemSpace;//定义子项间距
        private int itemColumnNum;//定义子项的列数

         RecyclerViewItemDecoration(int itemSpace, int itemColumnNum) {
            this.itemSpace = itemSpace;
            this.itemColumnNum = itemColumnNum;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.bottom=itemSpace;//底部留出间距
            if(parent.getChildAdapterPosition(view)%itemColumnNum==0)//每行第一项左边不留间距，其他留出间距
            {
                outRect.left=0;
            }
            else
            {
                outRect.left=itemSpace;
            }

        }
    }
    private void initData()
    {
        images.clear();
        try {
            for (int i=0;i<avatorinfo.size();i++) {
                Log.e(TAG,"http://"+ SipInfo.serverIp+":8000/static/addressbook/"+avatorinfo.get(i).getPic());
                images.add("http://"+ SipInfo.serverIp+":8000/static/addressbook/"+avatorinfo.get(i).getPic());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        adapter.notifyDataSetChanged();
    }
    private void sendRequestWithOkHttp() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d(TAG, "run: ");
                    OkHttpClient client = new OkHttpClient();
                    Request request1 = new Request.Builder()
                            .url(FORMT+"users/getAddressbookpic")
                            .build();
                    Log.d(TAG, "run:1 " + client.newCall(request1).execute().body().string());

                    Response response = client.newCall(request1).execute();
                    String responseData = response.body().string();

                    parseJSONWithGSON(responseData);
                    Log.d(TAG, "run:3 " + responseData);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private void parseJSONWithGSON(String responseData) {
//        Log.d("1111", "run:3 ");
        String jsonData = "[" + responseData.split("\\[")[1].split("\\]")[0] + "]";
        Log.e(TAG, "run:2" + jsonData);
        Gson gson = new Gson();

        try {
            avatorinfo = gson.fromJson(jsonData, new TypeToken<List<FriendCallAvator>>(){}.getType());
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }



    }
}

