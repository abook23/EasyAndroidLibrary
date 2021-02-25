package com.abook23.tv.ui.cache

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.abook23.tv.App
import com.abook23.tv.ben.MovieBen
import com.abook23.tv.dao.CacheVideoBeanDao

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
class CacheViewModel : ViewModel() {
    var checkSet = HashSet<MovieBen>()
    var checkButtonVisibility = MutableLiveData(false)
    var checked = MutableLiveData(false)
    private val model by lazy {
        CacheModel()
    }

    fun clearAllCacheMovie(context: Context){
        model.clearAllCache(context)
    }

    fun removeCacheMovie(mContext: Context) {
        checkSet.forEach {
            model.removeLocalCache(mContext,it)
        }
    }


    fun getCacheListData():MutableList<MovieBen>{
       return model.getCacheMovie()
    }

}