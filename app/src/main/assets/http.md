### HTTP
## 初始化
在 Application 中初始化
```
RetrofitHttp.init(getApplicationContext(), URL.BASE_URL);//网络请求初始化
```

### 继承 BaseAppCompatActivity
```
备注:BaseAppCompatActivity extends HttpAppCompatActivity

get("user/getMemberInfo", new HttpCall<ResponseBean<UserInfo>>() {
    @Override
    public void onSuccess(@NonNull ResponseBean<UserInfo> userInfoResponseBean) {

     }
});
```
```
get
protected <T> void get(String url, HttpCall<T> call)
protected <T> void get(String url, Map<String, Object> params, HttpCall<T> call)

post
protected <T> void post(String url, Map<String, Object> params, HttpCall<T> call)
protected <T> void post(String url, Map<String, Object> params, final boolean showProgress, final HttpCall<T> call)

文件上传 和 参数
protected <T> void upload(String url, Map<String, Object> params, HttpCall<T> call)

文件下载
protected Observable<File> download(String url)

文件下载 带进度监听
protected void download(String url, com.android.easy.retrofit.listener.download.Call call)

文件上传 带进度监听
protected void upload(String url, Map<String, Object> params, com.android.easy.retrofit.listener.loading.Call call)

```
## 高级使用
``` 
RetrofitHttp.addInterceptor(new TokenInterceptor() {
            @Override
            public String getHeaderTokenName() {
                //headerToken 名字
                return "token";
            }

            @Override
            public String getToken() {
                //获取token
                return SharedPreferencesUtils.getParam("token");
            }

            @Override
            public boolean testResponse(Response response) {
                //登录失败
                return response.code() != 401;
            }

            @Override
            public void toLogin() {
                //testResponse true 调用 toLogin, 一般为token 过期
                getApplicationContext().startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }

        });
```
```
    public static class UserInfo {
        public String userId;
        public String name;
    }

    public interface Api {
        @POST("user/login")
        Observable<ResponseBean<UserInfo>> login(@Query("mobile") String userName, @Query("passWord") String password, @Query("type") String type);

        @GET("user/getMemberInfo")
        Observable<ResponseBean<UserInfo>> userInfo();
    }

    RetrofitHttp.create(Api.class).userInfo()
                .compose(RxJavaUtils.defaultSchedulers())
                .subscribe(new ObserverBaseWeb<ResponseBean<UserInfo>>() {

                    @Override
                    public void onNext(ResponseBean<UserInfo> userInfoResponseBean) {

                    }
                });

```