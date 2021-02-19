package com.punuo.sys.app.agedcare.ui;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.punuo.sys.app.agedcare.R;
import com.punuo.sys.app.agedcare.R2;
import com.punuo.sys.app.agedcare.adapter.AppGridViewAdapter;
import com.punuo.sys.app.agedcare.adapter.ApplicationAdapter;
import com.punuo.sys.app.agedcare.model.MyApplicationInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.DEFAULT_KEYS_SEARCH_LOCAL;


public class MenuFragment extends Fragment {
    private static final String TAG = "MenuFragment";
    @BindView(R2.id.viewpager)
    ViewPager viewpager;

    public final int CHAT = 0;
    public final int COMMUNITY = 1;
    public final int PHONECALL = 2;
    public final int HOUSEKEEPING = 3;
    public final int EAT = 4;
    public final int SHOP = 5;
    public final int MUSIC = 5;
    public final int OTHER = 6;

    private BroadcastReceiver AppReceiver = new ApplicationsIntentReceiver();

    private List<MyApplicationInfo> mApplications;
    // 程序所占的总屏数
    private int screenCount;
    //每个屏幕最大程序数量
    public static final int NUMBER_PER_SCREEN = 16;


    private ApplicationAdapter applicationAdapter;

    private AppGridViewAdapter gridViewAdapter;

    private List<GridView> gridViewList;
    LayoutInflater inflater;
    int[] icon = new int[]{
            R.drawable.qinliao1,
            R.drawable.shequ1,
            R.drawable.album,
            R.drawable.xiagnce,
            R.drawable.fuwu,
//            R.drawable.shangcheng1,
            R.drawable.yingyue1,
            R.drawable.shezhi
    };

    List<String> titlelist = new ArrayList<>();

    String[] title = new String[]{
            "亲聊",
            "社区",
            "好友电话",
            "相册",
            "点餐",
//            "商城",
            "报警",
            "其他",
    };

    //手机内存卡路径
    String SdCard;
    //当前版本
//    String version;
//    //FTP上的版本
//    String FtpVersion;
//    //用于版本xml解析
//    HashMap<String, String> versionHashMap = new HashMap<>();
//    //进度条
//    CustomProgressDialog loading;
//    //进度条消失类型
//    String result;
//    //下载进度条
//    ProgressDialog downloadDialog;
//    //apk路径
    String apkPath;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.activity_menufragment, container, false);
        view.setClickable(true);
        ButterKnife.bind(this, view);

        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(getActivity())) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getActivity().getPackageName()));
                startActivityForResult(intent, 10);
            }
        }
        init(inflater);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (!Settings.canDrawOverlays(getActivity())) {
                    // SYSTEM_ALERT_WINDOW permission not granted...
                    Toast.makeText(getActivity(), "not granted", Toast.LENGTH_SHORT);
                }
            }
        }
    }

    private void init(LayoutInflater layoutInflater) {
        //启动监听服务
//        this.getActivity().startService(new Intent(getActivity(), NewsService.class));
        SdCard = Environment.getExternalStorageDirectory().getAbsolutePath();
        apkPath = SdCard + "/PNS9/download/apk/";
        for (String aTitle : title) {
            titlelist.add(aTitle);
        }
        inflater = layoutInflater;
        this.getActivity().setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);
        registerIntentReceivers();
        bindApplications();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        unregisterIntentReceivers();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void loadApplications(boolean isLaunching) {
        if (isLaunching && mApplications != null) {
            return;
        }

        //获取所有app的入口
        PackageManager manager = this.getActivity().getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        final List<ResolveInfo> apps = manager.queryIntentActivities(mainIntent, 0);
        Collections.sort(apps, new ResolveInfo.DisplayNameComparator(manager));
        if (mApplications == null) {
            mApplications = new ArrayList<>();
        }
        mApplications.clear();
        for (int i = 0; i < icon.length; i++) {
            MyApplicationInfo application = new MyApplicationInfo();
            application.setType(MyApplicationInfo.TYPE_BUTTON);
            application.setTitle(title[i]);
            application.setIcon(getActivity().getDrawable(icon[i]));
            application.setSystemApp(false);
            mApplications.add(application);
        }

    }

    /**
     * Creates a new appplications adapter for the grid view and registers it.
     */
    private void bindApplications() {
        loadApplications(true);
        screenCount = mApplications.size() % NUMBER_PER_SCREEN == 0 ?
                mApplications.size() / NUMBER_PER_SCREEN :
                mApplications.size() / NUMBER_PER_SCREEN + 1;
        viewpager.removeAllViews();
        gridViewList = new ArrayList<>();
        for (int i = 0; i < screenCount; i++) {
            GridView gv = new GridView(getActivity());
            gridViewAdapter = new AppGridViewAdapter(getActivity(), mApplications, i);
            gv.setAdapter(gridViewAdapter);
            gv.setGravity(Gravity.CENTER);
            gv.setNumColumns(4);
            gv.setClickable(true);
            gv.setFocusable(true);
//
            gv.setVerticalSpacing(48);
            gv.setHorizontalSpacing(15);

            final int finalI = i;
            gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View vie, int position, long id) {
                    final MyApplicationInfo currentAppInfo = mApplications.get(position + finalI * NUMBER_PER_SCREEN);
                    if (currentAppInfo.getType() == MyApplicationInfo.TYPE_BUTTON) {
                        //按钮功能
                        switch (titlelist.indexOf(currentAppInfo.getTitle().toString())) {
                            case EAT:
                                startActivity(new Intent(getActivity(), ServiceCallActivity.class));
                                break;
                            case CHAT:
                                startActivity(new Intent(getActivity(), FamilyCircle.class));
                                break;
                            case COMMUNITY:
                                startActivity(new Intent(getActivity(), CommunityActivity.class));
                                break;
                            case HOUSEKEEPING:
                                startActivity(new Intent(getActivity(), AlbumActivity.class));
                                break;
                            case PHONECALL:
                                startActivity(new Intent(getActivity(), FriendCallActivity.class));
                                break;
                            case MUSIC:
                                startActivity(new Intent(getActivity(), EntertainmentActivity.class));
                                break;
                            case OTHER:
                                startActivity(new Intent(getActivity(), CodeActivity.class));
                                break;
                        }
                    } else {
                        //第三方app或者系统app
                        startActivity(currentAppInfo.getIntent());
                    }
                }
            });
            gv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    final MyApplicationInfo currentAppInfo = mApplications.get(position + finalI * NUMBER_PER_SCREEN);
                    System.out.println(currentAppInfo.getTitle() + "" + currentAppInfo.isSystemApp());
                    if (!currentAppInfo.isSystemApp()) {//是否为系统app
                        if (currentAppInfo.getType() == MyApplicationInfo.TYPE_APP) {//是否为第三方app
                            Uri currentAppUri = Uri.parse("package:" + currentAppInfo.packageName);
                            Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, currentAppUri);
                            startActivity(uninstallIntent);
                        }
                    } else {
                        //显示应用信息
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", currentAppInfo.packageName, null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                    return true;
                }
            });
            gridViewList.add(gv);
        }
        applicationAdapter = new ApplicationAdapter(gridViewList);
        viewpager.setAdapter(applicationAdapter);
    }

    //注册app监听
    private void registerIntentReceivers() {
        IntentFilter filter;
        filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        filter.addDataScheme("package");
        this.getActivity().registerReceiver(AppReceiver, filter);
    }


    private void unregisterIntentReceivers() {
        this.getActivity().unregisterReceiver(AppReceiver);
    }

    private class ApplicationsIntentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadApplications(false);
            bindApplications();
        }
    }
}





