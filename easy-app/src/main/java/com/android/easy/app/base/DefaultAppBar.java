package com.android.easy.app.base;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;

import com.android.easy.app.R;

public class DefaultAppBar extends BaseAppBar {

    private EditText mEditText;
    private TextView appbar_right_TextView;
    private ImageView appbar_right_ImageView;
    private LinearLayout appbar_right_layout;

    public DefaultAppBar(Activity activity, ViewGroup viewGroup) {
        super(activity, viewGroup);
    }

    @Override
    public int getAppBarLayout() {
        return R.layout.easy_app_layout_default_app_bar;
    }

    @Override
    public int getNavigationId() {
        return R.id.appbar_back;
    }

    @Override
    public int getTitleId() {
        return R.id.appbar_title;
    }

    @Override
    protected void onViewCreated(View v) {
        mEditText = v.findViewById(R.id.appbar_edit);
        appbar_right_TextView = v.findViewById(R.id.appbar_right_text_view);
        appbar_right_ImageView = v.findViewById(R.id.appbar_right_image_view);
        appbar_right_layout = v.findViewById(R.id.appbar_right_layout);

        hideView(mEditText, appbar_right_TextView, appbar_right_ImageView);
    }

    private void hideView(View... views) {
        for (View view : views) {
            view.setVisibility(View.GONE);
        }
    }

    public void setAppbarRightView(String text, View.OnClickListener onClickListener) {
        appbar_right_TextView.setVisibility(View.VISIBLE);
        appbar_right_TextView.setText(text);
        appbar_right_TextView.setOnClickListener(onClickListener);
    }

    public void setAppbarRightView(@DrawableRes int resId, View.OnClickListener onClickListener) {
        appbar_right_ImageView.setVisibility(View.VISIBLE);
        appbar_right_ImageView.setImageResource(resId);
        appbar_right_ImageView.setOnClickListener(onClickListener);
    }

    public void setAppbarRightView(String text, @ColorInt int color, View.OnClickListener onClickListener) {
        appbar_right_TextView.setText(text);
        appbar_right_TextView.setTextColor(color);
        appbar_right_TextView.setOnClickListener(onClickListener);
    }


    /**
     * 搜索
     *
     * @param b
     */
    public void showBarSearch(boolean b) {
        getTitleView().setVisibility(b ? View.GONE : View.VISIBLE);
        mEditText.setVisibility(b ? View.VISIBLE : View.GONE);
    }

    public String getSearchView() {
        return mEditText.getText().toString();
    }

    /**
     * 添加自定义View
     *
     * @param view
     */
    public void addAppBarRightView(View view) {
        appbar_right_layout.addView(view);
    }
}
