package com.android.easy.app.base;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.easy.retrofit.HttpCall;
import com.android.easy.app.R;
import com.android.easy.retrofit.listener.Call;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

public abstract class BaseListFragment<M, T, K extends BaseViewHolder> extends BaseFragment {

    public RecyclerView mRecyclerView;
    public Adapter mAdapter;
    public int mPage = 1;
    public int mPageSize = 15;
    public Map<String, Object> params = new HashMap<>();

    protected abstract @LayoutRes
    int getItemLayout();

    protected abstract String getApiUrl();

    protected abstract void setParams(@NonNull Map<String, Object> params);

    protected abstract List<T> onResponseData(M resData);

    public abstract void onBaseQuickAdapterConvert(K helper, T item);

    @Override
    protected int getLayoutId() {
        return R.layout.easy_app_fragment_list;
    }

    @Override
    protected void initView(@NonNull View rootView) {
        if (rootView instanceof RecyclerView) {
            mRecyclerView = (RecyclerView) rootView;
            mRecyclerView.setLayoutManager(getLayoutManager());
            mRecyclerView.setAdapter(mAdapter = new Adapter(getItemLayout()));
            setEmptyView(null);
            onListener();
        }
    }

    public void setEmptyView(View view) {
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.easy_app_empty_view, mRecyclerView, false);
        }
        mAdapter.setEmptyView(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRefreshParam();
            }
        });
    }

    public RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
    }

    private void onListener() {
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                onLoadMoreRequestedParam();
                onRequestData(params);
            }
        }, mRecyclerView);
        mAdapter.setUpFetchListener(new BaseQuickAdapter.UpFetchListener() {
            @Override
            public void onUpFetch() {
                onRefreshParam();
                onRequestData(params);
            }
        });
    }

    @Override
    protected void onOneLoadData() {
        onLoadMoreRequestedParam();
        setParams(params);
        onRequestData(params);
    }

    @Override
    protected void onVisibleLoadData() {

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
        mPage = 1;
        params.put("page", mPage);
    }

    //加载更多,下一页
    public void onLoadMoreRequestedParam() {
        params.put("page", mPage);
        params.put("pageSize", mPageSize);
    }

    public List<T> getListData() {
        return mAdapter.getData();
    }

    public void notifyDataSetChanged() {
        mAdapter.notifyDataSetChanged();
    }

    private void onRequestData(Map<String, Object> params) {
        if (TextUtils.isEmpty(getApiUrl())) {
            List<T> list = onResponseData(null);
            notifyDataSetChanged(list);
        } else {
            get(getApiUrl(), params, true, new HttpCall<String>() {
                @Override
                public void onSuccess(@NonNull String s) {
                    try {
                        M result = formJsonData(s);
                        List<T> list = onResponseData(result);
                        notifyDataSetChanged(list);
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                        System.out.println("返回JSON格式 和 解析实体不对应");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                    mAdapter.loadMoreFail();
                }
            });
        }
    }

    private void notifyDataSetChanged(List<T> list) {
        if (list != null && list.size() > 0) {
            if (mPage == 1) {
                mAdapter.setNewData(list);
            } else {
                mAdapter.addData(list);
            }
            if (list.size() < mPageSize) {
                mAdapter.loadMoreEnd();
            } else {
                mAdapter.loadMoreComplete();
            }
            mPage++;
        } else {
            mAdapter.loadMoreEnd();
        }
//        mSwipeRefreshLayout.setRefreshing(false);
    }

    private <T> T formJsonData(String jsonStr) throws JsonSyntaxException {
        Gson gson = new Gson();
        Type type = getClass().getGenericSuperclass();
        ParameterizedType parameterizedType = (ParameterizedType) type;
        Type[] types = parameterizedType.getActualTypeArguments();
        if (types[0] instanceof ParameterizedType) {
            Type rawType = ((ParameterizedType) types[0]).getRawType();
            Type[] type2 = ((ParameterizedType) types[0]).getActualTypeArguments();
            Type ty = new Call.ParameterizedTypeImpl((Class) rawType, type2, null);
            return gson.fromJson(jsonStr, ty);
        } else {
            return gson.fromJson(jsonStr, types[0]);
        }
    }

    public class Adapter extends BaseQuickAdapter<T, K> {

        public Adapter(int layoutResId) {
            super(layoutResId);
        }

        @Override
        protected void convert(@NonNull K helper, T item) {
            onBaseQuickAdapterConvert(helper, item);
        }
    }


}
