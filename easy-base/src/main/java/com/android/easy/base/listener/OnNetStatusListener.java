package com.android.easy.base.listener;

import com.android.easy.base.net.NetworkManager;

/**
 * Created by abook23 on 2016/3/25.
 */
public interface OnNetStatusListener {
    void onNetStatus(NetworkManager.NetworkType networkType);
}
