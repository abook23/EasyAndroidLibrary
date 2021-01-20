package com.android.easy.retrofit.initerceptor;

import android.util.ArrayMap;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * description:    描述
 * author:         yangxiong
 * e-mail:         abook23@163.com
 * createDate:     2021/1/20 13:42
 * updateUser:     更新者：
 * updateDate:     2021/1/20 13:42
 * updateRemark:   更新说明：
 * version:        1.0
 */
public abstract class CustomizeInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder requestBuilder = request.newBuilder();
        Map<String, String> dataMap = new HashMap<>();
        if (request.body() instanceof FormBody) {
            FormBody.Builder newFormBodyBuilder = new FormBody.Builder();
            FormBody oidFormBody = (FormBody) request.body();
            for (int i = 0; i < oidFormBody.size(); i++) {
                String key = oidFormBody.name(i);
                String value = oidFormBody.value(i);
                dataMap.put(key, value);
            }
            //加密处理
            onRequest(dataMap, newFormBodyBuilder);
            //newFormBodyBuilder.add("data", encodeData)
            requestBuilder.method(request.method(), newFormBodyBuilder.build());
        }
        Request newRequest = requestBuilder.build();
        Response response = chain.proceed(newRequest);
        if (response.isSuccessful()) {
            MediaType mediaType = response.body().contentType();
            String responseData = response.body().string();
            //解密
            String decodeResponseData = onResponse(responseData);

            return response.newBuilder()
                    .body(ResponseBody.create(mediaType, decodeResponseData))
                    .build();
        }
        return response;
    }

    public abstract void onRequest(Map<String, String> params, FormBody.Builder newFormBodyBuilder);

    public abstract String onResponse(String responseData);
}
