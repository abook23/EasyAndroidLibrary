package com.android.easy.mvvm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.easy.data.bean.UserInfo

class MainViewModel : ViewModel() {
    val user = MutableLiveData<UserInfo>()

    private val model by lazy {
        MainModel()
    }

    fun loadData() {
        user.value = model.requestData()
    }
}