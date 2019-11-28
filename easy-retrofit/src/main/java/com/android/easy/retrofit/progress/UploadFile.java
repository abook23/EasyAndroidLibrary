package com.android.easy.retrofit.progress;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.android.easy.retrofit.Api;
import com.android.easy.retrofit.FileService;
import com.android.easy.retrofit.listener.loading.Call;
import com.android.easy.retrofit.rxjava.ObserverBaseWeb;
import com.android.easy.retrofit.rxjava.ResponseCodeError;
import com.android.easy.retrofit.util.MultipartUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * Created by abook23 on 2016/11/25.
 * Versions 1.0
 */

public class UploadFile {

    private Call mCall;
    private boolean pause;
    private boolean cancel;
    private boolean isStart;

    private static final int KEY_START = 0x01;
    private static final int KEY_SIZE = 0x02;

    public UploadFile(String url, File file) {
        Map<String, Object> map = new HashMap<>();
        map.put(file.getName(), file);
        upload(url, map);
    }

    public UploadFile(String url, List<File> files) {
        Map<String, Object> map = new HashMap<>();
        for (File file : files) {
            map.put(file.getName(), file);
        }
        upload(url, map);
    }

    /**
     * from 表单提交
     *
     * @param url       地址
     * @param objectMap 参数和file
     */
    public UploadFile(String url, Map<String, Object> objectMap) {
        upload(url, objectMap);
    }

    public void setOnListener(Call call) {
        mCall = call;
    }

    private void upload(String url, Map<String, Object> objectMap) {
        if (mCall != null) {
            mCall.onStart();
            isStart = true;
        }
        FileService.getInit().create(Api.class, new OnUpLoadingListener() {
            @Override
            public void onProgress(long bytesRead, long contentLength, boolean done) {
                if (!isStart && mCall != null) {
                    isStart = true;
                    mHandler.obtainMessage(KEY_START).sendToTarget();
                }
                if (mCall != null)
                    mHandler.obtainMessage(KEY_SIZE, new long[]{bytesRead, contentLength}).sendToTarget();
                while (pause) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (cancel) {
                    throw new ResponseCodeError("cancel");
                }
            }
        }).uploading(url, MultipartUtils.filesToMultipartBody(objectMap))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ObserverBaseWeb<ResponseBody>() {

                               @Override
                               public void onNext(ResponseBody responseBody) {
                                   mCall.onSuccess(responseBody);
                               }

                               @Override
                               public void onError(Throwable e) {
                                   super.onError(e);
                                   if (!cancel && mCall != null) {
                                       mCall.onFail(e);
                                   }
                               }
                           }

                );
    }

    public boolean isPause() {
        return pause;
    }

    public void pause() {
        pause = true;
        mCall.onPause();
    }

    public void resume() {
        pause = false;
        mCall.onResume();
    }

    public void cancel() {
        cancel = true;
        pause = false;
        mCall.onCancel();
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case KEY_START:
                    mCall.onStart();
                    break;
                case KEY_SIZE:
                    long[] values = (long[]) msg.obj;
                    mCall.onSize(values[0], values[1]);
                    break;
            }
        }
    };
}
