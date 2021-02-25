package com.abook23.tv

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.abook23.tv.ben.AppConfig
import com.abook23.tv.ui.main.me.MeFragment
import com.abook23.tv.ui.test.MainViewModel
import com.abook23.tv.ui.main.movie.MovieFragment
import com.android.easy.app.base.BaseAppCompatActivity
import com.android.easy.app.fragment.TabLayoutFragment
import com.android.easy.app.fragment.UpgradeAppDialogFragment
import com.google.android.material.tabs.TabLayout
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : BaseAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        appBar.hideAppBar()
        setTranslucentStatus()
        initData()
    }

    private fun initData() {

        //mvvm是一种架构 , dataBinding 和 liveData 是 一种设计模式, 别混淆
        val viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.appConfigLiveData.observe(this, Observer {
            initUI(it)
        })
        viewModel.appVersionLiveData.observe(this, Observer {
            val appVersion = it
            UpgradeAppDialogFragment.newInstance(appVersion.url, appVersion.version_name, appVersion.content,
                    appVersion.force_update == 1).show(supportFragmentManager, "UpgradeAppDialogFragment")
        })
        viewModel.initApp()

    }

    //主界面及分类
    private fun initUI(data: List<AppConfig>) {
        val fragment = getTableLayoutFragment(data)
        supportFragmentManager.beginTransaction().add(R.id.main_fragment, fragment).commit()
    }

    //主tabLayout
    private fun getTableLayoutFragment(data: List<AppConfig>): Fragment {

        val list1 = mutableListOf<TabLayoutFragment.TabLayoutItem>()
        val list2 = mutableListOf<TabLayoutFragment.TabLayoutItem>()
        data.forEach {
            if (it.type.toInt() == 0) {
                list1.add(TabLayoutFragment.TabLayoutItem(it.name, 0, MovieFragment.newInstance(it)))
            }
            if (it.type.toInt() == 1) {
                list2.add(TabLayoutFragment.TabLayoutItem(it.name, 0, MovieFragment.newInstance(it)))
            }
        }

        //主tabLayout
        val itemList = ArrayList<TabLayoutFragment.TabLayoutItem>()
        itemList.add(TabLayoutFragment.TabLayoutItem("首页", R.drawable.bg_table_host_0, getSubTableLayoutFragment(list1)))
        itemList.add(TabLayoutFragment.TabLayoutItem("频道", R.drawable.bg_table_host_1, getSubTableLayoutFragment(list2)))
        itemList.add(TabLayoutFragment.TabLayoutItem("我", R.drawable.bg_table_host_2, MeFragment.newInstance()))

        val tabLayoutFragment = TabLayoutFragment.newInstance(R.layout.fragment_tab_layout, R.id.tabLayout, R.id.viewPager)
        tabLayoutFragment.setTabMode(TabLayoutFragment.MODE_FIXED)
        tabLayoutFragment.addFragments(context, R.layout.tab_layout_item, itemList, object : TabLayoutFragment.Call {
            override fun convert(tabLayoutItemView: View, item: TabLayoutFragment.TabLayoutItem, position: Int) {
                tabLayoutItemView.findViewById<TextView>(R.id.tab_layout_item_tv).text = item.name
                tabLayoutItemView.findViewById<ImageView>(R.id.tab_layout_item_iv).setImageResource(item.resId)
            }
        })
        return tabLayoutFragment
    }

    //次tabLayout
    private fun getSubTableLayoutFragment(data: List<TabLayoutFragment.TabLayoutItem>): Fragment {
        val tabLayoutFragment = TabLayoutFragment.newInstance(R.layout.fragment_tab_layout_top, R.id.tabLayout, R.id.viewPager)
        tabLayoutFragment.setTabMode(TabLayoutFragment.MODE_SCROLLABLE)
        tabLayoutFragment.addFragments(context, R.layout.tab_layout_top_item, data, object : TabLayoutFragment.Call {
            override fun convert(tabLayoutItemView: View, item: TabLayoutFragment.TabLayoutItem, position: Int) {
                tabLayoutItemView.findViewById<TextView>(R.id.tab_layout_item_tv).text = item.name
            }
        })
        tabLayoutFragment.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab) {

            }

            override fun onTabUnselected(p0: TabLayout.Tab) {
                p0.customView?.findViewById<TextView>(R.id.tab_layout_item_tv)?.textSize = 18f
            }

            override fun onTabSelected(p0: TabLayout.Tab) {
                p0.customView?.findViewById<TextView>(R.id.tab_layout_item_tv)?.textSize = 26f
            }
        })
        return tabLayoutFragment
    }

    var onBackPressedTime: Long = 0
    override fun onBackPressed() {
        val nowTime = Date().time
        if (nowTime - onBackPressedTime > 2000) {
            onBackPressedTime = nowTime
            Toast.makeText(this, "再按一次退出", Toast.LENGTH_LONG).show()
            return
        }
        super.onBackPressed()
    }
}