package com.abook23.tv.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.abook23.tv.App;
import com.abook23.tv.ben.CacheVideoBean;
import com.abook23.tv.dao.CacheVideoBeanDao;
import com.android.easy.play.DownloadVideoManager;

import java.io.File;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Description: 描述
 * Author: yangxiong
 * E-mail: abook23@163.com
 * CreateDate: 2020/8/13 21:19
 * UpdateUser: 更新者：
 * UpdateDate: 2020/8/13 21:19
 * UpdateRemark: 更新说明：
 * Version: 1.0
 */
public class CacheVideoService extends Service {
    private ConcurrentMap<String, DownloadVideoManager.Call> downloadListener = new ConcurrentHashMap<>();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        starDownload();
    }

    public void starDownload() {
        if (App.isNetworkAvailable_wifi) {
            List<CacheVideoBean> list = App.getDaoSession().getCacheVideoBeanDao().queryBuilder()
                    .where(CacheVideoBeanDao.Properties.Download_complete.eq(false)).list();
            for (CacheVideoBean cacheVideoBean : list) {
                if (!cacheVideoBean.download_complete) {//没有完成
                    if (!cacheVideoBean.download_pause) {//没有暂停
                        download(cacheVideoBean.url, cacheVideoBean.v_id, cacheVideoBean.v_num);
                    }
                }
            }
        }
    }

    public void addListener(String url, DownloadVideoManager.Call call) {
        downloadListener.put(url, call);
    }

    public void removeListener(String url){
        downloadListener.remove(url);
    }

    public void clearListener(){
        downloadListener.clear();
    }


    public void download(String url, long v_id, int vNum) {
        CacheVideoBean cacheVideoBean = App.getDaoSession().getCacheVideoBeanDao().load(url);
        if (cacheVideoBean == null) {
            createCacheVideo(url, v_id, vNum);
        }
        DownloadVideoManager.getInstance().downloadFile(this, url, new DownloadVideoManager.Call() {
            @Override
            public void onStart(File file, long max) {

            }

            @Override
            public void onProgress(long progress, long max,long bytes) {
                saveCacheVideo(url, max, progress, progress == max);
                DownloadVideoManager.Call call = downloadListener.get(url);
                if (call != null) {
                    call.onProgress(progress, max,bytes);
                }
            }

            @Override
            public void onComplete(File file) {
                saveCacheVideo(url, 0, 0, true);
                downloadListener.remove(url);
            }
        });
    }

    public void pause(String url) {
        CacheVideoBean cacheVideoBean = App.getDaoSession().getCacheVideoBeanDao().load(url);
        if (!cacheVideoBean.download_pause) {
            DownloadVideoManager.getInstance().stop(url);
        } else {
            download(url, cacheVideoBean.v_id, cacheVideoBean.v_num);
        }
        cacheVideoBean.download_pause = !cacheVideoBean.download_pause;
        App.getDaoSession().getCacheVideoBeanDao().updateInTx(cacheVideoBean);
    }

    private void saveCacheVideo(String url, long max, long progress, boolean isComplete) {
        CacheVideoBean cacheVideoBean = App.getDaoSession().getCacheVideoBeanDao().load(url);
        if (isComplete) {
            cacheVideoBean.download_progress = cacheVideoBean.download_max;
            cacheVideoBean.download_complete = true;
        } else {
            cacheVideoBean.download_max = max;
            cacheVideoBean.download_progress = progress;
            cacheVideoBean.download_complete = false;
        }
        App.getDaoSession().getCacheVideoBeanDao().updateInTx(cacheVideoBean);
    }

    private CacheVideoBean createCacheVideo(String url, long v_id, int vNum) {
        CacheVideoBean cacheVideoBean = new CacheVideoBean();
        cacheVideoBean.url = url;
        cacheVideoBean.v_id = v_id;
        cacheVideoBean.v_num = vNum;
        App.getDaoSession().getCacheVideoBeanDao().insertOrReplaceInTx(cacheVideoBean);
        return cacheVideoBean;
    }


    public class MyBinder extends Binder {
        public CacheVideoService getService() {
            return CacheVideoService.this;
        }
    }
}
