package com.abook23.tv

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.database.sqlite.SQLiteDatabase
import android.os.IBinder
import android.util.Log
import com.abook23.tv.dao.DaoMaster
import com.abook23.tv.dao.DaoSession
import com.abook23.tv.service.CacheVideoService
import com.abook23.tv.service.CacheVideoService.MyBinder
import com.android.easy.app.BaseApplication
import com.android.easy.base.spf.SharedPreferencesUtils
import com.android.easy.retrofit.RetrofitHttp
import java.util.*

/**
 * description:    描述
 * author:         yangxiong
 * e-mail:         abook23@163.com
 * createDate:     2020/11/30 16:09
 * updateUser:     更新者：
 * updateDate:     2020/11/30 16:09
 * updateRemark:   更新说明：
 * version:        1.0
 */
class App : BaseApplication() {
    companion object {

        private var cacheVideoService: CacheVideoService? = null

        private lateinit var daoSession: DaoSession
        private lateinit var app: App

        @JvmStatic
        fun getDaoSession(): DaoSession {
            return daoSession
        }

        fun getContext(): Context {
            return app
        }

        fun getCacheVideoService(): CacheVideoService? {
            return cacheVideoService
        }
    }


    override fun onCreate() {
        super.onCreate()
        app = this
    }

    override fun onShouldInitApp() {
        val l = Date().time
        SharedPreferencesUtils.initialize(this)
        RetrofitHttp.init(this, URL.BASE_URL)
        initGreenDao()
        Log.d("Application", "onShouldInitApp: " + (Date().time - l) + "ms")
    }

    override fun onHandleInit() {
        startAppService()
    }

    private fun startAppService() {
        val intent = Intent(this, CacheVideoService::class.java)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
        bindService(Intent(this, CacheVideoService::class.java), connection, BIND_AUTO_CREATE)
    }

    /**
     * 初始化GreenDao,直接在Application中进行初始化操作
     */
    private fun initGreenDao() {
        val helper = DaoMaster.DevOpenHelper(this, "movie.db")
        val db: SQLiteDatabase = helper.getWritableDatabase()
//        val db: Database = helper.getEncryptedWritableDb("8888")//加密,变慢 400ms左右
        val daoMaster = DaoMaster(db)
        daoSession = daoMaster.newSession()
    }

    private val connection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            Log.d("ServiceConnection", "ServiceConnection")
            cacheVideoService = (service as MyBinder).service
        }

        override fun onServiceDisconnected(name: ComponentName) {}
    }


}