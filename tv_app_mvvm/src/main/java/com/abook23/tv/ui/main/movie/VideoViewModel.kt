package com.abook23.tv.ui.main.movie

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.abook23.tv.ben.TypeMovie

/**
 * description:    描述
 * author:         yangxiong
 * e-mail:         abook23@163.com
 * createDate:     2020/12/1 15:13
 * updateUser:     更新者：
 * updateDate:     2020/12/1 15:13
 * updateRemark:   更新说明：
 * version:        1.0
 */
class VideoViewModel : ViewModel() {
    var movieListLiveData = MutableLiveData<List<TypeMovie>>()
    private val model by lazy {
        MovieModel()
    }

    fun loadMovieList(type: Long) {
        model.getListType(type,movieListLiveData)
    }
}