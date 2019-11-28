package com.android.easy.base.tabhost;

import android.content.Context;
import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import com.android.easy.base.adapter.base.BaseViewHolder;


/**
 * Created by My on 2017/9/20.
 */

public abstract class TabHostViewPagerAdapter<T extends TabHostViewPage.TabHostBean> {
    private int mResource;
    private Context mContext;

    public TabHostViewPagerAdapter(Context context, @LayoutRes int tableHostItemRes) {
        mResource = tableHostItemRes;
        mContext = context;
    }

    public View getView(int position, T t) {
        return createViewFromResource(position,t, mResource);
    }

    private View createViewFromResource(int position, T t, int resource) {
        View view = View.inflate(mContext, resource, null);
        BaseViewHolder viewHolder = new BaseViewHolder(view);
        convert(viewHolder, position, t);
        return view;
    }

    public abstract void convert(@NonNull BaseViewHolder holder, int position, @NonNull T item);
}