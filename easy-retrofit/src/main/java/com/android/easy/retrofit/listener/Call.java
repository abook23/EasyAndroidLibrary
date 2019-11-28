package com.android.easy.retrofit.listener;

import com.android.easy.retrofit.rxjava.ObserverBaseWeb;
import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;

public abstract class Call<T> extends ObserverBaseWeb<ResponseBody> {

    @Override
    public void onNext(ResponseBody responseBody) {
        try {
            Gson gson = new Gson();
            String jsonStr = responseBody.string();
            Type type = getClass().getGenericSuperclass();
            Type[] types = ((ParameterizedType) type).getActualTypeArguments();
            T result;
            if (types[0] instanceof Class) {
                Class<T> c = (Class<T>) types[0];
                result = gson.fromJson(jsonStr, c);
            } else {
                Type type1 = ((ParameterizedType) types[0]).getRawType();
                Type[] type2 = ((ParameterizedType) types[0]).getActualTypeArguments();
                Type ty = new ParameterizedTypeImpl((Class) type1, new Type[]{type2[0]});
                result = gson.fromJson(jsonStr, ty);
            }
            try {//避免一些异常操作，退出app
                onSuccess(result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
            onError(e);
        }
    }

    public abstract void onSuccess(T t);

    private class ParameterizedTypeImpl implements ParameterizedType {
        private final Class raw;
        private final Type[] args;

        public ParameterizedTypeImpl(Class raw, Type[] args) {
            this.raw = raw;
            this.args = args != null ? args : new Type[0];
        }

        @Override
        public Type[] getActualTypeArguments() {
            return args;
        }

        @Override
        public Type getRawType() {
            return raw;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }
    }
}
