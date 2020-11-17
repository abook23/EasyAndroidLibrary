package com.android.easy.okhttp.initerceptor;

import com.android.easy.okhttp.util.AppUtils;
import com.android.easy.okhttp.util.NetWorkUtils;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;
import okio.GzipSink;
import okio.Okio;

/**
 * Created by abook23 on 2016/11/21.
 * Versions 1.0
 */
public class CommonInterceptor implements Interceptor {
    /**
     * 这里是设置缓存的
     * @param chain
     * @return
     * @throws IOException
     */
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Request.Builder newBuilder = originalRequest.newBuilder();
        Request compressedRequest;
        boolean netWork = NetWorkUtils.isNetConnect(AppUtils.getApplicationContext());
        if (!netWork) {
            newBuilder.cacheControl(CacheControl.FORCE_CACHE);//从缓存中读取
        } else {
            newBuilder.cacheControl(CacheControl.FORCE_NETWORK);
        }
         newBuilder.header("User-Agent", "KKWeight_Android");
//
//        if (originalRequest.body() == null || originalRequest.header("Content-Encoding") != null) {
//            newBuilder
//                    .header("User-Agent", "KKTablet/Android");
////                    .header("Content-Type", "application/x-www-form-urlencoded");
//        } else {
//            newBuilder.header("User-Agent", "KKTablet/Android")
////                    .header("Content-Type", "application/octet-stream")
//                    .header("Content-Encoding", "gzip")
//                    .method(originalRequest.method(), gzip(originalRequest.body()));
//        }
        compressedRequest = newBuilder.build();
        Response response = chain.proceed(compressedRequest);

        if (netWork) {
            int maxAge = 30 * 2; // 有网络时 设置缓存超时时间一小时
            response = response.newBuilder()
                    .removeHeader("Pragma")
                    //清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
                    .header("Cache-Control", "public, max-age=" + maxAge)//设置缓存超时时间
                    .build();
        } else {
            int maxStale = 60 * 5; // 无网络时，设置超时为5分钟
            response = response.newBuilder()
                    .removeHeader("Pragma")
                    .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                    //设置缓存策略，及超时策略
                    .build();
        }
        return response;
    }

    private RequestBody gzip(final RequestBody body) {
        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return body.contentType();
            }

            @Override
            public long contentLength() {
                return -1; // 无法知道压缩后的数据大小
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                BufferedSink gzipSink = Okio.buffer(new GzipSink(sink));
                body.writeTo(gzipSink);
                gzipSink.close();
            }
        };
    }
}
