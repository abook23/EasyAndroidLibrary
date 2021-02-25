package com.android.easy.retrofit;

import android.content.Context;

import androidx.annotation.RawRes;
import androidx.lifecycle.Lifecycle;

import com.android.easy.retrofit.config.CookieManger;
import com.android.easy.retrofit.config.SSLSocketManger;
import com.android.easy.retrofit.initerceptor.CommonInterceptor;
import com.android.easy.retrofit.initerceptor.LoggingInterceptor;
import com.android.easy.retrofit.listener.Call;
import com.android.easy.retrofit.progress.DownloadFile;
import com.android.easy.retrofit.progress.UploadFile;
import com.android.easy.retrofit.rxjava.RxJavaUtils;
import com.android.easy.retrofit.util.AppUtils;
import com.android.easy.retrofit.util.FileUtils;
import com.android.easy.retrofit.util.MultipartUtils;

import java.io.File;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLSocketFactory;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import okhttp3.Cache;
import okhttp3.CertificatePinner;
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
    private static ApiService sApiService;
    private long readTimeOut = 10;
    private int connectTimeOut = 10;
    private TimeUnit timeUnit = TimeUnit.SECONDS;
    private List<Interceptor> interceptorList = new ArrayList<>();
    private static Api mApi;

    private Retrofit initRetrofit() {
        if (mRetrofit == null) {
            synchronized (ApiService.class) {
                if (mRetrofit == null) {
                    OkHttpClient okHttpClient = getOkHttpClientBuilder().build();
                    mRetrofit = new Retrofit.Builder()
                            .baseUrl(baseUrl)
                            .client(okHttpClient)
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                }
            }
        }
        return mRetrofit;
    }

    private OkHttpClient.Builder getOkHttpClientBuilder() {
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
            if (sslSocketFactory != null) {
                builder.socketFactory(sslSocketFactory);
            }
        }
        for (Interceptor interceptor : interceptorList) {
            builder.addInterceptor(interceptor);
        }
//        builder.certificatePinner(new CertificatePinner.Builder().add("https://www.abook23.com","sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=").build());
        return builder;
    }

    public ApiService setTimeOut(long readTimeOut, int connectTimeOut) {
        setTimeOut(readTimeOut, connectTimeOut, TimeUnit.SECONDS);
        return this;
    }

    public ApiService setTimeOut(long readTimeOut, int connectTimeOut, TimeUnit timeUnit) {
        this.readTimeOut = readTimeOut;
        this.connectTimeOut = connectTimeOut;
        this.timeUnit = timeUnit;
        return this;
    }

    public void addInterceptor(Interceptor interceptor) {
        sApiService.interceptorList.add(interceptor);
    }


    public static ApiService init(Context applicationContext, String baseUrl) {
        AppUtils.initial(applicationContext.getApplicationContext(), baseUrl);
        sApiService = new ApiService();
        sApiService.baseUrl = baseUrl;
        return sApiService;
    }

    public static ApiService init(Context applicationContext, String baseUrl, @RawRes int... cerIds) {
        AppUtils.initial(applicationContext.getApplicationContext(), baseUrl);
        sApiService = new ApiService();
        sApiService.baseUrl = baseUrl;
        sApiService.mCertificates = cerIds;
        return sApiService;
    }

    public static <T> T create(Class<T> tClass) {
        return sApiService.initRetrofit().create(tClass);
    }

    public static Api getApi() {
        if (mApi == null) {
            mApi = create(Api.class);
        }
        return mApi;
    }

    public static <T> void get(String url, Call<T> call) {
        get(url, new HashMap<String, Object>(), call);
    }

    public static <T> void get(String url, Map<String, Object> params, Call<T> call) {
        getApi()
                .get(url, params)
                .compose(RxJavaUtils.<ResponseBody>defaultSchedulers())
                .subscribe(call);
    }

    public static <T> void get(String url, Map<String, Object> params, Lifecycle lifecycle, Call<T> call) {
        getApi()
                .get(url, params)
                .compose(RxJavaUtils.<ResponseBody>defaultSchedulers(lifecycle))
                .subscribe(call);
    }


    public static <T> void post(String url, Map<String, Object> params, Call<T> call) {
        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (entry.getValue() != null) {
                builder.add(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }
        getApi()
                .post(url, builder.build())
                .compose(RxJavaUtils.<ResponseBody>defaultSchedulers())
                .subscribe(call);
    }

    public static <T> void post(String url, Map<String, Object> params,Lifecycle lifecycle, Call<T> call) {
        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (entry.getValue() != null) {
                builder.add(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }
        getApi()
                .post(url, builder.build())
                .compose(RxJavaUtils.<ResponseBody>defaultSchedulers(lifecycle))
                .subscribe(call);
    }

    public static <T> void upload(String url, Map<String, Object> params, Call<T> call) {
        getApi()
                .uploading(url, MultipartUtils.filesToMultipartBody(params))
                .compose(RxJavaUtils.<ResponseBody>defaultSchedulers())
                .subscribe(call);
    }

    public static Observable<File> download(String url) {
        final String fileName = url.substring(url.lastIndexOf("/") + 1);
        return getApi()
                .download(url)
                .map(new Function<ResponseBody, File>() {
                    @Override
                    public File apply(ResponseBody responseBody) throws Exception {
                        return FileUtils.saveFile(responseBody.byteStream(), FileUtils.getDowloadDir(AppUtils.getApplicationContext()), fileName);
                    }
                }).compose(RxJavaUtils.<File>defaultSchedulers());
    }


    /**
     * 文件上传
     * @param url
     * @param params
     * @param call
     * @return
     */
    public static UploadFile upload(String url, Map<String, Object> params, com.android.easy.retrofit.listener.loading.Call call) {
        return FileService.upload(url, params, call);
    }

    /**
     * 文件下载
     * @param url
     * @param call
     * @return
     */
    public static DownloadFile download(String url, com.android.easy.retrofit.listener.download.Call call) {
        return FileService.download(url, call);
    }

}
