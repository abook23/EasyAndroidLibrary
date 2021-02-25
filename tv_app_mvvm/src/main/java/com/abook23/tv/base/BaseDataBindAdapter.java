package com.abook23.tv.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * description:    描述
 * author:         yangxiong
 * e-mail:         abook23@163.com
 * createDate:     2020/12/2 14:47
 * updateUser:     更新者：
 * updateDate:     2020/12/2 14:47
 * updateRemark:   更新说明：
 * version:        1.0
 */
public abstract class BaseDataBindAdapter<M, B extends ViewDataBinding> extends RecyclerView.Adapter<BaseDataBindAdapter.BaseBindDataViewHolder> {
//    public Context mContext;
    private int itemLayoutId;
    private List<M> mDataList = new ArrayList<>();
    private OnItemClickListener mOnItemClickListener;

    public BaseDataBindAdapter(int itemLayoutId) {
        this.itemLayoutId = itemLayoutId;
    }

    public void setNewData(List<M> dataList) {
        mDataList = dataList;
        notifyDataSetChanged();
    }

    public void addData(List<M> dataList) {
        mDataList.addAll(dataList);
        notifyDataSetChanged();
    }

    public M getItem(int position) {
        return mDataList.get(position);
    }

    public void clear() {
        mDataList.clear();
        notifyDataSetChanged();
    }

    private int getItemLayoutId() {
        return itemLayoutId;
    }


    public void setOnItemClickListener(@Nullable OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    @NonNull
    @Override
    public BaseBindDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context mContext = parent.getContext();
        View rootView = LayoutInflater.from(mContext).inflate(getItemLayoutId(), parent, false);
        BaseBindDataViewHolder viewHolder = new BaseBindDataViewHolder(rootView);
        bindViewClickListener(viewHolder);
        return viewHolder;
    }

    private void bindViewClickListener(BaseBindDataViewHolder holder) {
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(BaseDataBindAdapter.this, v, holder.getAdapterPosition());
                }
            });
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseBindDataViewHolder holder, int position) {
        B binding = DataBindingUtil.bind(holder.itemView);
        onBindItem(binding, mDataList.get(position));
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }


    public abstract void onBindItem(B binding, M item);

    static class BaseBindDataViewHolder extends RecyclerView.ViewHolder {
        public BaseBindDataViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(BaseDataBindAdapter adapter, View view, int position);
    }
}
