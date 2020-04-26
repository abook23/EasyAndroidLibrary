### 开启沉溺模式
``` 
setStatusBarTranslucentStatus();
```

### 网络请求 
``` 
get("user/getMemberInfo", new HttpCall<ResponseBean<UserInfo>>() {

    @Override
    public void onSuccess(@NonNull ResponseBean<UserInfo> userInfoResponseBean) {

    }
});
```

