package com.android.easy.dialog;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class DownloadFileManager {
    private DownLoadBroadcast downLoadBroadcast;
    private String filePath;
    private Timer mTimer = new Timer();
    private DownloadManager mDownloadManager;
    private Call mCall;
    private Context mContext;
    private long downLoadId;
    private boolean successful;

    public static DownloadFileManager getInstance() {
        return new DownloadFileManager();
    }

    public void cancel() {
        try {
            if (mDownloadManager != null) {
                if (!successful) {
                    mDownloadManager.remove(downLoadId);
                }
                if (downLoadBroadcast!=null){
                    mContext.unregisterReceiver(downLoadBroadcast);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mTimer != null) {
                mTimer.cancel();
            }
        }
    }


    /**
     * 注册广播
     */
    private void registerBroadcast(Context context) {
        /**注册service 广播 1.任务完成时 2.进行中的任务被点击*/
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        intentFilter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED);
        context.registerReceiver(downLoadBroadcast = new DownLoadBroadcast(), intentFilter);
    }

    public DownloadFileManager downloadFile(Context context, String url, Call call) {
        return downloadFile(context,url,true,call);
    }

    public DownloadFileManager downloadFile(Context context, String url,boolean coverage, Call call) {
        mCall = call;
        mContext = context;
        mDownloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        String fileName = url.substring(url.lastIndexOf("/") + 1);
        filePath = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + File.separator + fileName;
//        filePath = Environment.getDownloadCacheDirectory()+ File.separator + fileName;
        if (!coverage && new File(filePath).exists()) {
            Log.d("DownloadFileManager", "文件已存在" + filePath);
            call.onComplete(filePath);
            return this;
        }
        registerBroadcast(context);

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
//        if (!isMast) {
//            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
//        }
        /**
         * Request.VISIBILITY_VISIBLE
         * 在下载进行的过程中，通知栏中会一直显示该下载的Notification，当下载完成时，该Notification会被移除，这是默认的参数值。
         *
         * Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
         * 在下载过程中通知栏会一直显示该下载的Notification，在下载完成后该Notification会继续显示，直到用户点击该Notification或者消除该Notification。
         *
         * Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION
         * 只有在下载完成后该Notification才会被显示。
         *
         * Request.VISIBILITY_HIDDEN
         * 不显示该下载请求的Notification。如果要使用这个参数，需要在应用的清单文件中加上DOWNLOAD_WITHOUT_NOTIFICATION权限。
         * */
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setTitle("下载");
        request.setDescription("正在下载中...");
        request.setAllowedOverRoaming(false);


//        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);//sd卡下
        request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, fileName);//app 内部路径

        downLoadId = mDownloadManager.enqueue(request);
        call.onStart(downLoadId);
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                queryDownTask(mContext, mDownloadManager, downLoadId);
            }
        }, 0, 500);
        return this;
    }

    private void queryDownTask(Context context, DownloadManager downManager, long downLoadId) {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downLoadId);
        Cursor cursor = downManager.query(query);
        if (cursor == null) {
            return;
        }
        while (cursor.moveToNext()) {
//            String downId = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_ID));
//            String title = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE));
//            String address = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
            int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));//下载状态
            final int size = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));//已下载大小
            final int sizeTotal = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));//总大小
            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                //已下载
                successful = true;
                cancel();
                mCall.onComplete(filePath);
            }
            if (status == DownloadManager.STATUS_RUNNING) {
                mCall.onProgress(size, sizeTotal);
            }
        }
        cursor.close();
    }

    public class DownLoadBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
//                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
//                DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
//                Uri uri = downloadManager.getUriForDownloadedFile(downLoadId);
//                queryDownTask(context, downloadManager, id);
                successful = true;
                cancel();
                mCall.onComplete(filePath);
            } else if (intent.getAction().equals(DownloadManager.ACTION_NOTIFICATION_CLICKED)) {
//                Toast.makeText(MainActivity.this, "别瞎点！！！", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public interface Call {
        void onStart(long downloadId);

        void onProgress(long progress, long max);

        void onComplete(String filePath);

        void onError(String msg);
    }
}
