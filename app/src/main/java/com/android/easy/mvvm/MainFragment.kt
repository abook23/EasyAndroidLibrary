package com.android.easy.mvvm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

//    private lateinit var viewModel: MainViewModel
//    private lateinit var mainFragmentBinding: MainFragmentBinding
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
//                              savedInstanceState: Bundle?): View? {
//        //view DataBinding 绑定
//        mainFragmentBinding = DataBindingUtil.inflate<MainFragmentBinding>(inflater, R.layout.main_fragment, container, false)
//        //这个是结合 liveData 绑定是使用,原生 liveData可以不用
//        mainFragmentBinding.lifecycleOwner = this
//        return mainFragmentBinding.root
//    }
//
//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        //获取VM
//        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
//        //dataBinding 与liveData结合
//        //xml 绑定 android:text="@{viewModel.user.userName}"  user为VM中的 liveData :val user = MutableLiveData<User>()
//        mainFragmentBinding.viewModel = viewModel
//
//
//
//        //请求数据操作
//        viewModel.loadData()
//    }

}