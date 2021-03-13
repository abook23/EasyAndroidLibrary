package com.android.easy.retrofit.listener.loading;

import java.io.IOException;

import okhttp3.ResponseBody;

/**
 * description:    描述
 * author:         yangxiong
 * e-mail:         abook23@163.com
 * createDate:     2021/3/13 0:16
 * updateUser:     更新者：
 * updateDate:     2021/3/13 0:16
 * updateRemark:   更新说明：
 * version:        1.0
 */
public abstract class UploadCall implements Call {

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onFail(Throwable e) {

    }

    @Override
    public void onSuccess(ResponseBody responseBody) {
        try {
            onSuccess(responseBody.string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCancel() {

    }

    public abstract void onSuccess(String s);
}
