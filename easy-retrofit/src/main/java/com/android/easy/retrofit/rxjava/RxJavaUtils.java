package com.android.easy.retrofit.rxjava;


import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by abook23 on 2016/11/18.
 * Versions 1.0
 */

public class RxJavaUtils {

    /**
     * 请求在io 线程
     * 响应在 UI线程
     * observable.compose(RxJavaUtils.defaultSchedulers())
     */
    public static <T> ObservableTransformer<T, T> defaultSchedulers() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }
}
