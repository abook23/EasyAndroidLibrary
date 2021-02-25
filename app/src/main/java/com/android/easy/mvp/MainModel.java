package com.android.easy.mvp;

import com.android.easy.app.mvp.BaseModel;

/**
 * description:    数据打交道的地方:本地数据,网络数据,等逻辑运算处理
 * author:         yangxiong
 * e-mail:         abook23@163.com
 * createDate:     2020/11/26 14:05
 * updateUser:     更新者：
 * updateDate:     2020/11/26 14:05
 * updateRemark:   更新说明：
 * version:        1.0
 */
public class MainModel extends BaseModel {

    public boolean toLogin(String userName,String password){
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return userName.equals(password);
    }

}
