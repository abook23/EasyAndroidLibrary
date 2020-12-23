package com.android.easy.base.net;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.android.easy.base.listener.OnNetStatusListener;
import com.android.easy.base.util.AndroidUtils;


public class NetworkManager {

    public static boolean isAvailable;
    public static boolean isAvailable_wifi;
    public enum NetworkType {
        TYPE_LOST, TYPE_CELLULAR, TYPE_WIFI, TYPE_ETHERNET, TYPE_OTHER
    }
    /**
     * OnNetStatusListener: NoNetWork; 0 TYPE_MOBILE; 1 TYPE_WIFI; 2 TYPE_ETHERNET; 3 OtherNetWork
     *
     * @param context
     * @param onNetStatusListener
     */
    public static void requestNetwork(Context context, final OnNetStatusListener onNetStatusListener) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            context.registerReceiver(new NetBroadcastReceiver(), intentFilter);
            NetBroadcastReceiver.setOnNetListener(onNetStatusListener);
        } else {
            requestNetwork(context, new ConnectivityManager.NetworkCallback() {
                @Override
                public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
                    super.onCapabilitiesChanged(network, networkCapabilities);
                    if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
                        if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
//                            Log.d(TAG, "onCapabilitiesChanged: 网络类型为wifi");
                            onNetStatusListener.onNetStatus(NetworkType.TYPE_WIFI);
                            isAvailable_wifi = true;
                        } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
//                            Log.d(TAG, "onCapabilitiesChanged: 蜂窝网络");
                            onNetStatusListener.onNetStatus(NetworkType.TYPE_CELLULAR);
                        } else {
//                            L.d(TAG, "onCapabilitiesChanged: 其他网络");
                            onNetStatusListener.onNetStatus(NetworkType.TYPE_OTHER);
                        }
                    }
                }

                @Override
                public void onAvailable(Network network) {
                    super.onAvailable(network);
                    isAvailable = true;
                }

                @Override
                public void onLost(Network network) {
                    super.onLost(network);
                    isAvailable = false;
                    isAvailable_wifi = false;
                    onNetStatusListener.onNetStatus(NetworkType.TYPE_LOST);
                }
            });
        }
        AndroidUtils.getNetWorkType(context, onNetStatusListener);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static ConnectivityManager requestNetwork(Context context, ConnectivityManager.NetworkCallback networkCallback) {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest.Builder builder = new NetworkRequest.Builder();

        NetworkRequest request = builder
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .build();
        mConnectivityManager.requestNetwork(request, networkCallback);
        return mConnectivityManager;
    }
}
