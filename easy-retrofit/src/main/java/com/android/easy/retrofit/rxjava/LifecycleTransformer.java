package com.android.easy.retrofit.rxjava;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * @Description: 描述
 * @Author: yangxiong
 * @E-mail: abook23@163.com
 * @CreateDate: 2020/11/16 16:33
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/11/16 16:33
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class LifecycleTransformer<T> implements ObservableTransformer<T, T>, LifecycleObserver {

    private final Lifecycle mLifecycle;
    private boolean isDestroy;

    public LifecycleTransformer(Lifecycle lifecycle) {
        mLifecycle = lifecycle;
        mLifecycle.addObserver(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        mLifecycle.removeObserver(this);
        isDestroy = true;
    }

    @NonNull
    @Override
    public ObservableSource<T> apply(@NonNull Observable<T> upstream) {
        return upstream
                .takeUntil(new Predicate<T>() {
                    @Override
                    public boolean test(@NonNull T t) throws Exception {
                        return isDestroy;
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
