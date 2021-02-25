package com.abook23.tv.ui.movieinfo

import android.graphics.drawable.Drawable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import com.abook23.tv.R
import com.abook23.tv.ben.MovieBen
import com.abook23.tv.ben.PlayData
import com.android.easy.play.MovieInfo

/**
 * description:    描述
 * author:         yangxiong
 * e-mail:         abook23@163.com
 * createDate:     2020/12/14 11:24
 * updateUser:     更新者：
 * updateDate:     2020/12/14 11:24
 * updateRemark:   更新说明：
 * version:        1.0
 */
class MovieInfoViewModel : ViewModel() {
    var todLiveData = MutableLiveData<List<String>>()
    var movieBenLiveData = MutableLiveData<List<MovieBen>>()
    var collect = MutableLiveData<Boolean>(false)
    var collectRes = collect.map {
        if (it){
            R.mipmap.icon_collect1
        }else{
            R.mipmap.icon_collect
        }
    }
    private val model by lazy {
        MovieInfoModel()
    }

    fun requestMovieInfoData(vid: Long) {
        model.requestData(vid, object : MovieInfoModel.Call {
            override fun onSuccess(urls: List<String>) {
                todLiveData.value = urls
            }
        })
    }

    fun loadRecommendData(tid: Int) {
        model.loadRecommendData(tid,movieBenLiveData)
    }

    fun getPlayData(movieBen:MovieBen,url: String): PlayData {
        val list = ArrayList<MovieInfo>()
        val urls = todLiveData.value
        for (index in urls?.indices!!) {
            list.add(MovieInfo("第${index+1}集", urls[index], index + 1))
        }
        return PlayData(movieBen.v_id, movieBen.v_name, url, list)
    }
}