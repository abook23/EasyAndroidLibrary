package com.android.easy.play;

import android.content.Context;

import java.io.File;

/**
 * Description: 描述
 * Author: yangxiong
 * E-mail: abook23@163.com
 * CreateDate: 2020/8/20 11:21
 * UpdateUser: 更新者：
 * UpdateDate: 2020/8/20 11:21
 * UpdateRemark: 更新说明：
 * Version: 1.0
 */
public class VideoPlayCache {
    private static String play_url="";

    public static void cacheVideo(Context context, String url, OnVideoCacheListener onVideoCacheListener) {
        play_url = url;
        DownloadVideoManager.getInstance().shutdownNow();
        String cachePath = DownloadVideoManager.getCacheLocalPlayPath(context, url);
        if (new File(cachePath).exists()){
            onVideoCacheListener.onBufferingUpdate(cachePath);
        }
        DownloadVideoManager.getInstance().downloadFile(context, url, new DownloadVideoManager.Call() {
            @Override
            public void onStart(File file, long max) {

            }

            @Override
            public void onProgress(long progress, long max) {
                onVideoCacheListener.onBufferingUpdate(cachePath);
            }

            @Override
            public void onComplete(File file) {
                onVideoCacheListener.onBufferingUpdate(cachePath);
            }
        });
    }

    public static void stop(){
        DownloadVideoManager.getInstance().stop(play_url);
    }

    public interface OnVideoCacheListener {
        void onBufferingUpdate(String path);
    }
}
