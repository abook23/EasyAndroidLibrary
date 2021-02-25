package com.android.easy.ui;

import android.view.View;

import androidx.annotation.NonNull;

import com.android.easy.R;
import com.android.easy.URL;
import com.android.easy.app.base.BaseListFragment;
import com.android.easy.data.bean.UserInfo;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;
import java.util.Map;

public class UserInfoBaseListFragment extends BaseListFragment<String,UserInfo, BaseViewHolder> {
    @Override
    protected int getItemLayout() {
        return R.layout.item_list_01;
    }

    @Override
    protected void initView(@NonNull View rootView) {
        super.initView(rootView);
    }


    @Override
    protected String getApiUrl() {
        return URL.allFriend;
    }

    @Override
    protected List<UserInfo> onResponseData(@NonNull String resData) {
        return null;
    }

    @Override
    protected void setParams(@NonNull Map<String, Object> params) {

    }

    @Override
    public void onBaseQuickAdapterConvert(BaseViewHolder helper, UserInfo item) {

    }
}
