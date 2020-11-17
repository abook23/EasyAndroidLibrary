package com.android.easy.okhttp;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.android.easy.okhttp.listener.download.Call;
import com.android.easy.okhttp.listener.download.OnDownloadListener;
import com.android.easy.okhttp.listener.download.ProgressResponseBody;
import com.android.easy.okhttp.util.AppUtils;
import com.android.easy.okhttp.util.FileUtils;

import java.io.File;
import java.io.IOException;

import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by abook23 on 2016/11/25.
 * Versions 1.0
 */

public class DownloadFile {

    private Call mCall;
    private boolean mPause;
    private boolean cancel;
    private static final int KEY_SIZE = 0x02;

    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == KEY_SIZE) {
                if (mCall != null)
                    mCall.onSize(msg.arg1, msg.arg2);
            }
        }
    };

    public DownloadFile(String url, Call call) {
        mCall = call;
        download(url);
    }

    private void download(String url) {
        final String parent = FileUtils.getDowloadDir(AppUtils.getApplicationContext());
        final String fileName = url.substring(url.lastIndexOf("/") + 1);
        //.download(mUrl,"bytes=" + startByte + "-");断点续传
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        okHttpClientBuilder.addNetworkInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                //拦截
                Response originalResponse = chain.proceed(chain.request());
                //包装响应体并返回
                return originalResponse.newBuilder()
                        .body(new ProgressResponseBody(originalResponse.body(), mOnDownloadListener))
                        .build();
            }
        });
        OkHttpClient okHttpClient = okHttpClientBuilder.build();
        Request.Builder builder = new Request.Builder().url(url);
        okHttpClient.newCall(builder.build()).enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                if (!cancel && mCall != null)
                    mCall.onFail(e);
                String filePath = parent + File.separator + fileName;
                File file = new File(filePath);
                if (file.exists()) {
                    file.delete();
                }
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                if (mCall != null) {
                    mCall.onStart();
                }
                if (response.isSuccessful()){
                    ResponseBody responseBody = response.body();
                    File file = FileUtils.saveFile(responseBody.byteStream(), parent, fileName);
                    if (mCall != null)
                        mCall.onSuccess(file);
                }else {

                }
            }
        });
    }

    public boolean isPause() {
        return mPause;
    }

    public void pause() {
        mPause = true;
        if (mCall != null)
            mCall.onPause();
    }

    public void resume() {
        mPause = false;
        if (mCall != null)
            mCall.onResume();
    }

    public void cancel() {
        cancel = true;
        mPause = false;
        if (mCall != null)
            mCall.onCancel();
    }

    private OnDownloadListener mOnDownloadListener = new OnDownloadListener() {
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
                throw new RuntimeException("cancel");
            }
            mHandler.obtainMessage(KEY_SIZE, (int) bytesRead, (int) contentLength).sendToTarget();
        }
    };
}
