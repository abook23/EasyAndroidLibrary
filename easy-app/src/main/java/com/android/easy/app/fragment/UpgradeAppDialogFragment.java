package com.android.easy.app.fragment;


import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.android.easy.app.R;
import com.android.easy.base.util.AndroidUtils;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;


public class UpgradeAppDialogFragment extends DialogFragment {

    private String url;
    private boolean isMast;
    private String content;
    private String versionName;
    private DownLoadBroadcast downLoadBroadcast;
    private long downLoadId;
    private Timer mTimer = new Timer();
    private DownloadManager mDownloadManager;
    private ProgressBar mProgressBar;
    private String apkPath;
    private Button downLoadButton;

    public UpgradeAppDialogFragment() {

    }

    public static UpgradeAppDialogFragment newInstance(String newApkUrl, String versionName, String content, boolean isMast) {
        UpgradeAppDialogFragment fragment = new UpgradeAppDialogFragment();
        fragment.isMast = isMast;
        fragment.url = newApkUrl;
        fragment.content = content;
        fragment.versionName = versionName;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);//无标题栏
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.easy_app_fragment_check_app_version, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView downLoadTitle = view.findViewById(R.id.downLoadTitle);
        downLoadTitle.setText("你有新的版本 " + versionName);
        downLoadButton = view.findViewById(R.id.downLoadButton);
        mProgressBar = view.findViewById(R.id.downLoadProgressBar);
        TextView contentTextView = view.findViewById(R.id.downLoadContent);
        contentTextView.setText(content);
        mDownloadManager = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);
        View easy_download_close = view.findViewById(R.id.easy_download_close);
        if (isMast) {
            easy_download_close.setVisibility(View.GONE);
        }else {
            easy_download_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }
        downLoadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downLoadId = downLoadFile(getContext(), url);
                downLoadButton.setText("系统升级中...");
                downLoadButton.setEnabled(false);
            }
        });
        registerBroadcast(getContext());
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                queryDownTask(getContext(), mDownloadManager, downLoadId);
            }
        }, 0, 100);

    }

    @Override
    public void onStart() {
        super.onStart();

        // 设置宽度为屏宽, 靠近屏幕底部。
        Window window = getDialog().getWindow();
        // 一定要设置Background，如果不设置，window属性设置无效
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        // 使用ViewGroup.LayoutParams，以便Dialog 宽度充满整个屏幕
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);

//        if (isMast) {
            getDialog().setCancelable(false);
            getDialog().setCanceledOnTouchOutside(false);
//            getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
//                @Override
//                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//                    if (keyCode == KeyEvent.KEYCODE_BACK) {
//                        return true;
//                    }
//                    return false;
//                }
//            });
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTimer.cancel();
        getContext().unregisterReceiver(downLoadBroadcast);
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

    private long downLoadFile(final Context context, String url) {
        String fileName = url.substring(url.lastIndexOf("/") + 1);
        apkPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + fileName;
        if (new File(apkPath).exists()) {
            downLoadButton.postDelayed(new Runnable() {
                @Override
                public void run() {
                    AndroidUtils.install(context, apkPath);
                }
            }, 2000);
            return -1;
        }

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        if (!isMast) {
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        }
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


        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);//sd卡下
//        request.setDestinationInExternalFilesDir(context,Environment.DIRECTORY_DOWNLOADS, "newApk.apk");//app 内部路径

        long downLoadId = mDownloadManager.enqueue(request);
        return downLoadId;
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
                AndroidUtils.install(context, apkPath);
            }
            if (status == DownloadManager.STATUS_RUNNING) {
                mProgressBar.post(new Runnable() {
                    @Override
                    public void run() {
                        mProgressBar.setMax(sizeTotal);
                        mProgressBar.setProgress(size);
                        String progress = String.format("%.2f%s", (float) size / sizeTotal * 100, "%");
                        downLoadButton.setText("系统升级中（" + progress + "）");
                    }
                });
            }
        }
        cursor.close();
    }

    public class DownLoadBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                mProgressBar.setProgress(mProgressBar.getMax());
                downLoadButton.setText("系统升级中（100%）");
//                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
//                DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
//                Uri uri = downloadManager.getUriForDownloadedFile(downLoadId);
//                queryDownTask(context, downloadManager, id);
                mTimer.cancel();
                AndroidUtils.install(context, apkPath);
            } else if (intent.getAction().equals(DownloadManager.ACTION_NOTIFICATION_CLICKED)) {
//                Toast.makeText(MainActivity.this, "别瞎点！！！", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
