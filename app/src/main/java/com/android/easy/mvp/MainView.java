package com.android.easy.mvp;

import com.android.easy.app.mvp.BaseView;

/**
 * description:    操作 View 的接口
 * author:         yangxiong
 * e-mail:         abook23@163.com
 * createDate:     2020/11/26 14:07
 * updateUser:     更新者：
 * updateDate:     2020/11/26 14:07
 * updateRemark:   更新说明：
 * version:        1.0
 */
public interface MainView extends BaseView {
    void loginSuccess();
    void loginError();
}
