package com.android.easy.play;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;

/**
 * @Description: 描述
 * @Author: yangxiong
 * @E-mail: abook23@163.com
 * @CreateDate: 2020/8/20 11:21
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/8/20 11:21
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class VideoPlayCache {
    private String url;

    public void cacheVideo(Context context, String url, OnVideoCacheListener onVideoCacheListener) {
        this.url = url;
        String cachePath = DownloadVideoManager.getCacheLocalPlayPath(context, url);
//        if (new File(cachePath).exists()) {
//            onVideoCacheListener.onBufferingUpdate(cachePath);
//        }
        DownloadVideoManager.getInstance().shutdownNow();
        DownloadVideoManager.getInstance().downloadM3U8(context, url, new DownloadVideoManager.Call() {
            @Override
            public void onStart(File file, long max) {

            }

            @Override
            public void onProgress(int progress, long max) {
                onVideoCacheListener.onBufferingUpdate(cachePath);
            }

            @Override
            public void onComplete() {
                onVideoCacheListener.onBufferingUpdate(cachePath);
            }
        });
    }

    public void stop() {
        DownloadVideoManager.getInstance().stop(url);
    }

    public interface OnVideoCacheListener {
        void onBufferingUpdate(String path);
    }
}
