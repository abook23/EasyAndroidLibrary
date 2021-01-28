package com.android.easy.app.mvp;

import android.content.Context;

public interface BaseView {
    void showLoading();

    void dismissLoading();

    void showToast(String msg);

    Context getContext();
}
