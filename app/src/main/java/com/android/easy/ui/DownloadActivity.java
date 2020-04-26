package com.android.easy.ui;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.android.easy.R;
import com.android.easy.app.HttpCall;
import com.android.easy.app.base.BaseAppCompatActivity;
import com.android.easy.base.widget.ButtonProgress;
import com.android.easy.retrofit.ApiService;
import com.android.easy.retrofit.FileService;
import com.android.easy.retrofit.RetrofitHttp;
import com.android.easy.retrofit.listener.download.Call;
import com.android.easy.retrofit.progress.DownloadFile;
import com.android.easy.retrofit.progress.UploadFile;
import com.android.easy.retrofit.rxjava.ObserverBaseWeb;

import java.io.File;
import java.util.HashMap;

import okhttp3.ResponseBody;

public class DownloadActivity extends BaseAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
    }

    public void download() {
        download("").subscribe(new ObserverBaseWeb<File>() {
            @Override
            public void onNext(File file) {

            }
        });
    }

    public void upload() {
        upload("", new HashMap<String, Object>(), new HttpCall<Object>() {
            @Override
            public void onSuccess(@NonNull Object o) {

            }
        });
    }

    /**
     * 进度上传
     *
     * @param view
     */
    public void uploadFileClick(View view) {
        uploadProgress((ButtonProgress) view);
    }

    /**
     * 进度条下载
     *
     * @param view
     */
    public void downloadFileClick(View view) {
        downloadProgress((ButtonProgress) view);
    }

    public void downloadProgress(ButtonProgress view) {
        DownloadFile downloadFile = RetrofitHttp.download("", new Call() {
            @Override
            public void onStart() {

            }

            @Override
            public void onPause() {

            }

            @Override
            public void onResume() {

            }

            @Override
            public void onSize(long size, long maxSize) {
                view.setMax(maxSize);
                view.setProgress(size);
            }

            @Override
            public void onFail(Throwable e) {

            }

            @Override
            public void onSuccess(File file) {

            }

            @Override
            public void onCancel() {

            }
        });
    }


    public void uploadProgress(ButtonProgress view) {
        UploadFile uploadFile = RetrofitHttp.upload("url", new HashMap<>(), new com.android.easy.retrofit.listener.loading.Call() {
            @Override
            public void onStart() {

            }

            @Override
            public void onPause() {

            }

            @Override
            public void onResume() {

            }

            @Override
            public void onSize(long size, long maxSize) {
                view.setMax(maxSize);
                view.setProgress(size);
            }

            @Override
            public void onFail(Throwable e) {

            }

            @Override
            public void onSuccess(ResponseBody responseBody) {

            }

            @Override
            public void onCancel() {

            }
        });
    }

}
