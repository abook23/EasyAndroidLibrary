package com.android.easy.okhttp.listener.download;

/**
 * Created by abook23 on 2016/11/22.
 * Versions 1.0
 * 进度监听
 */

public interface OnDownloadListener {
    void onProgress(long bytesRead, long contentLength, boolean done);
}
