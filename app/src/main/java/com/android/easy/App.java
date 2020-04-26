package com.android.easy;

import android.content.Intent;

import com.android.easy.app.BaseApplication;
import com.android.easy.base.spf.SharedPreferencesUtils;
import com.android.easy.base.util.IdUtils;
import com.android.easy.retrofit.ApiService;
import com.android.easy.retrofit.RetrofitHttp;
import com.android.easy.retrofit.initerceptor.TokenInterceptor;
import com.android.easy.ui.LoginActivity;

import okhttp3.Response;

/**
 * @author abook23@163.com
 * @date 2020/03/31
 */
public class App extends BaseApplication {
    @Override
    public void onShouldInitApp() {
        //比较重要的初始化
        SharedPreferencesUtils.initialize(getApplicationContext());//存储初始化
    }

    @Override
    public void onHandleInit() {
        //可以后台参数的
        initHttp();
    }

    private void initHttp() {

        SharedPreferencesUtils.putParam("token", IdUtils.getUUId());//假装有token

        RetrofitHttp.init(getApplicationContext(), URL.BASE_URL);//网络请求初始化
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
    }
}
