package com.android.easy.okhttp.initerceptor;


import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 接口过期验证
 */

public abstract class ResponseInterceptor implements Interceptor {
    private static final Charset UTF8 = StandardCharsets.UTF_8;

    @Override
    public Response intercept(final Chain chain) throws IOException {
        final Request request = chain.request();
        final Response response = chain.proceed(request);
        if (onResponse(response, response.code())) {
            Call call = onRequest();
            if (call != null) {
                String data = call.execute().body().string();
                Request newRequest = onNewRequest(request, data);
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

    protected Call onRequest() {
        return null;
    }


    /**
     * 重新请求 原来的接口
     *
     * @param OldRequest
     * @param data
     * @return
     */
    protected Request onNewRequest(Request OldRequest, String data) {
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
