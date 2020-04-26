package com.android.easy.ui;

import com.android.easy.R;
import com.android.easy.app.base.BaseAppCompatListActivity;
import com.android.easy.data.ResponseBean;
import com.android.easy.data.model.UserInfo;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;
import java.util.Map;

/**
 * @author abook23@163.com
 * @date 2020/04/02
 */
public class ListActivity extends BaseAppCompatListActivity<ResponseBean<List<UserInfo>>, UserInfo> {

    @Override
    public int getItemLayout() {
        return R.layout.item_main;
    }

    @Override
    public String getURL() {
        return null;
    }

    @Override
    public void setParams(Map<String, Object> params) {

    }

    @Override
    public List<UserInfo> onResponseData(ResponseBean<List<UserInfo>> userInfoResponseBean) {
        return userInfoResponseBean.data;
    }

    @Override
    public void onBaseQuickAdapterConvert(BaseViewHolder helper, UserInfo item) {

    }
}
