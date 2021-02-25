package com.abook23.tv.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.abook23.tv.App;
import com.abook23.tv.MainActivity;
import com.abook23.tv.R;
import com.abook23.tv.ben.CacheVideoBean;
import com.abook23.tv.dao.CacheVideoBeanDao;
import com.android.easy.base.net.NetworkManager;
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
        NetworkManager.requestNetwork(this, new ConnectivityManager.NetworkCallback() {
            @Override
            public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
                super.onCapabilitiesChanged(network, networkCapabilities);
                if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {//真正的连接网络
                   boolean isNetworkAvailable_wifi = networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
                   if (isNetworkAvailable_wifi){
                       starDownload();
                   }
                }else {
                    DownloadVideoManager.getInstance().stopAll();
                }
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForegroundNotification("200");
        starDownload();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(false);
    }

    private void startForegroundNotification(String channelId) {
        //设置点击跳转
        Context context = getApplicationContext();
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String name = "channel_name_1";
            NotificationChannel channel = new NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_HIGH);
//            channel.setSound(null, null);
//            channel.enableLights(true);//设置提示灯
//            channel.setLightColor(Color.RED);//设置提示灯颜色
//            channel.setShowBadge(true);//显示logo
            notificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setContentTitle(context.getResources().getString(R.string.app_name))
//              .setContentText("运行中...")//内容
                .setWhen(System.currentTimeMillis())
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setContentIntent(pendingIntent)
                .setAutoCancel(false)// 设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(true)// true，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setSmallIcon(R.mipmap.ic_launcher);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(100, builder.build());
        } else {
            notificationManager.notify(100, builder.build());
        }

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

    public void removeListener(String url) {
        downloadListener.remove(url);
    }

    public void clearListener() {
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
                DownloadVideoManager.Call call = downloadListener.get(url);
                if (call != null) {
                    call.onStart(file, max);
                }
            }

            @Override
            public void onProgress(long progress, long max, long bytes) {
                DownloadVideoManager.Call call = downloadListener.get(url);
                saveCacheVideo(url, max, progress, progress == max);
                if (call != null) {
                    call.onProgress(progress, max, bytes);
                }
            }

            @Override
            public void onComplete(File file) {
                DownloadVideoManager.Call call = downloadListener.get(url);
                saveCacheVideo(url, 0, 0, true);
                if (call != null) {
                    call.onComplete(file);
                }
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
