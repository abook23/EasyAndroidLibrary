package com.abook23.tv.ui.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.abook23.tv.ben.MovieBen
import com.abook23.tv.ben.ResponseBen
import com.android.easy.retrofit.HttpCall

/**
 * description:    描述
 * author:         yangxiong
 * e-mail:         abook23@163.com
 * createDate:     2020/12/3 10:37
 * updateUser:     更新者：
 * updateDate:     2020/12/3 10:37
 * updateRemark:   更新说明：
 * version:        1.0
 */
class SearchViewModel : ViewModel() {
    var item = MutableLiveData<List<MovieBen>>()
    var clearViewVisibility = MutableLiveData<Boolean>(false)
    var mPage = 0
    private val model by lazy {
        SearchModel()
    }

    fun getPage():Int{
        return mPage-1
    }

    fun requestNetworkData(page: Int, tid: Int, searchVal: String) {
        mPage= page
        model.requestNetworkData(page, tid, searchVal, object : HttpCall<ResponseBen<List<MovieBen>>>() {
            override fun onSuccess(t: ResponseBen<List<MovieBen>>) {
                item.value = t.data//会自动更新  @BindingAdapter(value = {"binding_item"
                mPage++
            }

            override fun onError(e: Throwable) {
                super.onError(e)
                //通过 IView 做一些错误提示
            }
        })
    }

    fun requestLocalData(state: Int, searchVal: String){
        mPage = 0
        model.loadLocalData(state,searchVal,item)
    }

}