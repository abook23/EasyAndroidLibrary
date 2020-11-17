package com.android.easy.retrofit.rxjava;


import androidx.lifecycle.Lifecycle;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
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
            @NonNull
            @Override
            public ObservableSource<T> apply(@NonNull Observable<T> upstream) {
                return upstream
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    /**
     * 绑定生命周期
     * @param lifecycle Android 生命周期
     */
    public static <T> LifecycleTransformer<T> defaultSchedulers(Lifecycle lifecycle) {
        return new LifecycleTransformer<>(lifecycle);
    }
}
