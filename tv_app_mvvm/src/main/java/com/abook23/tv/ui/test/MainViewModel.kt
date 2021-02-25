package com.abook23.tv.ui.test

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.abook23.tv.ben.AppConfig
import com.abook23.tv.ben.AppVersion
import com.abook23.tv.ben.User

class MainViewModel : ViewModel() {
    val user = MutableLiveData<User>()
    var appConfigLiveData = MutableLiveData<List<AppConfig>>()
    var appVersionLiveData = MutableLiveData<AppVersion>()

    private val model by lazy {
        MainModel()
    }

    fun initApp(){
        model.requestAppConfig(appConfigLiveData,appVersionLiveData)
    }

    fun loadData() {
        user.value = model.requestData()
    }
}