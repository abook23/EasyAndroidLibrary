package com.abook23.tv.ui.main.movie

import androidx.lifecycle.MutableLiveData
import com.abook23.tv.URL
import com.abook23.tv.ben.ResponseBen
import com.abook23.tv.ben.TypeMovie
import com.android.easy.app.HttpCall
import com.android.easy.app.mvp.BaseModel

/**
 * description:    描述
 * author:         yangxiong
 * e-mail:         abook23@163.com
 * createDate:     2020/12/1 15:46
 * updateUser:     更新者：
 * updateDate:     2020/12/1 15:46
 * updateRemark:   更新说明：
 * version:        1.0
 */
class MovieModel : BaseModel() {
    fun getListType(id: Long, movieListLiveData: MutableLiveData<List<TypeMovie>>) {
        get(URL.movieListType, mapOf("type" to id), object : HttpCall<ResponseBen<List<TypeMovie>>>() {
            override fun onSuccess(t: ResponseBen<List<TypeMovie>>) {
                movieListLiveData.value = t.data
            }
        })
    }
}