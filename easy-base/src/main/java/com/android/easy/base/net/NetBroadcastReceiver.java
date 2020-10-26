package com.android.easy.base.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.android.easy.base.listener.OnNetStatusListener;

/**
 * 时时监听网络状态
 *  2015-5-10 下午8:10:04
 * @author abook23
 */
public class NetBroadcastReceiver extends BroadcastReceiver {

    /**
     * filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
     */
    private static OnNetStatusListener onNetListener;


    public static void setOnNetListener(OnNetStatusListener listener) {
        onNetListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = mConnectivityManager.getActiveNetworkInfo();
        NetworkManager.NetworkType networkType;
        if (netInfo != null && netInfo.isAvailable()) {
            NetworkManager.isAvailable = true;
            switch (netInfo.getType()) {
                case ConnectivityManager.TYPE_MOBILE:
                    networkType = NetworkManager.NetworkType.TYPE_CELLULAR;
//                    netName = netInfo.getSubtypeName();
                    break;
                case ConnectivityManager.TYPE_WIFI:
                    NetworkManager.isAvailable_wifi = true;
                    networkType = NetworkManager.NetworkType.TYPE_WIFI;
//                    netName = netInfo.getTypeName();
                    break;
                case ConnectivityManager.TYPE_ETHERNET:
                    networkType = NetworkManager.NetworkType.TYPE_ETHERNET;
//                    netName = netInfo.getTypeName();
                    break;
                default:
                    networkType = NetworkManager.NetworkType.TYPE_OTHER;
//                    netName = "OtherNetWork";
                    break;
            }
        } else {
            NetworkManager.isAvailable = false;
            NetworkManager.isAvailable_wifi = false;
            networkType = NetworkManager.NetworkType.TYPE_LOST;
        }
        if (onNetListener != null) {
            onNetListener.onNetStatus(networkType);
        }
    }
}
