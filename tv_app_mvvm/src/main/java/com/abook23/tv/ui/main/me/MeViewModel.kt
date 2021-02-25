package com.abook23.tv.ui.main.me

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.abook23.tv.R
import com.abook23.tv.ben.MovieBen

class MeViewModel : ViewModel() {
    val model by lazy {
        MeModel()
    }
    val typeArray = arrayListOf<Array<Any>>(
            arrayOf("观看历史", R.mipmap.icon_history),
            arrayOf("我的收藏", R.mipmap.icon_collect),
            arrayOf("离线下载", R.mipmap.icon_cache),
            arrayOf("缓存清理", R.mipmap.icon_clear_cache)
    )

    val listHistoryData = MutableLiveData<List<MovieBen>>()

    fun getMovieHistoryList() {
        listHistoryData.value = model.getMovieHistory();
    }
}