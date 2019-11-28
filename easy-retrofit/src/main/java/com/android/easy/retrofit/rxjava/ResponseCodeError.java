package com.android.easy.retrofit.rxjava;

/**
 * Created by abook23 on 2016/11/18.
 * Versions 1.0
 */

public class ResponseCodeError extends RuntimeException {
    public ResponseCodeError(String msg) {
        super(msg);
    }
}
