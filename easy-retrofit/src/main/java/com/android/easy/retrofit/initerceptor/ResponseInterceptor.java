package com.android.easy.retrofit.initerceptor;

import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.Call;

/**
 * 接口过期验证
 */

public abstract class ResponseInterceptor<T> implements Interceptor {
    private static final Charset UTF8 = Charset.forName("UTF-8");

    @Override
    public Response intercept(final Chain chain) throws IOException {
        final Request request = chain.request();
        final Response response = chain.proceed(request);
        if (onResponse(response, response.code())) {
            Call<T> call = onRequest();
            if (call != null) {
                T t = call.execute().body();
                Request newRequest = onNewRequest(request, t);
                if (newRequest == null) {
                    newRequest = request.newBuilder().build();
                }
//              Request newRequest = request.newBuilder().build();
                response.body().close();
                return chain.proceed(newRequest);
            }
        }
        // otherwise just pass the original response on
        return response;
    }

    protected Call<T> onRequest() {
        return null;
    }


    /**
     * 重新请求 原来的接口
     *
     * @param OldRequest
     * @param t
     * @return
     */
    protected Request onNewRequest(Request OldRequest, T t) {
        return null;
    }


    public String getBodyStr(Response response) {
        try {
            ResponseBody responseBody = response.body();
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE);
            Buffer buffer = source.buffer();
            Charset charset = UTF8;
            MediaType mediaType = responseBody.contentType();
            if (mediaType != null) {
                charset = mediaType.charset(UTF8);
            }
            return buffer.clone().readString(charset);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 接口验证
     *
     * @param response
     * @return
     */
    protected abstract boolean onResponse(Response response, int httpCode);

}
