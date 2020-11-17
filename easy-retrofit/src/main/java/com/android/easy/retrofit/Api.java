package com.android.easy.retrofit;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by abook23 on 2016/12/2.
 */

public interface Api {

    @POST()
    Observable<ResponseBody> uploading(@Url String url, @Body MultipartBody multipartBody);

    @POST()
    Observable<ResponseBody> uploading(@Url String url, @Body MultipartBody multipartBody, @Header("Range") String range);

    @Streaming
    @GET()
    Observable<ResponseBody> download(@Url() String url);

    @Streaming
    @GET()
    Observable<ResponseBody> download(@Url() String url, @Header("Range") String range);

    @GET
    Observable<ResponseBody> get(@Url() String url, @QueryMap Map<String, Object> params);

    @POST
    Observable<ResponseBody> post(@Url() String url, @Body RequestBody requestBody);
}
