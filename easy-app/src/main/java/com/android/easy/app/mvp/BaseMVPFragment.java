package com.android.easy.app.mvp;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.easy.app.base.BaseFragment;
import com.android.easy.app.util.GenericTypesUtils;


public abstract class BaseMVPFragment<P extends BasePresenter> extends BaseFragment implements BaseView {

    protected P mPresenter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            mPresenter = GenericTypesUtils.newInstancePresenter(getClass());
            if (mPresenter != null){
                mPresenter.attachView(this);
            }
        } catch (java.lang.InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
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
