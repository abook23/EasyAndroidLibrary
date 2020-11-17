package com.android.easy.okhttp;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;

import com.android.easy.okhttp.initerceptor.CommonInterceptor;
import com.android.easy.okhttp.initerceptor.LoggingInterceptor;
import com.android.easy.okhttp.listener.Call;
import com.android.easy.okhttp.util.AppUtils;
import com.android.easy.okhttp.util.FileUtils;
import com.android.easy.okhttp.util.MultipartUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @Description: 描述
 * @Author: yangxiong
 * @E-mail: abook23@163.com
 * @CreateDate: 2020/10/26 16:05
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/10/26 16:05
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class OkHttp {
    private static OkHttp sOkHttp;
    public static boolean DEBUG = true;
    public static boolean CACHE = false;
    private long readTimeOut = 30;
    private long connectTimeOut = 30;
    private TimeUnit timeUnit = TimeUnit.MILLISECONDS;
    private final Application mApplication;
    private OkHttpClient mClient;
    private List<Interceptor> interceptorList = new ArrayList<>();

    public static OkHttp init(Application application) {
        if (sOkHttp == null) {
            sOkHttp = new OkHttp(application);
        }
        return sOkHttp;
    }

    public static OkHttp getInstance() {
        return sOkHttp;
    }

    private OkHttp(Application application) {
        mApplication = application;
        AppUtils.initial(application);
    }

    public OkHttp setTimeOut(long readTimeOut, int connectTimeOut) {
        setTimeOut(readTimeOut, connectTimeOut, TimeUnit.MILLISECONDS);
        return this;
    }

    public OkHttp setTimeOut(long readTimeOut, int connectTimeOut, TimeUnit timeUnit) {
        this.readTimeOut = readTimeOut;
        this.connectTimeOut = connectTimeOut;
        this.timeUnit = timeUnit;
        return this;
    }

    public OkHttp addInterceptor(Interceptor interceptor) {
        interceptorList.add(interceptor);
        return this;
    }

    private OkHttpClient getOkHttpClient() {
        if (mClient == null) {
            OkHttpClient.Builder builder = getOkHttpClientBuilder();
            mClient = builder.build();
        }
        return mClient;
    }

    private OkHttpClient.Builder getOkHttpClientBuilder() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .readTimeout(readTimeOut, timeUnit)
                .connectTimeout(connectTimeOut, timeUnit)
                .cookieJar(new CookieManger(mApplication));//cookie session 长链
        if (CACHE) {//缓存
            builder.addNetworkInterceptor(new CommonInterceptor())
                    .cache(new Cache(new File(FileUtils.getDiskCacheDir(AppUtils.getApplicationContext()), "httpCache"), 10 * 1024 * 1024));//缓存,可用不用
        }
        if (DEBUG) {//日志监听
            builder.addNetworkInterceptor(new LoggingInterceptor(DEBUG, LoggingInterceptor.LogModel.CONCISE));
        }
        for (Interceptor interceptor : interceptorList) {
            builder.addInterceptor(interceptor);
        }
        return builder;
    }

    private <T> void get(String url, Call<T> call) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        okhttp3.Call newCall = getOkHttpClient().newCall(request);
        newCall.enqueue(call);
    }

    private <T> void get(String url, Map<String, Object> params, Call<T> call) {
        HttpUrl.Builder builder = HttpUrl.parse(url).newBuilder();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            builder.addQueryParameter(entry.getKey(),String.valueOf(entry.getValue()));
        }
        Request request = new Request.Builder()
                .url(builder.build())
                .build();
        okhttp3.Call newCall = getOkHttpClient().newCall(request);
        newCall.enqueue(call);
    }

    private <T> void post(String url, Map<String, Object> params, Call<T> call) {
        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (entry.getValue() != null) {
                builder.add(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }
        Request request = new Request.Builder()
                .url(url)
                .post(builder.build())
                .build();
        okhttp3.Call newCall = getOkHttpClient().newCall(request);
        newCall.enqueue(call);
    }

    private <T> void upload(String url, Map<String, Object> params, Call<T> call) {
        Request request = new Request.Builder()
                .url(url)
                .post(MultipartUtils.filesToMultipartBody(params))
                .build();
        okhttp3.Call newCall = getOkHttpClient().newCall(request);
        newCall.enqueue(call);
    }

    private UploadFile upload(String url, Map<String, Object> params, com.android.easy.okhttp.listener.upload.Call call) {
        UploadFile uploadFile = new UploadFile(url, params);
        uploadFile.addListener(call);
        return uploadFile;
    }

    private DownloadFile download(String url, Map<String, Object> params, com.android.easy.okhttp.listener.download.Call call) {
        return new DownloadFile(url, call);
    }

}
