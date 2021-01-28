package com.android.easy.app.mvp;

import com.android.easy.app.HttpCall;
import com.android.easy.retrofit.ApiService;

import java.util.Map;

public abstract class BaseModel {
    protected int pageSize = 15;
    protected <T> void get(String url, Map<String, Object> params, HttpCall<T> call) {
        ApiService.get(url, params, call);
    }

    protected <T> void post(String url, Map<String, Object> params, HttpCall<T> call) {
        ApiService.post(url, params, call);
    }
}
