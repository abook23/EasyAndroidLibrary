package com.android.easy.okhttp;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * Created by abook23 on 2016/11/18.
 * Versions 1.0
 */

public class CookieManger implements CookieJar {

    private static final String KEY_COOKIE_PREFERENCES = "COOKIE_PREFERENCES";
    private static final String KEY_cookie = "cookie";
    private SharedPreferences spf;
    private List<Cookie> mCookies;

    public CookieManger(Context applicationContext) {
        spf = applicationContext.getSharedPreferences(KEY_COOKIE_PREFERENCES, Context.MODE_PRIVATE);
    }

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        if (cookies != null && cookies.size() > 0) {
            for (Cookie cookie : cookies) {
                if ("JSESSIONID".equals(cookie.name())) {
                    mCookies = cookies;
                    Gson gson = new Gson();
                    String cookiesStr = gson.toJson(cookies);
                    SharedPreferences.Editor edit = spf.edit();
                    edit.putString(KEY_cookie, cookiesStr);
                    edit.apply();
                }
            }
        }
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        if (mCookies == null || mCookies.size() == 0) {
            String cookiesStr = spf.getString(KEY_cookie, null);
            if (cookiesStr != null) {
                mCookies = new Gson().fromJson(cookiesStr, new TypeToken<List<Cookie>>() {
                }.getType());
            } else {
                mCookies = new ArrayList<>();
            }
        }
        return mCookies;
    }
}
