package com.abook23.tv.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

/**
 * description:    描述
 * author:         yangxiong
 * e-mail:         abook23@163.com
 * createDate:     2020/12/1 16:13
 * updateUser:     更新者：
 * updateDate:     2020/12/1 16:13
 * updateRemark:   更新说明：
 * version:        1.0
 */
class BindDataAdapter<T, B : ViewDataBinding>(val itemRes: Int, val brId: Int) : RecyclerView.Adapter<BindDataAdapter.BindDataViewHolder<B>>() {
    private var dataList: ArrayList<T> = ArrayList()
    fun setData(data: List<T>) {
        dataList = data as ArrayList<T>
        notifyDataSetChanged()
    }

    fun addData(data: List<T>) {
        dataList.addAll(data)
        notifyDataSetChanged()
    }

    fun clear() {
        dataList.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindDataViewHolder<B> {
        val view = LayoutInflater.from(parent.context).inflate(itemRes, parent, false)
        val bindDataViewHolder = BindDataViewHolder<B>(view)
        return bindDataViewHolder
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: BindDataViewHolder<B>, position: Int) {
        holder.binding.setVariable(brId, dataList[position])
//        convert(holder.binding, dataList[position])
        //立即执行绑定
        holder.binding.executePendingBindings()
    }

//    protected abstract fun convert(@NonNull binding: B, item: T)

    class BindDataViewHolder<B : ViewDataBinding>(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding: B = DataBindingUtil.bind(itemView)!!
    }

}