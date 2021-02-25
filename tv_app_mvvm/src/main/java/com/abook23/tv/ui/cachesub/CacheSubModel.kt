package com.abook23.tv.ui.cachesub

import android.content.Context
import com.abook23.tv.App
import com.abook23.tv.ben.CacheVideoBean
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
class CacheSubModel {

    fun clearAllCache(context: Context,movieBen: MovieBen) {
        val cacheVideoBeans = App.getDaoSession().cacheVideoBeanDao.queryBuilder()
                .where(CacheVideoBeanDao.Properties.V_id.eq(movieBen.v_id)).list()
        cacheVideoBeans.forEach {
            removeLocalCache(context,it)
        }
    }

    fun removeLocalCache(context: Context, cacheVideoBean: CacheVideoBean) {
        App.getDaoSession().cacheVideoBeanDao.delete(cacheVideoBean)
        DownloadVideoManager.delCacheVideo(context, cacheVideoBean.url)//某个电影
    }

    fun getCacheMovie(v_id:Long): MutableList<CacheVideoBean> {
                return App.getDaoSession().cacheVideoBeanDao.queryBuilder()
                .where(CacheVideoBeanDao.Properties.V_id.eq(v_id))
                .list()
    }
}