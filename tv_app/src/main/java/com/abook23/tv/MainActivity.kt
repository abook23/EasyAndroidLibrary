package com.abook23.tv

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
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
import com.android.easy.base.spf.SharedPreferencesUtils
import com.android.easy.base.util.AndroidUtils
import com.google.android.material.tabs.TabLayout

class MainActivity : BaseAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main, false)
        clearTranslucentStatusBarHeight()
        initApp()
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
        val list1 = mutableListOf<TabLayoutFragment.TabLayoutItem>()
        val list2 = mutableListOf<TabLayoutFragment.TabLayoutItem>()
        data.forEach {
            if (it.type.toInt() == 0) {
                list1.add(TabLayoutFragment.TabLayoutItem(it.name, 0, MainFragment1.newInstance(it)))
            }
            if (it.type.toInt() == 1) {
                list2.add(TabLayoutFragment.TabLayoutItem(it.name, 0, MainFragment2.newInstance(it)))
            }
        }
        val statusBarHeight = getStatusBarHeight()

        val datas = ArrayList<TabLayoutFragment.TabLayoutItem>()
        datas.add(TabLayoutFragment.TabLayoutItem("首页", R.drawable.bg_table_host_0, getSubTableLayoutFragment(list1)))
        datas.add(TabLayoutFragment.TabLayoutItem("频道", R.drawable.bg_table_host_1, getSubTableLayoutFragment(list2)))
        datas.add(TabLayoutFragment.TabLayoutItem("我", R.drawable.bg_table_host_2, MainFragment3.newInstance()))
        supportFragmentManager.beginTransaction().add(R.id.main_fragment, getTableLayoutFragment(datas)).commit()
    }

    private fun getTableLayoutFragment(data: List<TabLayoutFragment.TabLayoutItem>): Fragment {
        val tabLayoutFragment = TabLayoutFragment.newInstance(
                R.layout.fragment_tab_layout, R.id.tabLayout, R.id.viewPager
        )
        tabLayoutFragment.setTabMode(TabLayoutFragment.MODE_FIXED)
        tabLayoutFragment.addFragments(context, R.layout.tab_layout_item, data, object : TabLayoutFragment.Call {
            override fun convert(tabLayoutItemView: View, item: TabLayoutFragment.TabLayoutItem, position: Int) {
                tabLayoutItemView.findViewById<TextView>(R.id.tab_layout_item_tv).text = item.name
                tabLayoutItemView.findViewById<ImageView>(R.id.tab_layout_item_iv).setImageResource(item.resId)
            }
        })
        return tabLayoutFragment
    }

    private fun getSubTableLayoutFragment(data: List<TabLayoutFragment.TabLayoutItem>): Fragment {
        val tabLayoutFragment = TabLayoutFragment.newInstance(
                R.layout.fragment_tab_layout_top, R.id.tabLayout, R.id.viewPager
        )
        tabLayoutFragment.setTabMode(TabLayoutFragment.MODE_SCROLLABLE)
        tabLayoutFragment.addFragments(context, R.layout.tab_layout_top_item, data, object : TabLayoutFragment.Call {
            override fun convert(tabLayoutItemView: View, item: TabLayoutFragment.TabLayoutItem, position: Int) {
                tabLayoutItemView.findViewById<TextView>(R.id.tab_layout_item_tv).text = item.name
                tabLayoutItemView.findViewById<TextView>(R.id.tab_layout_item_tv_select).text = item.name
            }
        })
        tabLayoutFragment.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {

            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
                p0?.customView?.findViewById<View>(R.id.tab_layout_item_tv)?.visibility=View.VISIBLE
                p0?.customView?.findViewById<View>(R.id.tab_layout_item_tv_select)?.visibility=View.GONE
            }

            override fun onTabSelected(p0: TabLayout.Tab?) {
                p0?.customView?.findViewById<View>(R.id.tab_layout_item_tv_select)?.visibility=View.VISIBLE
                p0?.customView?.findViewById<View>(R.id.tab_layout_item_tv)?.visibility=View.GONE
            }
        })
        return tabLayoutFragment
    }
}
