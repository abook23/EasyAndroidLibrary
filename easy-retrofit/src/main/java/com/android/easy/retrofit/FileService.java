package com.android.easy.retrofit;

import android.content.Context;

import com.android.easy.retrofit.listener.Call;
import com.android.easy.retrofit.progress.DownloadFile;
import com.android.easy.retrofit.progress.OnDownloadListener;
import com.android.easy.retrofit.progress.OnUploadingListener;
import com.android.easy.retrofit.progress.ProgressRequestBody;
import com.android.easy.retrofit.progress.ProgressResponseBody;
import com.android.easy.retrofit.progress.UploadFile;
import com.android.easy.retrofit.rxjava.RxJavaUtils;
import com.android.easy.retrofit.util.AppUtils;
import com.android.easy.retrofit.util.FileUtils;
import com.android.easy.retrofit.util.MultipartUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by abook23 on 2016/11/22.
 * Versions 1.0
 */

public class FileService {

    private String baseUrl;
    private static FileService sFileService;
    public static boolean DEBUG = true;
    public static long CONNECT_TIMEOUT_SECONDS = 60;
    public static long READ_TIMEOUT_SECONDS = 600;
    public static long WRITE_TIMEOUT_SECONDS = 600;

    private List<Interceptor> interceptorList = new ArrayList<>();

    public static FileService init(Context applicationContext, String baseUrl) {
        sFileService = new FileService();
        sFileService.baseUrl = baseUrl;
        AppUtils.initial(applicationContext);
        return sFileService;
    }
    public static FileService getInstance() {
        if (sFileService == null) {
            init(AppUtils.getApplicationContext(), AppUtils.getBaseUrl());
        }
        return sFileService;
    }

    private Retrofit.Builder getBuilder() {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create());
    }
    public void addInterceptor(Interceptor interceptor) {
        interceptorList.add(interceptor);
    }

    public <T> T create(Class<T> tClass) {
        OkHttpClient.Builder builder = getOkHttpBuilder();
        return sFileService.getBuilder()
                .client(builder.build())
                .build()
                .create(tClass);
    }

    /**
     * 创建带响应进度(下载进度)回调的service
     */
    public <T> T create(Class<T> tClass, final OnDownloadListener listener) {
        OkHttpClient.Builder builder = getOkHttpBuilder();
        builder.addNetworkInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                //拦截
                Response originalResponse = chain.proceed(chain.request());
                //包装响应体并返回
                return originalResponse.newBuilder()
                        .body(new ProgressResponseBody(originalResponse.body(), listener))
                        .build();
            }
        });
        return sFileService.getBuilder()
                .client(builder.build())
                .build()
                .create(tClass);
    }

    /**
     * 创建带请求体进度(上传进度)回调的service
     */
    public <T> T create(Class<T> tClass, final OnUploadingListener listener) {
        OkHttpClient.Builder builder = getOkHttpBuilder();
        //增加拦截器
        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request request = original.newBuilder()
                        .method(original.method(), new ProgressRequestBody(original.body(), listener))
                        .build();
                return chain.proceed(request);
            }
        });
        return sFileService.getBuilder()
                .client(builder.build())
                .build()
                .create(tClass);
    }

    private OkHttpClient.Builder getOkHttpBuilder() {
        OkHttpClient.Builder builder = RetrofitHttp.getInitOkHttpClientBuilder();//集成来着 RetrofitHttp 里的设置
//        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        setTimeOut(builder);
        for (Interceptor interceptor : interceptorList) {
            builder.addInterceptor(interceptor);
        }
        return builder;
    }

    private void setTimeOut(OkHttpClient.Builder builder) {
        TimeUnit timeUnit = TimeUnit.SECONDS;
        builder.connectTimeout(CONNECT_TIMEOUT_SECONDS, timeUnit);
        builder.readTimeout(READ_TIMEOUT_SECONDS, timeUnit);
        builder.writeTimeout(WRITE_TIMEOUT_SECONDS, timeUnit);
    }

    public Observable<File> download(String url) {
        final String fileName = url.substring(url.lastIndexOf("/") + 1);
        return getInstance().create(Api.class).download(url).map(new Function<ResponseBody, File>() {

            @Override
            public File apply(@NonNull ResponseBody responseBody) throws Exception {
                return FileUtils.saveFile(responseBody.byteStream(), FileUtils.getDownloadDir(AppUtils.getApplicationContext()), fileName);
            }
        }).compose(RxJavaUtils.<File>defaultSchedulers());
    }

    public static DownloadFile download(String url, com.android.easy.retrofit.listener.download.Call call) {
        return new DownloadFile(url, call);
    }

    public Observable<String> upload(String url, String name, List<File> files) {
        return getInstance().create(Api.class)
                .uploading(url, MultipartUtils.filesToMultipartBody(name, files))
                .compose(RxJavaUtils.<ResponseBody>defaultSchedulers())
                .map(new Function<ResponseBody, String>() {
                    @Override
                    public String apply(@NonNull ResponseBody responseBody) throws Exception {
                        return responseBody.string();
                    }
                });
    }

    public <T> void upload(String url, String name, List<File> files, Call<T> call) {
        getInstance().create(Api.class)
                .uploading(url, MultipartUtils.filesToMultipartBody(name, files))
                .compose(RxJavaUtils.<ResponseBody>defaultSchedulers())
                .subscribe(call);
    }


    public static UploadFile upload(String url, String name, File file, com.android.easy.retrofit.listener.loading.Call call) {
        return new UploadFile(url, name, file, call);
    }

    public static UploadFile upload(String url, String name, List<File> files, com.android.easy.retrofit.listener.loading.Call call) {
        return new UploadFile(url, name, files, call);
    }

    public static UploadFile upload(String url, Map<String, Object> params, com.android.easy.retrofit.listener.loading.Call call) {
        return new UploadFile(url, params, call);
    }


}
