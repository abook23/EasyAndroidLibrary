package com.android.easy.app.base;

import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.easy.app.HttpCall;
import com.android.easy.app.R;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author abook23@163.com
 * @date 2020/04/02
 */
public abstract class BaseAppCompatListActivity<T, M> extends BaseAppCompatActivity {

    public BaseQuickAdapter mBaseQuickAdapter;
    public RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Map<String, Object> params = new HashMap<>();
    private int page = 1;


    //重新加载
    public void onRefreshParam(Map<String, Object> params, int page) {
        params.put("page", page);
    }

    //加载更多,下一页
    public void onLoadMoreRequestedParam(Map<String, Object> params, int page) {
        params.put("page", page);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.easy_app_layout_list);
        mRecyclerView = findViewById(R.id.recyclerView);
        mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        mRecyclerView.setAdapter(mBaseQuickAdapter = new Adapter(getItemLayout()));
        mRecyclerView.setLayoutManager(getLayoutManager());

        setParams(params);
        onListener();
    }

    private void onListener() {
        mBaseQuickAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                onLoadMoreRequestedParam(params, page);
                onRequestData(params);
            }
        }, mRecyclerView);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                onRefreshParam(params, page);
                onRequestData(params);
            }
        });
    }

    private void onRequestData(final Map<String, Object> params) {
        get(getURL(), params, true, new HttpCall<T>() {
            @Override
            public void onSuccess(@NonNull T t) {
                List<M> list = onResponseData(t);
                if (list.size() > 0) {
                    if (page == 1) {
                        mBaseQuickAdapter.setNewData(list);
                    } else {
                        mBaseQuickAdapter.addData(list);
                    }
                    mBaseQuickAdapter.loadMoreComplete();
                    page++;
                } else {
                    mBaseQuickAdapter.loadMoreEnd();
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                mBaseQuickAdapter.loadMoreFail();
            }
        });
    }

    public RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
    }

    public abstract @LayoutRes
    int getItemLayout();

    public abstract String getURL();

    public abstract void setParams(Map<String, Object> params);

    public abstract @NonNull
    List<M> onResponseData(T t);

    public abstract void onBaseQuickAdapterConvert(BaseViewHolder helper, M item);


    public class Adapter extends BaseQuickAdapter<M, BaseViewHolder> {

        public Adapter(int layoutResId) {
            super(layoutResId);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder helper, M item) {
            onBaseQuickAdapterConvert(helper, item);
        }
    }

}
