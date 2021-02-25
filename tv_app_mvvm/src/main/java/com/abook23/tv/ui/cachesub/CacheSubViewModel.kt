package com.abook23.tv.ui.cachesub

import android.content.Context
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.abook23.tv.App
import com.abook23.tv.ben.CacheVideoBean
import com.abook23.tv.ben.MovieBen
import com.abook23.tv.ben.PlayData
import com.android.easy.play.MovieInfo
import java.util.*
import kotlin.collections.HashSet

/**
 * description:    描述
 * author:         yangxiong
 * e-mail:         abook23@163.com
 * createDate:     2020/12/15 9:42
 * updateUser:     更新者：
 * updateDate:     2020/12/15 9:42
 * updateRemark:   更新说明：
 * version:        1.0
 */
class CacheSubViewModel : ViewModel() {
    var checkSet = HashSet<CacheVideoBean>()
    var checkButtonVisibility = MutableLiveData(false)
    var checked = MutableLiveData(false)
    private val model by lazy {
        CacheSubModel()
    }

    fun clearAllCacheMovie(context: Context, movieBen: MovieBen) {
        model.clearAllCache(context, movieBen)
    }

    fun removeCacheMovie(mContext: Context) {
        checkSet.forEach {
            model.removeLocalCache(mContext, it)
        }
    }


    fun getCacheListData(v_id: Long): MutableList<CacheVideoBean> {
        return model.getCacheMovie(v_id)
    }

    fun getRate(progress: Float, max: Float): String {
        if (max > 0) {
            return String.format("已缓存%.2f%s", progress / max * 100, "%")
        }
        return "0.00%"
    }

    fun formatterDownloadStatus(item: CacheVideoBean): String {
        var text = ""
        if (item.download_complete) {
            text = "完成"
        } else if (item.download_pause) {
            text = "开始"
        } else {
            text = "暂停"
        }
        return text
    }

    fun getRate2f(value: String?): String {
        if (value==null) {
            return "0.00%"
        }
        return String.format("%.2f%s", value.toFloat(), "%")

    }

    fun getPlayData(movieBen: MovieBen, item: CacheVideoBean): PlayData {
        val list = ArrayList<MovieInfo>()
        val listData = getCacheListData(movieBen.v_id)
        listData.forEach {
            val movieInfo = MovieInfo("第${it.v_num+1}集", it.url, it.v_num + 1)
            list.add(movieInfo)
        }
        return PlayData(movieBen.v_id, movieBen.v_name + " 第${item.v_num + 1}集", item.url, list)
    }

    fun downloadPause(view: View, item: CacheVideoBean) {
        if (item.download_complete){
            return
        }
        App.getCacheVideoService()?.pause(item.url)
    }

}