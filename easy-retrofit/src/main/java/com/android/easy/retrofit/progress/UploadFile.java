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
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
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

    private static final int KEY_SIZE = 0x02;
    private static final int KEY_BYTES = 0x03;
    private boolean isUpdateBytes;
    private ScheduledFuture<?> mScheduledFuture;

    public UploadFile(String url, String name, File file, Call call) {
        upload(url, MultipartUtils.filesToMultipartBody(name, file), call);
    }

    public UploadFile(String url, String name, List<File> files, Call call) {
        upload(url, MultipartUtils.filesToMultipartBody(name, files), call);
    }

    /**
     * from 表单提交
     *
     * @param url       地址
     * @param objectMap 参数和file
     */
    public UploadFile(String url, Map<String, Object> objectMap, Call call) {
        upload(url, MultipartUtils.filesToMultipartBody(objectMap), call);
    }

    private void upload(String url, MultipartBody multipartBody, Call call) {
        mCall = call;
        mCall.onStart();
        isStart = true;
        mScheduledFuture = Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(mRunnable, 0, 1000, TimeUnit.MILLISECONDS);
        FileService.getInstance().create(Api.class, new OnUploadingListener() {
            long byteSize = 0L;

            @Override
            public void onProgress(long bytesRead, long contentLength, boolean done) {
                if (isUpdateBytes) {
                    isUpdateBytes = false;
                    mHandler.obtainMessage(KEY_BYTES, bytesRead - byteSize).sendToTarget();
                    byteSize = bytesRead;
                }
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
        }).uploading(url, multipartBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ObserverBaseWeb<ResponseBody>() {

                               @Override
                               public void onNext(ResponseBody responseBody) {
                                   mScheduledFuture.cancel(true);
                                   mCall.onSuccess(responseBody);
                               }

                               @Override
                               public void onError(Throwable e) {
                                   super.onError(e);
                                   mScheduledFuture.cancel(true);
                                   if (!cancel && mCall != null) {
                                       mCall.onFail(e);
                                   }
                               }
                           }

                );
    }

    public Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            isUpdateBytes = true;
        }
    };

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
                case KEY_SIZE:
                    long[] values = (long[]) msg.obj;
                    mCall.onSize(values[0], values[1]);
                    break;
                case KEY_BYTES:
                    mCall.onBytes((Long) msg.obj);
                    break;
            }
        }
    };
}
