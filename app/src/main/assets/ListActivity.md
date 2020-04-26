## 继承BaseAppCompatListActivity<T, M>
T 代表 请求网络 返回的数据 格式
M 代表 adapter 数据 格式
```
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

```
## 加载第一页
```
    @Override
    public void onRefreshParam(Map<String, Object> params, int page) {
        super.onRefreshParam(params, page);
    }

```

## 加载更多
```
    @Override
    public void onLoadMoreRequestedParam(Map<String, Object> params, int page) {
        super.onLoadMoreRequestedParam(params, page);
    }
```