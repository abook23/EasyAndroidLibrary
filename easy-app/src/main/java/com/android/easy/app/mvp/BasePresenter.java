package com.android.easy.app.mvp;


import com.android.easy.app.HttpCall;
import com.android.easy.retrofit.ApiService;

import java.lang.ref.WeakReference;
import java.util.Map;

public abstract class BasePresenter<M extends IBaseModel, V extends IBaseView> {
    private WeakReference<V> mMvpView;
    private M mMvpModel;


    public BasePresenter(M mvpModel) {
        mMvpModel = mvpModel;
    }

    public void attachView(V view) {
        mMvpView = new WeakReference<>(view);
    }

    public void detachView() {
        if (mMvpView != null) {
            mMvpView.clear();
            mMvpView = null;
        }
        this.mMvpModel = null;
    }

    public V getView() {
        return mMvpView == null ? null : mMvpView.get();
    }

    public M getModule() {
        return mMvpModel;
    }

    public void showLoading() {
        getView().showLoading();
    }

    public void dismissLoading() {
        getView().dismissLoading();
    }

    public void showToast(String msg) {
        getView().showToast(msg);
    }

    protected <T> void get(String url, Map<String, Object> params, HttpCall<T> call) {
        ApiService.get(url, params, call);
    }

    protected <T> void post(String url, Map<String, Object> params, HttpCall<T> call) {
        ApiService.post(url, params, call);
    }

}
