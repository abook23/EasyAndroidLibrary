package com.android.easy.app;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import java.util.List;
import java.util.concurrent.Executors;

public abstract class BaseApplication extends Application {

    //NetworkUtils.registerNetwork(this, this::onNetStatusListener);
    @Override
    public void onCreate() {
        super.onCreate();
        if (shouldInit()) {
            onShouldInitApp();
            Executors.newSingleThreadExecutor().submit(new Runnable() {
                @Override
                public void run() {
                    onHandleInit();
                }
            });
        }
    }

    public abstract void onShouldInitApp();

    public abstract void onHandleInit();

    protected boolean shouldInit() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }
}
