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

public abstract class TokenInterceptor<T> implements Interceptor {
    private static final Charset UTF8 = Charset.forName("UTF-8");

    @Override
    public Response intercept(final Chain chain) throws IOException {
        Request request = chain.request();

        Request.Builder newBuilder = request.newBuilder();
        setRequestBuilder(newBuilder);

        Response response = chain.proceed(newBuilder.build());
        if (onAuthenticator(response)) {//根据和服务端的约定判断 是否 过期
            //再次请求
            Call<T> call = onAfresh();
            T t = call.execute().body();
            onNewRequest(request, t);
            Request.Builder builder = request.newBuilder();
            setRequestBuilder(builder);
//            Request newRequest = request.newBuilder().build();
            response.body().close();
            return chain.proceed(builder.build());
        }
        // otherwise just pass the original response on
        return response;
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

    protected abstract void setRequestBuilder(Request.Builder newBuilder);

    /**
     * 接口验证
     *
     * @param response
     * @return
     */
    protected abstract boolean onAuthenticator(Response response);

    /**
     * 验证
     *
     * @return
     */
    protected abstract Call<T> onAfresh();

    /**
     * 重新请求 原来的接口
     *
     * @param request
     * @param t
     */
    protected abstract void onNewRequest(Request request, T t);

}
