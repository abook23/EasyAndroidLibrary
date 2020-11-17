package com.android.easy.okhttp;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;


import com.android.easy.okhttp.listener.upload.Call;
import com.android.easy.okhttp.listener.upload.OnUpLoadingListener;
import com.android.easy.okhttp.listener.upload.ProgressRequestBody;
import com.android.easy.okhttp.util.MultipartUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
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

    public UploadFile(String url,String name, File file) {
        upload(url, MultipartUtils.filesToMultipartBody(name,file));
    }

    public UploadFile(String url,String name, List<File> files) {
        upload(url, MultipartUtils.filesToMultipartBody(name,files));
    }

    /**
     * from 表单提交
     *
     * @param url       地址
     * @param params 参数和file
     */
    public UploadFile(String url, Map<String, Object> params) {
        upload(url, MultipartUtils.filesToMultipartBody(params));
    }

    public void addListener(Call call) {
        mCall = call;
    }

    private void upload(String url, RequestBody requestBody) {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        okHttpClientBuilder.addNetworkInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request request = original.newBuilder()
                        .method(original.method(), new ProgressRequestBody(original.body(), mListener))
                        .build();
                return chain.proceed(request);
            }
        });
        OkHttpClient okHttpClient = okHttpClientBuilder.build();
        Request.Builder builder = new Request.Builder();
        builder.url(url).post(requestBody);
        Request request = builder.build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                if (!cancel && mCall != null) {
                    mCall.onFail(e);
                }
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                if (mCall != null) {
                    mCall.onStart();
                    isStart = true;
                }
                ResponseBody responseBody = response.body();
                mCall.onSuccess(responseBody);
            }
        });
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

    OnUpLoadingListener mListener = new OnUpLoadingListener() {
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
                throw new RuntimeException("cancel");
            }
        }
    };
}
