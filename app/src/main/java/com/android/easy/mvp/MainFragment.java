package com.android.easy.mvp;

import android.view.View;

import androidx.annotation.NonNull;

import com.android.easy.app.base.BaseFragment;
import com.android.easy.app.mvp.BaseMVPFragment;

/**
 * description:    描述
 * author:         yangxiong
 * e-mail:         abook23@163.com
 * createDate:     2021/3/15 14:35
 * updateUser:     更新者：
 * updateDate:     2021/3/15 14:35
 * updateRemark:   更新说明：
 * version:        1.0
 */
public class MainFragment extends BaseMVPFragment<MainPresenter> {

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected void initView(@NonNull View rootView) {

    }

    @Override
    protected void onOneLoadData() {

    }

    @Override
    protected void onVisibleLoadData() {

    }
}
