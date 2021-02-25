package com.abook23.tv.ui.cache

import android.content.Context
import com.abook23.tv.App
import com.abook23.tv.ben.MovieBen
import com.abook23.tv.dao.CacheVideoBeanDao
import com.android.easy.play.DownloadVideoManager

/**
 * description:    描述
 * author:         yangxiong
 * e-mail:         abook23@163.com
 * createDate:     2020/12/15 9:47
 * updateUser:     更新者：
 * updateDate:     2020/12/15 9:47
 * updateRemark:   更新说明：
 * version:        1.0
 */
class CacheModel {

    fun clearAllCache(context: Context) {
        App.getDaoSession().cacheVideoBeanDao.deleteAll()
        DownloadVideoManager.clearCacheAll(context)//所有电影
    }

    fun removeLocalCache(context: Context, movieBen: MovieBen) {
        val cacheVideoBeans = App.getDaoSession().cacheVideoBeanDao.queryBuilder()
                .where(CacheVideoBeanDao.Properties.V_id.eq(movieBen.v_id)).list()
        cacheVideoBeans.forEach {
            App.getDaoSession().cacheVideoBeanDao.delete(it)
            DownloadVideoManager.delCacheVideo(context, it.url)//某个电影
        }
    }

    fun getCacheMovie(): MutableList<MovieBen> {
        var vids = ""
        val cacheVideoBeanList = App.getDaoSession().cacheVideoBeanDao.loadAll()
        cacheVideoBeanList.forEach {
            vids += it.v_id.toString() + ","
        }
        if (vids.isNotEmpty()) {
            val where = "where _id in (${vids.substring(0, vids.length - 1)}) "
            return App.getDaoSession().movieBenDao.queryRaw(where)
        }
        return ArrayList()
    }
}