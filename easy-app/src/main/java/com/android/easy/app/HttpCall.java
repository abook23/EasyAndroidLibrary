package com.android.easy.app;


import com.android.easy.retrofit.listener.Call;

import io.reactivex.disposables.Disposable;

public abstract class HttpCall<T> extends Call<T> {

    public HttpCall(){
        super();
    }

    @Override
    public void onSubscribe(Disposable d) {
        super.onSubscribe(d);
    }

    @Override
    public void onComplete() {
        super.onComplete();
    }

    @Override
    public void onError(Throwable e) {
        super.onError(e);
    }
}