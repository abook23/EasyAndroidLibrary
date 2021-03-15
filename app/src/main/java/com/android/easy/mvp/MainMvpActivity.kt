package com.android.easy.mvp

import android.os.Bundle
import android.view.View
import com.android.easy.R
import com.android.easy.app.mvp.BaseMVPActivity

class MainMvpActivity : BaseMVPActivity<MainPresenter>(),MainView {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_mvp)
    }

    fun onLoginClick(view:View){
        mPresenter.toLong("zhangsan","zhangsan")
//        mPresenter.toLong("zhangsan","zhangsan1")
    }

    override fun loginSuccess() {
        showToast("成功")
    }

    override fun loginError() {
        showToast("失败")
    }
}