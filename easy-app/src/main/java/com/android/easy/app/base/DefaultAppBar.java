package com.android.easy.app.base;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;

import com.android.easy.app.R;

public class DefaultAppBar extends BaseAppBar {

    private AppBarViewHolder mAppBarViewHolder;

    public AppBarViewHolder getAppBarViewHolder() {
        return mAppBarViewHolder;
    }

    public DefaultAppBar(ViewGroup viewGroup) {
        super(viewGroup);
        mAppBarViewHolder = new AppBarViewHolder(getRootView());
        getNavigation().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnAppBarListener.onNavigationClick(v);
            }
        });
    }

    public void setTitle(String title) {
        setTitle(title, -1);
    }

    @Override
    public void setTitle(String title, @ColorInt int color) {
        mAppBarViewHolder.mTitle.setVisibility(View.VISIBLE);
        mAppBarViewHolder.mTitle.setText(title);
        mAppBarViewHolder.mTitle.setTextColor(color);
    }

    @Override
    public ImageView getNavigation() {
        return mAppBarViewHolder.mBack;
    }

    /**
     * 搜索
     *
     * @param b
     */
    public void showBarSearch(boolean b) {
        mAppBarViewHolder.mTitle.setVisibility(b ? View.GONE : View.VISIBLE);
        mAppBarViewHolder.mEditText.setVisibility(b ? View.VISIBLE : View.GONE);
        mAppBarViewHolder.mButton.setVisibility(b ? View.VISIBLE : View.GONE);
    }

    public String getBarSearch() {
        return mAppBarViewHolder.mEditText.getText().toString();
    }

    /**
     * 添加自定义View
     *
     * @param view
     */
    public void addBarView(View view) {
        mAppBarViewHolder.mLinearLayout.addView(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnAppBarListener.onAppBarClick(v);
            }
        });
    }

    public void removeBarView(View view) {
        mAppBarViewHolder.mLinearLayout.removeView(view);
    }


    @Override
    protected View onCreateView(ViewGroup viewGroup) {
        return LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.easy_app_layout_default_app_bar, viewGroup, false);
    }

    public class AppBarViewHolder implements View.OnClickListener {

        public TextView mTitle;
        public EditText mEditText;
        public TextView mButton;
        public LinearLayout mLinearLayout;
        public ImageView mBack;

        AppBarViewHolder(View v) {
            mBack = v.findViewById(R.id.toolbar_back);
            mTitle = v.findViewById(R.id.toolbar_title);
            mEditText = v.findViewById(R.id.toolbar_edit);
            mButton = v.findViewById(R.id.toolbar_right_button);
            mLinearLayout = v.findViewById(R.id.toolbar_right_layout);

            mButton.setOnClickListener(this);
            mBack.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnAppBarListener != null) {
                mOnAppBarListener.onAppBarClick(v);
            }
            if (v.getId() == mBack.getId()) {
                mOnAppBarListener.onNavigationClick(v);
            }
        }
    }
}
