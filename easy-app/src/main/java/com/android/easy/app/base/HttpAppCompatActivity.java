package com.android.easy.app.base;

import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.android.easy.app.HttpCall;
import com.android.easy.retrofit.ApiService;
import com.android.easy.retrofit.FileService;
import com.android.easy.retrofit.RetrofitHttp;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

/**
 * 包含网络请求
 */
public class HttpAppCompatActivity extends AppCompatActivity {

    protected View contentLoadingView;

    protected <T> void get(String url, HttpCall<T> call) {
        RetrofitHttp.get(url, new HashMap<String, Object>(), call);
    }

    protected <T> void get(String url, Map<String, Object> params, HttpCall<T> call) {
        RetrofitHttp.get(url, params, call);
    }

    protected <T> void post(String url, Map<String, Object> params, HttpCall<T> call) {
        RetrofitHttp.post(url, params, call);
    }

    /**
     * 简单上传
     */
    protected <T> void upload(String url, Map<String, Object> params, HttpCall<T> call) {
        RetrofitHttp.upload(url, params, call);
    }

    /**
     * 简单下载
     */
    protected Observable<File> download(String url) {
        return RetrofitHttp.download(url);
    }


    protected void upload(String url, Map<String, Object> params, com.android.easy.retrofit.listener.loading.Call call) {
        FileService.upload(url, params, call);
    }

    protected void download(String url, com.android.easy.retrofit.listener.download.Call call) {
        RetrofitHttp.download(url, call);
    }


    protected <T> void get(String url, Map<String, Object> params, final boolean showProgress, final HttpCall<T> call) {
        RetrofitHttp.get(url, params, new HttpCall<T>() {
            @Override
            public void onSubscribe(Disposable d) {
                super.onSubscribe(d);
                if (showProgress) {
                    contentLoadingView.setVisibility(View.VISIBLE);
                }
                call.onSubscribe(d);
            }

            @Override
            public void onSuccess(T t) {
                if (showProgress) {
                    contentLoadingView.setVisibility(View.GONE);
                }
                call.onSuccess(t);
            }

            @Override
            public void onError(Throwable e) {
                if (showProgress) {
                    contentLoadingView.setVisibility(View.GONE);
                }
                super.onError(e);
                call.onError(e);
            }
        });
    }

    protected <T> void post(String url, Map<String, Object> params, final boolean showProgress, final HttpCall<T> call) {
        RetrofitHttp.post(url, params, new HttpCall<T>() {
            @Override
            public void onSubscribe(Disposable d) {
                super.onSubscribe(d);
                if (showProgress) {
                    contentLoadingView.setVisibility(View.VISIBLE);
                }
                call.onSubscribe(d);
            }

            @Override
            public void onSuccess(T t) {
                if (showProgress) {
                    contentLoadingView.setVisibility(View.GONE);
                }
                call.onSuccess(t);
            }

            @Override
            public void onError(Throwable e) {
                if (showProgress) {
                    contentLoadingView.setVisibility(View.GONE);
                }
                super.onError(e);
                call.onError(e);
            }
        });
    }
}
