package com.abook23.tv.ui.movieinfo

import androidx.lifecycle.MutableLiveData
import com.abook23.tv.URL
import com.abook23.tv.ben.MovieBen
import com.abook23.tv.ben.MovieDataBody
import com.abook23.tv.ben.ResponseBen
import com.abook23.tv.util.SeaDataUtils
import com.android.easy.app.mvp.BaseModel
import com.android.easy.retrofit.HttpCall

/**
 * description:    描述
 * author:         yangxiong
 * e-mail:         abook23@163.com
 * createDate:     2020/12/14 11:14
 * updateUser:     更新者：
 * updateDate:     2020/12/14 11:14
 * updateRemark:   更新说明：
 * version:        1.0
 */
class MovieInfoModel : BaseModel() {

    fun requestData(vid: Long, call: Call) {
        get(URL.playData, mapOf("id" to vid), object : HttpCall<ResponseBen<MovieDataBody>>() {
            override fun onSuccess(t: ResponseBen<MovieDataBody>) {
                val list = ArrayList<String>()
                val movieDataBody = t.data
                val urlMap = SeaDataUtils.formatBody(movieDataBody.body)
                urlMap.forEach {
                    if (it.key.contains("m3u8")) {
                        var m3u8url = it.value
                        m3u8url.forEach {
                            list.add(it[1])
                        }
                        call.onSuccess(list)
                        return
                    }
                }
            }
        })
    }

    fun loadRecommendData(tid: Int, movieBenLiveData: MutableLiveData<List<MovieBen>>) {
        get(URL.movieList, mapOf("tid" to tid, "start" to 0, "num" to 8), object : HttpCall<ResponseBen<List<MovieBen>>>() {
            override fun onSuccess(t: ResponseBen<List<MovieBen>>) {
                movieBenLiveData.value = t.data
            }
        })
    }

    interface Call {
        fun onSuccess(urls: List<String>)
    }

}