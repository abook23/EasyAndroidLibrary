package com.abook23.tv.ui.search

import androidx.lifecycle.MutableLiveData
import com.abook23.tv.App
import com.abook23.tv.URL
import com.abook23.tv.ben.MovieBen
import com.abook23.tv.ben.ResponseBen
import com.abook23.tv.dao.MovieBenDao
import com.android.easy.app.mvp.BaseModel
import com.android.easy.retrofit.HttpCall

/**
 * description:    描述
 * author:         yangxiong
 * e-mail:         abook23@163.com
 * createDate:     2020/12/3 10:54
 * updateUser:     更新者：
 * updateDate:     2020/12/3 10:54
 * updateRemark:   更新说明：
 * version:        1.0
 */
class SearchModel : BaseModel() {

    fun requestNetworkData(page: Int, tid: Int, searchVal: String, call: HttpCall<ResponseBen<List<MovieBen>>>) {
        get(URL.movieList, mapOf("tid" to tid, "name" to searchVal, "start" to page * pageSize, "num" to pageSize), call)
    }

    fun loadLocalData(state: Int, searchVal: String, searchLiveData: MutableLiveData<List<MovieBen>>) {
        var queryBuilder = App.getDaoSession().movieBenDao.queryBuilder()
        queryBuilder.where(MovieBenDao.Properties.V_name.like("%$searchVal%"))
        if (state == 1) {
            queryBuilder.where(MovieBenDao.Properties.IsPlay.eq(true))
        }
        if (state == 2) {
            queryBuilder.where(MovieBenDao.Properties.IsCollect.eq(true))
        }
        val data = queryBuilder.list()
        data.reverse()
        searchLiveData.value = data
    }
}