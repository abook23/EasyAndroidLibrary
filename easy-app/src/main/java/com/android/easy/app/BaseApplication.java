package com.android.easy.app;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

import androidx.annotation.NonNull;

import com.android.easy.base.net.NetworkManager;

import java.util.List;
import java.util.concurrent.Executors;

public abstract class BaseApplication extends Application {
    public static boolean isNetworkAvailable;
    public static boolean isNetworkAvailable_wifi;
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
            NetworkManager.requestNetwork(this, new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(@NonNull Network network) {
                    super.onAvailable(network);
                    isNetworkAvailable = true;
                }

                @Override
                public void onLost(@NonNull Network network) {
                    super.onLost(network);
                    isNetworkAvailable = false;
                    isNetworkAvailable_wifi = false;
                }

                @Override
                public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
                    super.onCapabilitiesChanged(network, networkCapabilities);
                    if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
                        isNetworkAvailable_wifi = networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
                    }else {
                        isNetworkAvailable_wifi = false;
                    }
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
