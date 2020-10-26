package com.abook23.tv.ui.main

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abook23.tv.R
import com.abook23.tv.URL
import com.abook23.tv.ben.AppConfig
import com.abook23.tv.ben.MovieBen
import com.abook23.tv.ben.ResponseBen
import com.abook23.tv.ben.TypeMovie
import com.abook23.tv.ui.MovieInfoActivity
import com.abook23.tv.ui.SearchActivity
import com.abook23.tv.util.RoundedCornersFitStart
import com.android.easy.app.HttpCall
import com.android.easy.app.base.BaseFragment
import com.android.easy.base.util.L
import com.android.easy.retrofit.ApiService
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.fragment_main2.*

class MainFragment2 : BaseFragment() {
    lateinit var adapter: Adapter
    lateinit var appConfig: AppConfig
    var typeId=-1

    companion object {
        @JvmStatic
        fun newInstance(appConfig: AppConfig): MainFragment2 {
            var fragment2 = MainFragment2()
            fragment2.appConfig = appConfig
            return fragment2
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_main2
    }

    override fun initView(rootView: View) {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        adapter = Adapter()
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
        adapter.setOnItemClickListener { adapter, view, position ->
            var data = adapter.getItem(position) as TypeMovie
            SearchActivity.starActivity(activity!!, false, typeId)
        }

        searchLinearLayout.setOnClickListener {
            toSearchActivity()
        }
        requestData()
    }

    override fun onVisibleLoadData() {

    }

    override fun onOneLoadData() {

    }

    override fun onDestroy() {
        super.onDestroy()
        ApiService.CACHE = false
    }

    fun toSearchActivity() {
        activity?.let { SearchActivity.starActivity(it, false, typeId, true) }
    }

    fun startToPlayDataActivity(movieBen: MovieBen) {
        MovieInfoActivity.starPlayActivity(activity!!, movieBen)
    }

    fun requestData() {
        ApiService.CACHE = true
        get(URL.movieListType, mapOf("type" to appConfig.id), object : HttpCall<ResponseBen<List<TypeMovie>>>() {
            override fun onSuccess(t: ResponseBen<List<TypeMovie>>) {
                typeId = t.data[0]?.typeId
                adapter.setNewData(t.data)
            }
        })
    }

    inner class Adapter : BaseQuickAdapter<TypeMovie, BaseViewHolder>(R.layout.item_recycler_view_h) {
        override fun convert(helper: BaseViewHolder, item: TypeMovie) {
            helper.setText(R.id.text, item.typeName)
            var adapterSub = AdapterSub()
            var recyclerView = helper.getView<RecyclerView>(R.id.recyclerView)
            recyclerView.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
            recyclerView.adapter = adapterSub
            adapterSub.setNewData(item.data)
            adapterSub.setOnItemClickListener { adapter, view, position ->
                val movieBen = adapter.getItem(position) as MovieBen
                startToPlayDataActivity(movieBen)
            }
        }

        inner class AdapterSub : BaseQuickAdapter<MovieBen, BaseViewHolder>(R.layout.item_movie_1) {
            override fun convert(helper: BaseViewHolder, item: MovieBen?) {
                helper.setText(R.id.score, String.format("%.1f%s", item?.v_score?.toFloat(), "分"))
                helper.setText(R.id.name, item?.v_name)
                helper.setText(R.id.text, item?.body)
                var imageView = helper.getView<ImageView>(R.id.imageView)
                Glide.with(mContext).load(item?.v_pic)
                        .apply(RequestOptions.bitmapTransform(RoundedCornersFitStart(16))) //圆角半径
                        .into(imageView)
            }

        }
    }
}
