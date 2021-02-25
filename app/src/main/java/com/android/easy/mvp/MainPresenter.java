package com.android.easy.mvp;

import com.android.easy.app.mvp.BasePresenter;

/**
 * description:    M 和 V 的中间桥梁
 * author:         yangxiong
 * e-mail:         abook23@163.com
 * createDate:     2020/11/26 14:06
 * updateUser:     更新者：
 * updateDate:     2020/11/26 14:06
 * updateRemark:   更新说明：
 * version:        1.0
 */
public class MainPresenter extends BasePresenter<MainModel, MainView> {


    public MainPresenter() {
        super(new MainModel());
    }

    public void toLong(String userName, String password) {
        showLoading();
        boolean b = mModel.toLogin(userName, password);
        dismissLoading();
        if (b) {
            showToast("登录成功");
            getView().loginSuccess();
        } else {
            showToast("登录失败");
            getView().loginError();
        }
    }
}
