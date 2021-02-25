package com.abook23.tv.ui.main

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.abook23.tv.App
import com.abook23.tv.R
import com.abook23.tv.ben.MovieBen
import com.abook23.tv.dao.MovieBenDao
import com.abook23.tv.ui.cache.CacheActivity
import com.abook23.tv.ui.MovieInfoActivity
import com.abook23.tv.ui.SearchActivity
import com.abook23.tv.util.RoundedCornersFitStart
import com.android.easy.app.base.BaseFragment
import com.android.easy.base.util.AndroidUtils
import com.android.easy.dialog.EasyDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.fragment_main3.*

class MainFragment3 : BaseFragment() {
    var adapter1: Adapter1? = null
    var typeArray= arrayListOf<Array<Any>>(
            arrayOf("观看历史", R.mipmap.icon_history),
            arrayOf("我的收藏", R.mipmap.icon_collect),
            arrayOf("离线下载", R.mipmap.icon_cache),
            arrayOf("缓存清理", R.mipmap.icon_clear_cache)
    )

    companion object {
        fun newInstance() = MainFragment3()
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_main3
    }

    override fun initView(rootView: View) {
        val adapter0 = Adapter0()
        recyclerView0.layoutManager = GridLayoutManager(context, 4)
        recyclerView0.adapter = adapter0
        adapter0.setNewData(typeArray)

        adapter1 = Adapter1()
        recyclerView1.layoutManager = LinearLayoutManager(context)
        recyclerView1.adapter = adapter1

        adapter0.setOnItemClickListener { adapter, view, position ->
            if (position==0 || position== 1){
                SearchActivity.starActivity(activity!!, true, -1, false, position + 1)
            }else if (position==2){
                CacheActivity.start(context!!)
            }else if(position==3){
                val cacheSize = AndroidUtils.selectCacheSize(context,false)
                EasyDialog.Builder()
                        .title("缓存清理")
                        .content("当前缓存:$cacheSize")
                        .positive("确认",EasyDialog.OnClickListener { dialog, view ->
                            dialog.dismiss()
                            AndroidUtils.selectCacheSize(context,true)
                        })
                        .build()
                        .show(childFragmentManager)
            }
        }
        adapter1?.setOnItemClickListener { adapter, view, position ->
            val movieBen = adapter1?.getItem(position)
            MovieInfoActivity.starPlayActivity(activity!!, movieBen!!)
        }
        getMovieHistroy()
    }

    fun getMovieHistroy() {
        if (adapter1 != null) {
            var movieList = App.getDaoSession().movieBenDao.queryBuilder()
                    .where(MovieBenDao.Properties.IsPlay.eq(true)).limit(10).list()
            adapter1?.setNewData(movieList)
        }
    }

    override fun onVisibleLoadData() {

    }

    override fun onOneLoadData() {
        getMovieHistroy()
    }

    inner class Adapter0 : BaseQuickAdapter<Array<Any>, BaseViewHolder>(R.layout.item_type_0) {
        override fun convert(helper: BaseViewHolder, item: Array<Any>) {
            helper.setText(R.id.text, item[0].toString())
            helper.getView<ImageView>(R.id.imageView).setImageResource(item[1].toString().toInt())
        }
    }

    inner class Adapter1 : BaseQuickAdapter<MovieBen, BaseViewHolder>(R.layout.item_movie_1_2) {
        override fun convert(helper: BaseViewHolder, item: MovieBen) {
            helper.getView<TextView>(R.id.name).text = item.v_name
            helper.getView<TextView>(R.id.score).text = item.v_score.toString()
            helper.getView<TextView>(R.id.textView2).text = "${item.v_publishyear}/${item.v_publisharea}/${item.tname}"
            helper.getView<TextView>(R.id.v_actor).text = item.v_actor
            helper.getView<TextView>(R.id.content).text = item.body
            val imageView = helper.getView<ImageView>(R.id.imageView)
            Glide.with(mContext)
                    .load(item.v_pic)
                    .apply(RequestOptions.bitmapTransform(RoundedCornersFitStart(16))) //圆角半径
                    .into(imageView)
        }
    }

}
