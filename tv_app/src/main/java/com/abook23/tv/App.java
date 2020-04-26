package com.abook23.tv;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.multidex.MultiDex;

import com.abook23.tv.dao.DaoMaster;
import com.abook23.tv.dao.DaoSession;
import com.android.easy.app.BaseApplication;
import com.android.easy.base.spf.SharedPreferencesUtils;
import com.android.easy.base.util.L;
import com.android.easy.retrofit.ApiService;

/**
 * @author abook23@163.com
 *  2019/12/05
 */
public class App extends BaseApplication {

    public static boolean isStart;

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
    }

    /**'
     * 异步初始化
     */
    @Override
    public void onHandleInit() {
        initGreenDao();//最好异步初始化
    }

    /**
     * 初始化GreenDao,直接在Application中进行初始化操作
     */
    private void initGreenDao() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this,   "movie.db");
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    private static DaoSession daoSession;

    public static DaoSession getDaoSession() {
        return daoSession;
    }

}
