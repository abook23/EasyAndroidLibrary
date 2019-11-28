package com.android.easy.app.base;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.ColorInt;

public abstract class BaseAppBar {

    private View mRootView;
    protected OnAppBarListener mOnAppBarListener;

    public BaseAppBar(ViewGroup viewGroup) {
        mRootView = onCreateView(viewGroup);
    }

    protected abstract View onCreateView(ViewGroup viewGroup);

    public void hideAppBar() {
        getRootView().setVisibility(View.GONE);
    }

    public View getRootView() {
        return mRootView;
    }

    public abstract void setTitle(String title, @ColorInt int color);

    public abstract ImageView getNavigation();

    public void hideNavigation() {
        getNavigation().setVisibility(View.INVISIBLE);
    }


    public void setOnAppBarListener(OnAppBarListener onAppBarListener) {
        mOnAppBarListener = onAppBarListener;
    }

    public interface OnAppBarListener {
        void onNavigationClick(View view);

        void onAppBarClick(View view);
    }
}
