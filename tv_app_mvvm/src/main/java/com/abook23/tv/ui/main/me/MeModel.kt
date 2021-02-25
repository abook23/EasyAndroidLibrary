package com.abook23.tv.ui.main.me

import com.abook23.tv.App
import com.abook23.tv.ben.MovieBen
import com.abook23.tv.dao.MovieBenDao
import java.util.*

/**
 * description:    描述
 * author:         yangxiong
 * e-mail:         abook23@163.com
 * createDate:     2020/12/14 16:30
 * updateUser:     更新者：
 * updateDate:     2020/12/14 16:30
 * updateRemark:   更新说明：
 * version:        1.0
 */
class MeModel {
    fun getMovieHistory(): List<MovieBen> {
        val list  =App.getDaoSession().movieBenDao.queryBuilder()
        .where(MovieBenDao.Properties.IsPlay.eq(true)).limit(10).list()
        list.reverse()
        return list
    }
}