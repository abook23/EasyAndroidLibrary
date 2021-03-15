package com.android.easy.app.mvp;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.easy.app.base.BaseFragment;


public abstract class BaseMVPFragment<P extends BasePresenter> extends BaseFragment implements BaseView {

    protected P mPresenter;

    public abstract P initPresenter();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = initPresenter();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }

    @Override
    public void showLoading() {
    }

    @Override
    public void dismissLoading() {
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }
}
