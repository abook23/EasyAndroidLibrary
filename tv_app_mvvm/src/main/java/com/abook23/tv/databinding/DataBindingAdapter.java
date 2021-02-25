package com.abook23.tv.databinding;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.abook23.tv.util.RoundedCornersFitStart;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;

import kotlin.Function;

/**
 * description:    描述
 * author:         yangxiong
 * e-mail:         abook23@163.com
 * createDate:     2020/12/1 11:21
 * updateUser:     更新者：
 * updateDate:     2020/12/1 11:21
 * updateRemark:   更新说明：
 * version:        1.0
 */
public class DataBindingAdapter {

    /**
     * 这个是为了 UI 可以设置默认值 好预览UI 界面布局
     *
     * @param textView
     * @param text
     */
    @BindingAdapter(value = {"app:text"})
    public static void text(TextView textView, String text) {
        textView.setText(text);
    }


    /**
     * imageView url 图片加载
     *
     * @param imageView
     * @param url
     */
    @BindingAdapter(value = {"app:image_url"}, requireAll = false)
    public static void imageView(ImageView imageView, Object url) {
        Glide.with(imageView.getContext())
                .load(url)
                .apply(RequestOptions.bitmapTransform(new RoundedCornersFitStart(16))) //圆角半径'
                //.apply { RequestOptions.bitmapTransform(RoundedCornersFitStart());RequestOptions.bitmapTransform(RoundedCorners(16)) } //圆角半径
                .into(imageView);

    }

    /**
     * @param recyclerView
     * @param items        数据
     * @param page         当前是请求第几页数据
     */
    @BindingAdapter(value = {"app:item", "app:page"}, requireAll = false)
    public static void bindAdapterItem(RecyclerView recyclerView, List<?> items, int page) {
        if (items != null) {
            BaseQuickAdapter adapter = (BaseQuickAdapter) recyclerView.getAdapter();
            notifyDataSetChanged(adapter, items, page);
        }
    }

    private static void notifyDataSetChanged(BaseQuickAdapter mAdapter, List<?> list, int page) {
        if (list != null && list.size() > 0) {
            if (page == 0) {
                mAdapter.setNewData(list);
            } else {
                mAdapter.addData(list);
            }
            if (list.size() < 15) {//默认设置每次请求数据共有15条
                mAdapter.loadMoreEnd();
            } else {
                mAdapter.loadMoreComplete();
            }
        } else {
            mAdapter.setNewData(list);
            mAdapter.loadMoreEnd();
        }
    }

}
