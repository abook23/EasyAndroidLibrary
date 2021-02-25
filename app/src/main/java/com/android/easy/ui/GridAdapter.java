package com.android.easy.ui;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.easy.R;
import com.android.easy.ui.dialog.DialogActivity;

/**
 * Description: 描述
 * Author: yangxiong
 * E-mail: abook23@163.com
 * CreateDate: 2020/7/28 15:01
 * UpdateUser: 更新者：
 * UpdateDate: 2020/7/28 15:01
 * UpdateRemark: 更新说明：
 * Version: 1.0
 */
public class GridAdapter extends RecyclerView.Adapter<GridAdapter.MyViewHolder> {
    Object[][] data;
    AdapterView.OnItemClickListener mOnItemClickListener;

    public GridAdapter(Object[][] data) {
        this.data = data;
    }

    public void addItemClick(AdapterView.OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(View.inflate(parent.getContext(), R.layout.item_main, null));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.mTextView.setText((String) data[position][0]);
        if (mOnItemClickListener != null)
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(null, holder.itemView, position, position);
                }
            });
    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView mTextView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.text);
        }
    }
}
