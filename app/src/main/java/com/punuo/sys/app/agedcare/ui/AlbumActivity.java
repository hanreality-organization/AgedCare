package com.punuo.sys.app.agedcare.ui;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.punuo.sys.app.agedcare.R;
import com.punuo.sys.app.agedcare.tools.AlbumBitmapCacheHelper;
import com.punuo.sys.app.agedcare.adapter.MyRecyclerViewAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.punuo.sys.app.agedcare.video.VideoInfo.mCamera;


public class AlbumActivity extends HindebarActivity {

    private RecyclerView rv;
    private List<String> images = new ArrayList<String>();//图片地址
    private List<String> imagespath = new ArrayList<String>();
    private Context mContext;
    private DisplayImageOptions options;
    private MyRecyclerViewAdapter adapter;
    private HashMap<Integer, float[]> xyMap = new HashMap<Integer, float[]>();//所有子项的坐标
    private int screenWidth;//屏幕宽度
    private int screenHeight;//屏幕高度
    PopupMenu popup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albumactivity);
        mContext = this;
        initView();
//       initData();
        startGetImageThread();
        setEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
//        screenWidth = wm.getDefaultDisplay().getWidth();
//        screenHeight = wm.getDefaultDisplay().getHeight();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
    }

    /**
     * recyclerView item点击事件
     */
    private void setEvent() {
        adapter.setmOnLongItemClickListener(new MyRecyclerViewAdapter.OnLongItemClickListener() {
            @Override
            public void onLongClick(View view, final int position) {
                Log.d("onlongclick", "success");

                popup = new PopupMenu(AlbumActivity.this, view);
                // 将R.menu.popup_menu菜单资源加载到popup菜单中
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
                // 为popup菜单的菜单项单击事件绑定事件监听器
                popup.setOnMenuItemClickListener(
                        new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.deletepicture:
//                                                picklist.add(allImages.get(position).path);
                                        AlbumBitmapCacheHelper.getInstance().clearCache();
                                        File file = new File(images.get(position).substring(7));
                                        Log.d("ssssb", position + "");
                                        Log.d("ssssb", images.get(position).substring(7));
                                        if (file.exists()) {
                                            getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DATA + "=?", new String[]{images.get(position).substring(7)});//删除系统缩略图
                                            file.delete();
                                            images.remove(position);
                                            adapter.notifyDataSetChanged();
                                        } else {
                                            Log.d("ssssb", "文件不存在");
                                        }
                                        break;
                                }
                                return true;
                            }
                        });
                popup.show();


            }
        });
        adapter.setmOnItemClickListener(new MyRecyclerViewAdapter.OnItemClickListener() {

            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(mContext, AlbumSecondActivity.class);
                intent.putStringArrayListExtra("urls", (ArrayList<String>) images);
                intent.putExtra("position", position);
                xyMap.clear();//每一次点击前子项坐标都不一样，所以清空子项坐标

                //子项前置判断，是否在屏幕内，不在的话获取屏幕边缘坐标
                View view0 = rv.getChildAt(0);
                int position0 = rv.getChildAdapterPosition(view0);
                if (position0 > 0) {
                    for (int j = 0; j < position0; j++) {
                        float[] xyf = new float[]{(1 / 6.0f + (j % 3) * (1 / 3.0f)) * screenWidth, 0};//每行3张图，每张图的中心点横坐标自然是屏幕宽度的1/6,3/6,5/6
                        xyMap.put(j, xyf);
                    }
                }

                //其余子项判断
                for (int i = position0; i < rv.getAdapter().getItemCount(); i++) {
                    View view1 = rv.getChildAt(i - position0);
                    if (rv.getChildAdapterPosition(view1) == -1)//子项末尾不在屏幕部分同样赋值屏幕底部边缘
                    {
                        float[] xyf = new float[]{(1 / 6.0f + (i % 3) * (1 / 3.0f)) * screenWidth, screenHeight};
                        xyMap.put(i, xyf);
                    } else {
                        int[] xy = new int[2];
                        view1.getLocationOnScreen(xy);
                        float[] xyf = new float[]{xy[0] * 1.0f + view1.getWidth() / 2, xy[1] * 1.0f + view1.getHeight() / 2};
                        xyMap.put(i, xyf);
                    }
                }
                intent.putExtra("xyMap", xyMap);
                startActivity(intent);
            }


        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.takePhoto);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), 1);

            }
        });
    }

    private void initView() {
        rv = (RecyclerView) findViewById(R.id.rv);
        GridLayoutManager glm = new GridLayoutManager(mContext, 3);//定义3列的网格布局
        rv.setLayoutManager(glm);
        rv.addItemDecoration(new RecyclerViewItemDecoration(5, 3));//初始化子项距离和列数
        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.pictureloading)
                .showImageOnLoading(R.drawable.pictureloading)
                .showImageOnFail(R.drawable.pictureloading)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .displayer(new FadeInBitmapDisplayer(1))
                .build();
        adapter = new MyRecyclerViewAdapter(images, mContext, options, glm);
        rv.setAdapter(adapter);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 如果返回值是正常的话
        if (resultCode == Activity.RESULT_OK) {
            // 验证请求码是否一至，也就是startActivityForResult的第二个参数
            switch (requestCode) {
                case 1:
                    startGetImageThread();
                    break;

                default:
                    break;
            }
        }
    }

    public void startGetImageThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver contentResolver = getContentResolver();
                //获取jpeg和png格式的文件，并且按照时间进行倒序
                Cursor cursor = contentResolver.query(uri, null, MediaStore.Images.Media.MIME_TYPE + "=\"image/jpeg\" or " +
                        MediaStore.Images.Media.MIME_TYPE + "=\"image/png\"", null, MediaStore.Images.Media.DATE_MODIFIED + " desc");
                if (cursor != null) {
                    images.clear();
                    while (cursor.moveToNext()) {
                        String realPath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        String path = "file://" + cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        Log.d("1111", path);
                        images.add(path);
                        imagespath.add(realPath);
                        adapter.notifyDataSetChanged();
//                        try {
//                            singleImageModel.date = Long.parseLong(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED)));
//                        } catch (NumberFormatException e) {
//                            singleImageModel.date = System.currentTimeMillis();
//                        }
//                        try {
//                            singleImageModel.id = Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)));
//                        } catch (NumberFormatException e) {
//                            singleImageModel.id = 0;
//                        }
//                        allImages.add(singleImageModel);
//
//                        Log.d("Album", singleImageModel.path);
//                        Log.d("Album1", "" + singleImageModel.date);
//                        Log.d("Album2", "" + singleImageModel.id);
//
//                        //存入按照目录分配的list
//                        String path = singleImageModel.path;
//                        String parentPath = new File(path).getParent();
//                        putImageToParentDirectories(parentPath, path, singleImageModel.date, singleImageModel.id);
                    }

                    cursor.close();
                }

            }

        }).start();

    }

    public class RecyclerViewItemDecoration extends RecyclerView.ItemDecoration {
        private int itemSpace;//定义子项间距
        private int itemColumnNum;//定义子项的列数

        RecyclerViewItemDecoration(int itemSpace, int itemColumnNum) {
            this.itemSpace = itemSpace;
            this.itemColumnNum = itemColumnNum;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.bottom = itemSpace;//底部留出间距
            if (parent.getChildAdapterPosition(view) % itemColumnNum == 0)//每行第一项左边不留间距，其他留出间距
            {
                outRect.left = 0;
            } else {
                outRect.left = itemSpace;
            }

        }
    }

    /**
     * 重写startActivity方法，禁用activity默认动画
     */
    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(0, 0);
    }

    @Override

    public void

    onConfigurationChanged(Configuration newConfig) {

// TODO Auto-generated method stub

        super
                .onConfigurationChanged(newConfig);

        if
                (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

//横屏

            mCamera.setDisplayOrientation(
                    0
            );

        } else {

//竖屏

            mCamera.setDisplayOrientation(
                    90
            );

        }

    }
}
