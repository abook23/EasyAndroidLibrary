package com.android.easy.retrofit.listener;

import androidx.annotation.NonNull;

import com.android.easy.retrofit.rxjava.ObserverBaseWeb;
import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

public abstract class Call<T> extends ObserverBaseWeb<ResponseBody> {

    @Override
    public void onSubscribe(Disposable d) {
        super.onSubscribe(d);
    }

    @Override
    public void onComplete() {
        super.onComplete();
    }

    @Override
    public void onError(Throwable e) {
        super.onError(e);
    }

    @Override
    public void onNext(ResponseBody responseBody) {
        try {
            Gson gson = new Gson();
            String jsonStr = responseBody.string();
            Type type = getClass().getGenericSuperclass();
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] types = parameterizedType.getActualTypeArguments();
            T result;
            try {
                if (types[0] instanceof Class) {
                    Class<T> c = (Class<T>) types[0];
                    if ("String".equals(c.getSimpleName())) {
                        result = (T) jsonStr;
                    } else {
                        result = gson.fromJson(jsonStr, c);
                    }
                } else if (types[0] instanceof ParameterizedType){
                    ParameterizedType parameterizedType1 = (ParameterizedType) types[0];
                    Type rawType = (parameterizedType1).getRawType();
                    Type[] type2 = (parameterizedType1).getActualTypeArguments();
                    Type ty = new ParameterizedTypeImpl((Class<?>) rawType, type2, null);
                    result = gson.fromJson(jsonStr, ty);
                }else {
                    result = null;
                }
            } catch (Exception e) {
                //json 解析出错 避免一些异常操作，退出app
                e.printStackTrace();
                onError(e);
                return;
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

    public abstract void onSuccess(@NonNull T t);


    public static class ParameterizedTypeImpl implements ParameterizedType {
        private final Class<?> raw;
        private final Type[] args;
        private final Type owner;

        public ParameterizedTypeImpl(Class<?> raw, Type[] args, Type owner) {
            this.raw = raw;
            this.args = args;
            this.owner = owner;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return args.clone();
        }

        @Override
        public Type getRawType() {
            return raw;
        }

        @Override
        public Type getOwnerType() {
            return owner;
        }
    }
}
