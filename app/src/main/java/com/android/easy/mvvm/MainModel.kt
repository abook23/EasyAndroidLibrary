package com.android.easy.mvvm

import com.android.easy.app.mvp.BaseModel
import com.android.easy.data.bean.UserInfo

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
class MainModel:BaseModel() {

    //模拟数据请求
    fun requestData():UserInfo{
        return UserInfo()
    }
}