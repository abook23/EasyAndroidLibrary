package com.android.easy.retrofit.rxjava;


import android.widget.Toast;

import com.android.easy.retrofit.util.AppUtils;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;

/**
 * Created by abook23 on 2016/11/18.
 * Versions 1.0
 */

public abstract class ObserverBaseWeb<T> implements Observer<T> {

    @Override
    public void onComplete() {

    }

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onError(Throwable e) {
        String error;
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            error = httpException.code() + httpException.message();
        } else if (e instanceof ConnectException) {
            error = "连接异常";
        } else if (e instanceof SocketTimeoutException) {
            error = "连接超时";
        } else if (e instanceof UnknownHostException) {
            error = "网络异常,请检测网络状态";
            e.printStackTrace();
        } else if (e instanceof IOException) {
            error = "Please check your network status\n" + e.getMessage();
            e.printStackTrace();
        } else {
            error = e.getMessage();
            e.printStackTrace();
        }
        if (AppUtils.getApplicationContext() != null)
            Toast.makeText(AppUtils.getApplicationContext(), error, Toast.LENGTH_SHORT).show();
    }

}
