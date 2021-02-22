package com.punuo.sys.app.agedcare.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;

import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.punuo.sys.app.agedcare.R;
import com.punuo.sys.app.agedcare.adapter.MyRecyclerViewAdapter;
import com.punuo.sys.app.agedcare.tools.AlbumBitmapCacheHelper;
import com.punuo.sys.app.router.HomeRouter;
import com.punuo.sys.sdk.activity.BaseActivity;
import com.punuo.sys.sdk.task.ImageTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Route(path = HomeRouter.ROUTER_ALBUM_ACTIVITY)
public class AlbumActivity extends BaseActivity {

    private RecyclerView rv;
    private final List<String> images = new ArrayList<String>();//图片地址
    private Context mContext;
    private MyRecyclerViewAdapter adapter;
    private PopupMenu popup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_layout);
        mContext = this;
        initView();
        startGetImageThread();
        setEvent();
    }

    private void setEvent() {
        adapter.setmOnLongItemClickListener((view, position) -> {
            popup = new PopupMenu(AlbumActivity.this, view);
            popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(
                    item -> {
                        if (item.getItemId() == R.id.deletepicture) {
                            AlbumBitmapCacheHelper.getInstance().clearCache();
                            File file = new File(images.get(position).substring(7));
                            if (file.exists()) {
                                getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                        MediaStore.Images.Media.DATA + "=?", new String[]{images.get(position).substring(7)});
                                file.delete();
                            }
                            images.remove(position);
                            adapter.notifyDataSetChanged();
                        }
                        return true;
                    });
            popup.show();
        });
        adapter.setmOnItemClickListener((view, position) -> ARouter.getInstance().build(HomeRouter.ROUTER_IMAGE_PAGER_ACTIVITY)
                .withStringArrayList(ImagePagerActivity.EXTRA_IMAGE_URLS, (ArrayList<String>) images)
                .withInt(ImagePagerActivity.EXTRA_IMAGE_INDEX, position)
                .navigation());
        ImageView fab = (ImageView) findViewById(R.id.takePhoto);
        fab.setOnClickListener(v -> {
            Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraPhotoPath = getCameraPhotoPath();
            Uri imageUri = Uri.fromFile(new File(cameraPhotoPath));
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(captureIntent, 1);

        });
    }

    private String cameraPhotoPath;

    private String getCameraPhotoPath() {
        return Environment.getExternalStorageDirectory() + "/DCIM/Camera/" + System.currentTimeMillis() + ".png";
    }

    private void initView() {
        rv = (RecyclerView) findViewById(R.id.rv);
        GridLayoutManager glm = new GridLayoutManager(mContext, 3);//定义3列的网格布局
        rv.setLayoutManager(glm);
        adapter = new MyRecyclerViewAdapter(images, mContext, glm);
        rv.setAdapter(adapter);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 1:
                    Uri imageUri = Uri.fromFile(new File(cameraPhotoPath));
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, imageUri));
                    rv.postDelayed(this::startGetImageThread, 500);
                    break;

                default:
                    break;
            }
        }
    }

    public void startGetImageThread() {
        new ImageTask(imageList -> {
            images.clear();
            images.addAll(imageList);
            adapter.notifyDataSetChanged();
        }).execute();
    }
}
