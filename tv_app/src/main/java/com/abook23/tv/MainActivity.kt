package com.abook23.tv

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTabHost
import androidx.viewpager.widget.ViewPager
import com.abook23.tv.ben.AppConfig
import com.abook23.tv.ben.AppVersion
import com.abook23.tv.ben.ResponseBen
import com.abook23.tv.ui.main.MainFragment1
import com.abook23.tv.ui.main.MainFragment2
import com.abook23.tv.ui.main.MainFragment3
import com.android.easy.app.HttpCall
import com.android.easy.app.base.BaseAppCompatActivity
import com.android.easy.app.fragment.TabLayoutFragment
import com.android.easy.app.fragment.UpgradeAppDialogFragment
import com.android.easy.base.adapter.base.BaseViewHolder
import com.android.easy.base.spf.SharedPreferencesUtils
import com.android.easy.base.tabhost.TabHostViewPage
import com.android.easy.base.tabhost.TabHostViewPagerAdapter
import com.android.easy.base.util.AndroidUtils
import com.android.easy.base.util.L
import java.util.*

class MainActivity : BaseAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main, false)
        clearTranslucentStatusBarHeight()
        var t = System.currentTimeMillis();
        initApp()
        L.d((System.currentTimeMillis() - t).toString() + "ms")
    }

    fun initApp() {
        val isInitAppData = SharedPreferencesUtils.getParam("initApp", false)
        if (isInitAppData) {
            val data = App.getDaoSession().appConfigDao.loadAll()
            initUI(data)
        }
        get(URL.appConfig, mapOf(), object : HttpCall<ResponseBen<List<AppConfig>>>() {
            override fun onSuccess(t: ResponseBen<List<AppConfig>>) {
                App.getDaoSession().appConfigDao.insertOrReplaceInTx(t.data)
                SharedPreferencesUtils.putParam("initApp", true)
                if (!isInitAppData) {//只做数据库更新，不更新UI
                    initUI(t.data)
                }
            }
        })
        get(URL.appVersion, mapOf(), object : HttpCall<ResponseBen<AppVersion>>() {
            override fun onSuccess(t: ResponseBen<AppVersion>) {
                val appVersion = t.data ?: return
                val versionCode = AndroidUtils.getVersionCode(context)
                if (versionCode < appVersion.version_code) {
                    UpgradeAppDialogFragment.newInstance(appVersion.url, appVersion.version_name, appVersion.content, appVersion.force_update == 1).show(supportFragmentManager, "UpgradeAppDialogFragment")
                }
            }
        })
    }

    fun initUI(data: List<AppConfig>) {
        val map1 = mutableMapOf<String, Fragment>()
        val map2 = mutableMapOf<String, Fragment>()
        data.forEach {
            if (it.type.toInt() == 0) {
                map1.put(it.name, MainFragment1.newInstance(it))
            }
            if (it.type.toInt() == 1) {
                map2.put(it.name, MainFragment2.newInstance(it))
            }
        }
        val statusBarHeight = getStatusBarHeight()

        val tabHostBeans = ArrayList<TabHostViewPage.TabHostBean>()
        tabHostBeans.add(TabHostViewPage.TabHostBean("首页", R.drawable.bg_table_host_0, TabLayoutFragment.newInstance(map1, statusBarHeight)))
        tabHostBeans.add(TabHostViewPage.TabHostBean("频道", R.drawable.bg_table_host_1, TabLayoutFragment.newInstance(map2, statusBarHeight)))
        tabHostBeans.add(TabHostViewPage.TabHostBean("我", R.drawable.bg_table_host_2, MainFragment3.newInstance()))

        val viewPager = findViewById<ViewPager>(R.id.view_pager)
        val fragmentTabHost = findViewById<FragmentTabHost>(R.id.tabs)
        val tabHostViewPage = TabHostViewPage<TabHostViewPage.TabHostBean>(this, fragmentTabHost, viewPager, tabHostBeans)
        tabHostViewPage.setAdapter(object : TabHostViewPagerAdapter<TabHostViewPage.TabHostBean>(this, R.layout.item_tablehost) {
            override fun convert(holder: BaseViewHolder, position: Int, item: TabHostViewPage.TabHostBean) {
                holder.setText(R.id.tv_tbhost, item.title)
                holder.setImageResource(R.id.iv_tbhost, item.resId)//如果是网络图片,继承TabHostBean 重写.
            }
        })
    }
}
