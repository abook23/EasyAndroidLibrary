package com.android.easy.app.base;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.easy.retrofit.HttpCall;
import com.android.easy.app.R;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author abook23@163.com
 */
public abstract class BaseAppCompatListActivity<M, T> extends BaseAppCompatActivity {

    public BaseQuickAdapter<T, BaseViewHolder> mBaseQuickAdapter;
    public RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Map<String, Object> params = new HashMap<>();
    private int mPage = 0;
    public int mPageSize = 15;

    public abstract @LayoutRes
    int getItemLayout();

    public abstract String getApiUrl();

    public abstract void setParams(@NonNull Map<String, Object> params);

    public abstract List<T> onResponseData(M m);

    public abstract void onBaseQuickAdapterConvert(@NonNull BaseViewHolder helper, @NonNull T item);

    public void onAdapterViewDetachedFromWindow(@NonNull BaseViewHolder holder){

    }

    /**
     * 加载数据
     */
    public void loadRequestData() {
        onRefreshParam();
        onRequestData(params);
    }

    //重新加载
    public void onRefreshParam() {
        mPage = 0;
        params.put("page", mPage);
    }

    //加载更多,下一页
    public void onLoadMoreRequestedParam() {
        params.put("page", mPage);
        params.put("pageSize", mPageSize);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ContentViewLayout layout = getContentViewLayout();
        setContentView(layout.getLayoutResId());
        mRecyclerView = findViewById(layout.getRecyclerViewId());
        mSwipeRefreshLayout = findViewById(layout.getSwipeRefreshLayoutId());
        mRecyclerView.setAdapter(mBaseQuickAdapter = initAdapter());
        mRecyclerView.setLayoutManager(getLayoutManager());
        setEmptyView(null);
        setParams(params);
        onListener();
    }

    public ContentViewLayout getContentViewLayout() {
        return new ContentViewLayout() {
            @Override
            public int getLayoutResId() {
                return R.layout.easy_app_layout_list;
            }

            @Override
            public int getRecyclerViewId() {
                return R.id.recyclerView;
            }

            @Override
            public int getSwipeRefreshLayoutId() {
                return R.id.swipeRefreshLayout;
            }
        };

    }

    public BaseQuickAdapter<T,BaseViewHolder> initAdapter(){
        return new Adapter(getItemLayout());
    }

    public void setEmptyView(View view) {
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.easy_app_empty_view, mRecyclerView, false);
        }
        mBaseQuickAdapter.setEmptyView(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRefreshParam();
            }
        });
    }

    public RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadRequestData();
    }

    private void onListener() {
        mBaseQuickAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                onLoadMoreRequestedParam();
                onRequestData(params);
            }
        }, mRecyclerView);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onRefreshParam();
                onRequestData(params);
            }
        });
    }

    private void onRequestData(final Map<String, Object> params) {
        if (TextUtils.isEmpty(getApiUrl())) {
            List<T> list = onResponseData(null);
            notifyDataSetChanged(list);
        } else {
            get(getApiUrl(), params, true, new HttpCall<M>() {
                @Override
                public void onSuccess(@NonNull M m) {
                    List<T> list = onResponseData(m);
                    notifyDataSetChanged(list);
                }

                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                    mBaseQuickAdapter.loadMoreFail();
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });
        }
    }

    private void notifyDataSetChanged(List<T> list) {
        if (mPage == 0) {
            mBaseQuickAdapter.setNewData(list);
        } else {
            mBaseQuickAdapter.addData(list);
        }
        if (list != null && list.size() > 0) {
            if (list.size() < mPageSize) {
                mBaseQuickAdapter.loadMoreEnd();
            } else {
                mBaseQuickAdapter.loadMoreComplete();
            }
            mPage++;
        } else {
            mBaseQuickAdapter.loadMoreEnd();
        }
        mSwipeRefreshLayout.setRefreshing(false);
    }

    public List<T> getListData() {
        return mBaseQuickAdapter.getData();
    }

    public void notifyDataSetChanged() {
        mBaseQuickAdapter.notifyDataSetChanged();
    }

    public class Adapter extends BaseQuickAdapter<T, BaseViewHolder> {

        public Adapter(int layoutResId) {
            super(layoutResId);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder helper, T item) {
            onBaseQuickAdapterConvert(helper, item);
        }

        @Override
        public void onViewDetachedFromWindow(@NonNull BaseViewHolder holder) {
            super.onViewDetachedFromWindow(holder);
            onAdapterViewDetachedFromWindow(holder);
        }
    }

    public interface ContentViewLayout {
        @LayoutRes
        int getLayoutResId();

        @IdRes
        int getRecyclerViewId();

        @IdRes
        int getSwipeRefreshLayoutId();
    }

}
