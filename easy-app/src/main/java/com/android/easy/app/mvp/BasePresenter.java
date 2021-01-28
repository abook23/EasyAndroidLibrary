package com.android.easy.app.mvp;


import java.lang.ref.WeakReference;

public abstract class BasePresenter<M extends BaseModel, V extends BaseView> {
    private WeakReference<V> mView;
    protected M mModel;


    public BasePresenter(M model) {
        mModel = model;
    }

    public void attachView(V view) {
        mView = new WeakReference<>(view);
    }

    public void detachView() {
        if (mView != null) {
            mView.clear();
            mView = null;
        }
        this.mModel = null;
    }

    public V getView() {
        return mView == null ? null : mView.get();
    }

    public M getModule() {
        return mModel;
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

}
