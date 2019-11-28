package com.android.easy.app;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import com.android.easy.base.net.NetworkUtils;
import com.android.easy.base.util.L;

import java.util.List;

public class BaseApplication extends Application {



    @Override
    public void onCreate() {
        super.onCreate();
        if (shouldInit()) {
            onShouldInitApp();
        }
    }

    public void onShouldInitApp() {
        NetworkUtils.registerNetwork(this, this::onNetStatusListener);
    }

    public void onNetStatusListener(int netType, String netName) {
        L.d("net-status netType:" + netType + "--netName:" + netName);
    }

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
