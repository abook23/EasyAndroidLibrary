package com.android.easy.retrofit.initerceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author abook23@163.com
 * @date 2020/03/31
 */
public abstract class TokenInterceptor implements Interceptor {
    private String token;

    @Override
    public Response intercept(Chain chain) throws IOException {
        if (token == null) {
            token = getToken();
        }
        Request request = chain.request()
                .newBuilder()
                .addHeader(getHeaderTokenName(), token)
                .build();

        Response response = chain.proceed(request);
        if (testResponse(response)) {
            toLogin();
        }
//      return chain.proceed(request);
        return response;
    }

    public String getHeaderTokenName() {
        return "token";
    }

    public abstract String getToken();

    public abstract boolean testResponse(Response response);

    public abstract void toLogin();

}
