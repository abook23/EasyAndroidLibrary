package com.abook23.tv.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.abook23.tv.App
import com.abook23.tv.R
import com.abook23.tv.URL
import com.abook23.tv.ben.MovieBen
import com.abook23.tv.ben.PlayData
import com.abook23.tv.ben.PlayDataBen
import com.abook23.tv.ben.ResponseBen
import com.abook23.tv.ui.cache.AddCacheDialogFragment
import com.abook23.tv.util.RoundedCornersFitStart
import com.abook23.tv.util.SeaDataUtils
import com.android.easy.app.HttpCall
import com.android.easy.app.base.BaseAppCompatActivity
import com.android.easy.play.MovieInfo
import com.android.easy.play.VideoFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.activity_movie_info.*

class MovieInfoActivity : BaseAppCompatActivity() {

    var playDataBen: PlayDataBen? = null
    lateinit var movieBen: MovieBen
    var urls: ArrayList<String> = ArrayList()

    lateinit var todAdapter: TodAdapter

    companion object {
        fun starPlayActivity(activity: Activity, movieBen: MovieBen) {
            var intent = Intent(activity, MovieInfoActivity::class.java)
            intent.putExtra("data", movieBen)
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_info, false)
        movieBen = intent.getSerializableExtra("data") as MovieBen
        loadData()
    }

    private fun loadData() {
        requestData(movieBen.v_id)

        todCountText.text = movieBen.v_note
        name.text = movieBen.v_name
        score.text = String.format("%.1f%s", movieBen.v_score.toFloat(), "分")
        textView2.text = movieBen.v_publishyear.toString() + "/" + movieBen.v_publisharea + "/" + movieBen.tname
        v_actor.text = movieBen.v_actor
        content.text = movieBen.body
        Glide.with(this)
                .load(movieBen.v_pic)
                .apply(RequestOptions.bitmapTransform(RoundedCornersFitStart(16))) //圆角半径
//                .apply(RequestOptions.bitmapTransform(RoundedCorners(16))) //圆角半径
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .skipMemoryCache(true)
                .into(imageView)

        todAdapter = TodAdapter()
        recyclerView0.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView0.adapter = todAdapter
        todAdapter.setOnItemClickListener { adapter, view, position ->
            val url = adapter.getItem(position) as String
            val playData = getPlayData(url)
            PlayActivity.starPlayActivity(this, playData)
            movieBen.isPlay = true
            App.getDaoSession().movieBenDao.insertOrReplaceInTx(movieBen)
            collectData(1)//用户播放
        }
        collectData(0)//用户查看

        var playMovie = App.getDaoSession().movieBenDao.load(movieBen.v_id)
        if (playMovie != null && playMovie.isCollect) {
            collectImageView.setImageResource(R.mipmap.icon_collect1)
        }
    }

    fun getPlayData(url: String): PlayData {
        val list = ArrayList<MovieInfo>()
        for (index in urls.indices) {
            list.add(MovieInfo("第${index}集", urls[index], index + 1))
        }
        return PlayData(movieBen.v_id, movieBen.v_name, url, list)
    }

    /**
     * 数据收集
     */
    private fun collectData(type: Int) {
        post(URL.collectData, mapOf("vid" to movieBen.v_id, "type" to type), object : HttpCall<ResponseBen<String>>() {
            override fun onSuccess(t: ResponseBen<String>) {
            }
        })
    }

    private fun requestData(vid: Long) {
        get(URL.playData, mapOf("id" to vid), object : HttpCall<ResponseBen<PlayDataBen>>() {
            override fun onSuccess(t: ResponseBen<PlayDataBen>) {
                urls.clear()
                playDataBen = t.data
                var urlMap = SeaDataUtils.formatBody(playDataBen?.body)
                urlMap.forEach {
                    if (it.key.contains("m3u8")) {
                        var m3u8url = it.value
                        m3u8url.forEach {
                            urls.add(it[1])
                        }
                        todAdapter.setNewData(urls)
                        return
                    }
                }
            }
        })

        loadRecommendData()
    }

    fun loadRecommendData() {
        var adapter = Adapter()
        recyclerView1.layoutManager = LinearLayoutManager(mContext)
        recyclerView1.adapter = adapter;
        get(URL.movieList, mapOf("tid" to movieBen.tid, "start" to 0, "num" to 8), object : HttpCall<ResponseBen<List<MovieBen>>>() {
            override fun onSuccess(t: ResponseBen<List<MovieBen>>) {
                adapter.setNewData(t.data)
                adapter.loadMoreEnd()
            }
        })
    }

    inner class TodAdapter : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_tod) {
        val sharedPreferences = VideoFragment.getSharedPreferences(applicationContext)
        override fun convert(helper: BaseViewHolder, item: String?) {
            helper.setText(R.id.text, (helper.layoutPosition + 1).toString())
            if (sharedPreferences.contains(item)) {
                helper.setTextColor(R.id.text, resources.getColor(R.color.blue))
            } else {
                helper.setTextColor(R.id.text, resources.getColor(R.color.white))
            }
        }
    }

    inner class Adapter : BaseQuickAdapter<MovieBen, BaseViewHolder>(R.layout.item_movie_1_2) {
        override fun convert(helper: BaseViewHolder, item: MovieBen) {
            helper.getView<TextView>(R.id.name).text = item.v_name
            helper.getView<TextView>(R.id.score).text = String.format("%.1f", item.v_score.toFloat())
            helper.getView<TextView>(R.id.textView2).text = "${item.v_publishyear}/${item.v_publisharea}/${item.tname}"
            helper.getView<TextView>(R.id.v_actor).text = item.v_actor
            helper.getView<TextView>(R.id.content).text = item.body
            val imageView = helper.getView<ImageView>(R.id.imageView)
            Glide.with(mContext)
                    .load(item.v_pic)
                    .into(imageView)
            helper.itemView.setOnClickListener {
                movieBen = item
                loadData()
            }
        }
    }

    fun onCollectClick(view: View) {
        movieBen.isCollect = !movieBen.isCollect
        if (movieBen.isCollect) {
            collectImageView.setImageResource(R.mipmap.icon_collect1)
        } else {
            collectImageView.setImageResource(R.mipmap.icon_collect)
        }
        App.getDaoSession().movieBenDao.insertOrReplaceInTx(movieBen)
    }

    fun onCacheClick(view: View) {
        val addCacheFragment = AddCacheDialogFragment.newInstance(getPlayData(""))
        addCacheFragment.show(supportFragmentManager, "addCacheFragment")
    }
}
