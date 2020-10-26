package com.android.easy.app.base;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;

import com.android.easy.app.R;

public abstract class BaseAppBar {

    private View mRootView;
    private Activity mActivity;

    public BaseAppBar(Activity activity, ViewGroup viewGroup) {
        mActivity = activity;
        mRootView = LayoutInflater.from(viewGroup.getContext()).inflate(getAppBarLayout(), viewGroup, false);
        onViewCreated(mRootView);
        addListener();
    }

    private View getNavigation() {
        return getRootView().findViewById(getNavigationId());
    }

    public TextView getTitleView() {
        return getRootView().findViewById(getTitleId());
    }

    public void setTitle(String title) {
        setTitle(title, -1);
    }

    public void setTitle(String title, @ColorInt int color) {
        TextView textView = getTitleView();
        textView.setVisibility(View.VISIBLE);
        textView.setText(title);
        textView.setTextColor(color);
    }

    private void addListener() {
        getNavigation().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.finish();
            }
        });
    }

    /**
     * appBar Id
     *
     * @return
     */
    public abstract @LayoutRes
    int getAppBarLayout();

    /**
     * 返回ID
     *
     * @return
     */
    public abstract @IdRes
    int getNavigationId();

    /**
     * 标题Id
     *
     * @return
     */
    public abstract @IdRes
    int getTitleId();

    /**
     * AppBar 创建完成
     *
     * @param view
     */
    protected abstract void onViewCreated(View view);

    public void hideAppBar() {
        getRootView().setVisibility(View.GONE);
    }

    public View getRootView() {
        return mRootView;
    }

    public void hideNavigation() {
        getNavigation().setVisibility(View.INVISIBLE);
    }

}
