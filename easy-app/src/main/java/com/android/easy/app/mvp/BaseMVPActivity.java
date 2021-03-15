package com.android.easy.app.mvp;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.easy.app.base.BaseAppCompatActivity;
import com.android.easy.app.util.GenericTypesUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;


public abstract class BaseMVPActivity<P extends BasePresenter> extends BaseAppCompatActivity implements BaseView {

    protected P mPresenter;

//    public abstract P initPresenter();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
//          mPresenter = initPresenter();
            mPresenter = GenericTypesUtils.newInstancePresenter(getClass());//反映射实例化 Presenter
            if (mPresenter != null){
                mPresenter.attachView(this);
            }
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void dismissLoading() {

    }

    @Override
    public Context getContext() {
        return this;
    }


}
