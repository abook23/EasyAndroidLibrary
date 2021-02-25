package com.abook23.tv;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.multidex.MultiDex;

import com.abook23.tv.dao.DaoMaster;
import com.abook23.tv.dao.DaoSession;
import com.abook23.tv.service.CacheVideoService;
import com.android.easy.app.BaseApplication;
import com.android.easy.base.listener.OnNetStatusListener;
import com.android.easy.base.net.NetworkManager;
import com.android.easy.base.spf.SharedPreferencesUtils;
import com.android.easy.base.util.AndroidUtils;
import com.android.easy.base.util.L;
import com.android.easy.retrofit.ApiService;

/**
 * author abook23@163.com
 * 2019/12/05
 */
public class App extends BaseApplication {

    public static boolean isStart;
    private static CacheVideoService cacheVideoService;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    /**
     * 当前 app 进程初始化
     */
    @Override
    public void onShouldInitApp() {
        long t = System.currentTimeMillis();
//      L.d((System.currentTimeMillis() - t) + "ms");
        SharedPreferencesUtils.initialize(this);
        ApiService.init(this, URL.BASE_URL);
        startService(new Intent(this, CacheVideoService.class));
        bindService(new Intent(this, CacheVideoService.class), connection, Service.BIND_AUTO_CREATE);
    }

    /**
     * '
     * 异步初始化
     */
    @Override
    public void onHandleInit() {
        initGreenDao();//最好异步初始化
    }

    public static CacheVideoService getCacheVideoService() {
        return cacheVideoService;
    }

    /**
     * 初始化GreenDao,直接在Application中进行初始化操作
     */
    private void initGreenDao() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "movie.db");
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    private static DaoSession daoSession;

    public static DaoSession getDaoSession() {
        return daoSession;
    }

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            cacheVideoService = ((CacheVideoService.MyBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
}
