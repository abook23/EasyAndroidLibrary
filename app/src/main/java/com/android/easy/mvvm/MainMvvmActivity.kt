package com.android.easy.mvvm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.android.easy.R
import com.android.easy.databinding.ActivityMainMvvmBinding

class MainMvvmActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_mvvm)
        //view DataBinding 绑定
        val activityMainMvvmBinding: ActivityMainMvvmBinding = DataBindingUtil.setContentView(this, R.layout.activity_main_mvvm)
        //这个是结合 liveData 绑定是使用,原生 liveData可以不用
        activityMainMvvmBinding.lifecycleOwner = this
        //获取VM
        val viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        //dataBinding 与liveData结合
        //xml 绑定 android:text="@{viewModel.user.userName}"  user为VM中的 liveData :val user = MutableLiveData<UserInfo>()
        activityMainMvvmBinding.viewModel = viewModel



        //请求数据操作
        viewModel.loadData()
    }
}