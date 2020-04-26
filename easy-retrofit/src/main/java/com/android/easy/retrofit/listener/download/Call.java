package com.android.easy.retrofit.listener.download;

import java.io.File;

/**
 * Created by abook23 on 2016/11/25.
 * Versions 1.0
 */

public interface Call {

    void onStart();
    /**
     * 暂停
     */
    void onPause();

    /**
     * 恢复
     */
    void onResume();

    /**
     * 当前现在量
     *
     * @param size    当前量
     * @param maxSize 总大小
     */
    void onSize(long size, long maxSize);

    /**
     * 失败
     */
    void onFail(Throwable e);

    /**
     * 成功
     */
    void onSuccess(File file);

    /**
     * 取消下载
     */
    void onCancel();

}
