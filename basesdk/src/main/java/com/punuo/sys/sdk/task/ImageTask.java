package com.punuo.sys.sdk.task;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import com.punuo.sys.sdk.PnApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by han.chen.
 * Date on 2021/2/19.
 **/
public class ImageTask extends AsyncTask<Object, Void, List<String>> {
    private final CallBack mCallBack;
    private final List<String> imageList = new ArrayList<>();
    public ImageTask(CallBack callBack) {
        mCallBack = callBack;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        imageList.clear();
    }

    @Override
    protected List<String> doInBackground(Object... objects) {
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver contentResolver = PnApplication.getInstance().getContentResolver();
        //获取jpeg和png格式的文件，并且按照时间进行倒序
        Cursor cursor = contentResolver.query(uri, null, MediaStore.Images.Media.MIME_TYPE + "=\"image/jpeg\" or " +
                MediaStore.Images.Media.MIME_TYPE + "=\"image/png\"", null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String realPath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                String path = "file://" + realPath;
                imageList.add(path);
            }
            cursor.close();
        }
        return imageList;
    }

    @Override
    protected void onPostExecute(List<String> imageList) {
        super.onPostExecute(imageList);
        if (mCallBack != null) {
            mCallBack.onGetImageList(imageList);
        }
    }

    public interface CallBack {
        void onGetImageList(List<String> imageList);
    }
}
