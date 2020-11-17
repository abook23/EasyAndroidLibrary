package com.android.easy.okhttp.listener;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * @Description: 描述
 * @Author: yangxiong
 * @E-mail: abook23@163.com
 * @CreateDate: 2020/11/13 11:22
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/11/13 11:22
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public abstract class Call<T> implements Callback, LifecycleObserver {

    private Lifecycle mLifecycle;
    private boolean isDestroy;

    public Call() {
    }

    /**
     * 管理生命周期
     *
     * @param lifecycle
     */
    public Call(Lifecycle lifecycle) {
        mLifecycle = lifecycle;
        mLifecycle.addObserver(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        isDestroy = true;
    }

    @Override
    public void onFailure(okhttp3.Call call, IOException e) {
//        onError(e);
        mHandler.obtainMessage(MESSAGE_ON_ERROR, e);
        if (mLifecycle != null)
            mLifecycle.removeObserver(this);
    }

    @Override
    public void onResponse(okhttp3.Call call, Response response) throws IOException {
        ResponseBody responseBody = response.body();
        if (isDestroy) {
            call.cancel();
        } else {
            onNext(responseBody);
        }
        if (mLifecycle != null)
            mLifecycle.removeObserver(this);
    }

    public void onNext(ResponseBody responseBody) {
        try {
            Gson gson = new Gson();
            String data = responseBody.string();
            Type type = getClass().getGenericSuperclass();
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] types = parameterizedType.getActualTypeArguments();
            T result;
            try {
                if (types[0] instanceof Class) {
                    Class<T> c = (Class<T>) types[0];
                    if ("String".equals(c.getSimpleName())) {
                        result = (T) data;
                    } else {
                        result = gson.fromJson(data, c);
                    }
                } else {
                    Type rawType = ((ParameterizedType) types[0]).getRawType();
                    Type[] type2 = ((ParameterizedType) types[0]).getActualTypeArguments();
                    Type ty = new ParameterizedTypeImpl((Class) rawType, type2, null);
                    result = gson.fromJson(data, ty);
                }
            } catch (Exception e) {
                //json 解析出错 避免一些异常操作，退出app
                e.printStackTrace();
//                onError(e);
                mHandler.obtainMessage(MESSAGE_ON_ERROR, e);
                return;
            }
            try {//避免一些异常操作，退出app
//                onResponse(result);
                mHandler.obtainMessage(MESSAGE_ON_RESPONSE, result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
//            onError(e);
            mHandler.obtainMessage(MESSAGE_ON_ERROR, e);
        }
    }

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


    private static final int MESSAGE_ON_RESPONSE = 0x1;
    private static final int MESSAGE_ON_ERROR = 0x2;
    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (isDestroy) {
                return;
            }
            try {
                if (msg.what == MESSAGE_ON_RESPONSE) {
                    onResponse((T) msg.obj);
                } else if (msg.what == MESSAGE_ON_ERROR) {
                    onError((Exception) msg.obj);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public abstract void onError(Exception e);

    public abstract void onResponse(@NonNull T t);
}
