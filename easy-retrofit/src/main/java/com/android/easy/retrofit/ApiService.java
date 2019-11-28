package com.android.easy.retrofit;

import android.content.Context;

import androidx.annotation.RawRes;

import com.android.easy.retrofit.config.CookieManger;
import com.android.easy.retrofit.config.SSLSocketManger;
import com.android.easy.retrofit.initerceptor.CommonInterceptor;
import com.android.easy.retrofit.initerceptor.LoggingInterceptor;
import com.android.easy.retrofit.listener.Call;
import com.android.easy.retrofit.rxjava.RxJavaUtils;
import com.android.easy.retrofit.util.AppUtils;
import com.android.easy.retrofit.util.FileUtils;
import com.android.easy.retrofit.util.MultipartUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLSocketFactory;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import okhttp3.Cache;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.android.easy.retrofit.util.FileUtils.getDiskCacheDir;


/**
 * Created by abook23 on 2016/11/18.
 * Versions 1.0
 */

public class ApiService {

    public static boolean DEBUG = true;
    public static boolean CACHE = false;

    public String baseUrl;
    private Retrofit mRetrofit;
    private int[] mCertificates;
    private static ApiService SERVICE;
    private long readTimeOut = 10;
    private int connectTimeOut = 10;
    private TimeUnit timeUnit = TimeUnit.SECONDS;
    private List<Interceptor> interceptorList = new ArrayList<>();

    public static ApiService init(Context applicationContext, String baseUrl) {
        AppUtils.initial(applicationContext.getApplicationContext(),baseUrl);
        SERVICE = new ApiService();
        SERVICE.baseUrl = baseUrl;
        return SERVICE;
    }

    public static ApiService init(Context applicationContext, String baseUrl, @RawRes int... cerIds) {
        AppUtils.initial(applicationContext.getApplicationContext(),baseUrl);
        SERVICE = new ApiService();
        SERVICE.baseUrl = baseUrl;
        SERVICE.mCertificates = cerIds;
        return SERVICE;
    }

    public void addInterceptor(Interceptor interceptor) {
        interceptorList.add(interceptor);
    }

    private Retrofit retrofit() {
        if (mRetrofit == null) {
            synchronized (ApiService.class) {
                if (mRetrofit == null) {
                    OkHttpClient okHttpClient = getBuilder().build();
                    mRetrofit = new Retrofit.Builder()
                            .baseUrl(baseUrl)//"http://172.16.0.22:8099"
                            .client(okHttpClient)
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                }
            }
        }
        return mRetrofit;
    }

    private OkHttpClient.Builder getBuilder() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .readTimeout(readTimeOut, timeUnit)
                .connectTimeout(connectTimeOut, timeUnit)
                .cookieJar(new CookieManger(AppUtils.getApplicationContext()));//cookie session 长链
        if (CACHE) {//缓存
            builder.addNetworkInterceptor(new CommonInterceptor())
                    .cache(new Cache(new File(getDiskCacheDir(AppUtils.getApplicationContext()), "httpCache"), 10 * 1024 * 1024));//缓存,可用不用
        }
        if (DEBUG) {//日志监听
            builder.addNetworkInterceptor(new LoggingInterceptor(DEBUG, LoggingInterceptor.LogModel.CONCISE));
        }
        if (mCertificates != null && mCertificates.length > 0) {//https (自定义证书)
            SSLSocketFactory sslSocketFactory = SSLSocketManger.getSSLSocketFactory(AppUtils.getApplicationContext(), mCertificates);
            if (sslSocketFactory != null)
                builder.socketFactory(sslSocketFactory);
        }
        for (Interceptor interceptor : interceptorList) {
            builder.addInterceptor(interceptor);
        }
        return builder;
    }

    public void setTimeOut(long readTimeOut, int connectTimeOut) {
        setTimeOut(readTimeOut, connectTimeOut, TimeUnit.SECONDS);
    }

    public void setTimeOut(long readTimeOut, int connectTimeOut, TimeUnit timeUnit) {
        this.readTimeOut = readTimeOut;
        this.connectTimeOut = connectTimeOut;
        this.timeUnit = timeUnit;
    }

    public static Retrofit getRetrofit() {
        return SERVICE.retrofit();
    }

    public static <T> T create(Class<T> tClass) {
        return SERVICE.retrofit().create(tClass);
    }

    public static <T> void get(String url, Map<String, Object> params, Call<T> call) {
        ApiService.create(Api.class)
                .get(url, params)
                .compose(RxJavaUtils.<ResponseBody>defaultSchedulers())
                .subscribe(call);
    }

    public static <T> void post(String url, Map<String, Object> params, Call<T> call) {
        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (entry.getValue() != null)
                builder.add(entry.getKey(), String.valueOf(entry.getValue()));
        }
        ApiService.create(Api.class)
                .post(url, builder.build())
                .compose(RxJavaUtils.<ResponseBody>defaultSchedulers())
                .subscribe(call);
    }

    public static <T> void upload(String url, Map<String, Object> params, Call<T> call) {
        ApiService.create(Api.class)
                .uploading(url, MultipartUtils.filesToMultipartBody(params))
                .compose(RxJavaUtils.<ResponseBody>defaultSchedulers())
                .subscribe(call);
    }

    public static Observable<File> download(String url) {
        final String fileName = url.substring(url.lastIndexOf("/") + 1);
        return ApiService.create(Api.class)
                .download(url)
                .map(new Function<ResponseBody, File>() {
                    @Override
                    public File apply(ResponseBody responseBody) throws Exception {
                        return FileUtils.saveFile(responseBody.byteStream(), FileUtils.getDowloadDir(AppUtils.getApplicationContext()), fileName);
                    }
                }).compose(RxJavaUtils.<File>defaultSchedulers());
    }

}
