package com.android.easy.ui.http;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.android.easy.R;
import com.android.easy.app.HttpCall;
import com.android.easy.app.base.BaseAppCompatActivity;
import com.android.easy.data.ResponseBean;
import com.android.easy.retrofit.ApiService;
import com.android.easy.retrofit.rxjava.ObserverBaseWeb;
import com.android.easy.retrofit.rxjava.RxJavaUtils;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public class HttpActivity extends BaseAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_http);

        ApiService.create(Api.class).userInfo()
                .compose(RxJavaUtils.defaultSchedulers())
                .subscribe(new ObserverBaseWeb<ResponseBean<UserInfo>>() {

                    @Override
                    public void onNext(ResponseBean<UserInfo> userInfoResponseBean) {

                    }
                });

        get("user/getMemberInfo", new HttpCall<ResponseBean<UserInfo>>() {

            @Override
            public void onSuccess(@NonNull ResponseBean<UserInfo> userInfoResponseBean) {

            }
        });

    }


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
}
