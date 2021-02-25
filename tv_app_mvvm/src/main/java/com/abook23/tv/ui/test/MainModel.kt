package com.abook23.tv.ui.test

import androidx.lifecycle.MutableLiveData
import com.abook23.tv.App
import com.abook23.tv.URL
import com.abook23.tv.ben.AppConfig
import com.abook23.tv.ben.AppVersion
import com.abook23.tv.ben.ResponseBen
import com.abook23.tv.ben.User
import com.android.easy.app.HttpCall
import com.android.easy.app.mvp.BaseModel
import com.android.easy.base.spf.SharedPreferencesUtils
import com.android.easy.base.util.AndroidUtils

/**
 * description:    描述
 * author:         yangxiong
 * e-mail:         abook23@163.com
 * createDate:     2020/11/30 16:18
 * updateUser:     更新者：
 * updateDate:     2020/11/30 16:18
 * updateRemark:   更新说明：
 * version:        1.0
 */
class MainModel : BaseModel() {

    fun requestAppConfig(appConfigLiveData: MutableLiveData<List<AppConfig>>, appVersionLiveData: MutableLiveData<AppVersion>) {
        val isInitAppData = SharedPreferencesUtils.getParam("initApp", false)
        if (isInitAppData) {
            val data = App.getDaoSession().appConfigDao.loadAll()
            appConfigLiveData.value = data
        }
        get(URL.appConfig, mapOf(), object : HttpCall<ResponseBen<List<AppConfig>>>() {
            override fun onSuccess(t: ResponseBen<List<AppConfig>>) {
                App.getDaoSession().appConfigDao.insertOrReplaceInTx(t.data)
                SharedPreferencesUtils.putParam("initApp", true)
                if (!isInitAppData) {//只做数据库更新，不更新UI
                    appConfigLiveData.value = t.data
                }
            }
        })
        get(URL.appVersion, mapOf(), object : HttpCall<ResponseBen<AppVersion>>() {
            override fun onSuccess(t: ResponseBen<AppVersion>) {
                val appVersion = t.data ?: return
                val versionCode = AndroidUtils.getVersionCode(App.getContext())
                if (versionCode < appVersion.version_code) {
                    appVersionLiveData.value = appVersion
                }
            }
        })
    }

    //模拟数据请求
    fun requestData(): User {
        return User()
    }
}