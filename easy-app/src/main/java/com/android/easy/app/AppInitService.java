package com.android.easy.app;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.android.easy.base.spf.SharedPreferencesUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AppInitService extends IntentService {

    public AppInitService() {
        super("AppInitService");
    }

    public static void startAppInitService(){

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        SharedPreferencesUtils.initialize(this);
        //初始化retrofit2
//        ApiService apiService = ApiService.init(this, URL.BaseUrl);
        //添加token
//        apiService.addInterceptor(new TokenInterceptor());
    }

    public static class TokenInterceptor implements Interceptor {
        private String _token;

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request()
                    .newBuilder()
                    .addHeader("token", getToken())
                    .build();
            return chain.proceed(request);
        }

        private String getToken() {
            if (_token == null) {
                _token = SharedPreferencesUtils.getParam("token");
            }
            return _token;
        }
    }
}
