package com.android.easy.retrofit.progress;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.android.easy.retrofit.Api;
import com.android.easy.retrofit.FileService;
import com.android.easy.retrofit.listener.download.Call;
import com.android.easy.retrofit.rxjava.ObserverBaseWeb;
import com.android.easy.retrofit.rxjava.ResponseCodeError;
import com.android.easy.retrofit.util.AppUtils;
import com.android.easy.retrofit.util.FileUtils;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * Created by abook23 on 2016/11/25.
 * Versions 1.0
 */

public class DownloadFile {

    private final ScheduledFuture<?> mScheduledFuture;
    private final Call mCall;
    private boolean mPause;
    private boolean cancel;
    private static final int KEY_SIZE = 0x02;
    private static final int KEY_BYTES = 0x03;

    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case KEY_SIZE:
                    mCall.onSize(msg.arg1, msg.arg2);
                    break;
                case KEY_BYTES:
                    mCall.onBytes((Long) msg.obj);
                    break;
            }
        }
    };
    public Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            isUpdateBytes = true;
        }
    };
    private boolean isUpdateBytes;

    public DownloadFile(String url, Call call) {
        mCall = call;
        mScheduledFuture = Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(mRunnable, 0, 1000, TimeUnit.MILLISECONDS);
        final String parent = FileUtils.getDownloadDir(AppUtils.getApplicationContext());
        final String fileName = url.substring(url.lastIndexOf("/") + 1);
        //.download(mUrl,"bytes=" + startByte + "-");断点续传
        FileService.getInstance().create(Api.class, mOnDownloadListener).download(url)
                .map(new Function<ResponseBody, File>() {
                    @Override
                    public File apply(ResponseBody responseBody) throws Exception {
                        return FileUtils.saveFile(responseBody.byteStream(), parent, fileName);
                    }

                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ObserverBaseWeb<File>() {
                    @Override
                    public void onNext(File file) {
                        mScheduledFuture.cancel(true);
                        mCall.onSuccess(file);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mScheduledFuture.cancel(true);
                        if (!cancel)
                            mCall.onFail(e);
                        String filePath = parent + File.separator + fileName;
                        File file = new File(filePath);
                        if (file.exists()) {
                            file.delete();
                        }
                    }
                });
    }

    public boolean isPause() {
        return mPause;
    }

    public void pause() {
        mPause = true;
        mCall.onPause();
    }

    public void resume() {
        mPause = false;

        mCall.onResume();
    }

    public void cancel() {
        cancel = true;
        mPause = false;
        mCall.onCancel();
    }

    private final OnDownloadListener mOnDownloadListener = new OnDownloadListener() {
        long byteSize = 0L;

        @Override
        public void onProgress(long bytesRead, long contentLength, boolean done) {
            while (mPause) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (cancel) {
                throw new ResponseCodeError("cancel");
            }
            if (isUpdateBytes) {
                isUpdateBytes = false;
                mHandler.obtainMessage(KEY_BYTES, bytesRead - byteSize).sendToTarget();
                byteSize = bytesRead;
            }
            mHandler.obtainMessage(KEY_SIZE, (int) bytesRead, (int) contentLength).sendToTarget();
        }
    };
}
