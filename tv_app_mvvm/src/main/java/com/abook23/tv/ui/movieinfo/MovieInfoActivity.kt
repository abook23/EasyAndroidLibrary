package com.abook23.tv.ui.movieinfo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.abook23.tv.App
import com.abook23.tv.R
import com.abook23.tv.ben.MovieBen
import com.abook23.tv.databinding.ActivityMovieInfoBinding
import com.abook23.tv.databinding.ItemMovie12Binding
import com.abook23.tv.ui.PlayActivity
import com.abook23.tv.ui.cache.AddCacheDialogFragment
import com.android.easy.app.base.BaseAppCompatActivity
import com.android.easy.play.VideoFragment
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class MovieInfoActivity : BaseAppCompatActivity() {

    lateinit var movieBen: MovieBen
    lateinit var viewModel: MovieInfoViewModel

    companion object {
        fun startActivity(activity: Activity, movieBen: MovieBen) {
            var intent = Intent(activity, MovieInfoActivity::class.java)
            intent.putExtra("data", movieBen)
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_info, false)

        movieBen = intent.getSerializableExtra("data") as MovieBen

        val binding = DataBindingUtil.bind<ActivityMovieInfoBinding>(rootView)!!
        viewModel = ViewModelProvider(this).get(MovieInfoViewModel::class.java)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val todAdapter = TodAdapter()
        binding.recyclerViewTod.adapter = todAdapter
        binding.recyclerView1.adapter = Adapter()

        binding.movieBen = movieBen
        viewModel.requestMovieInfoData(movieBen.v_id)
        viewModel.loadRecommendData(movieBen.tid)


        todAdapter.setOnItemClickListener { adapter, view, position ->
            val url = adapter.getItem(position) as String
            val playData = viewModel.getPlayData(movieBen, url)
            PlayActivity.starPlayActivity(this, playData)

            movieBen.isPlay = true
            App.getDaoSession().movieBenDao.insertOrReplaceInTx(movieBen)
        }

        var _movieBen = App.getDaoSession().movieBenDao.load(movieBen.v_id)
        if(_movieBen!=null){
            viewModel.collect.value = _movieBen.isCollect
        }
    }

    fun onCollectClick(view: View) {
        movieBen.isCollect = !movieBen.isCollect
        viewModel.collect.value = movieBen.isCollect
        App.getDaoSession().movieBenDao.insertOrReplaceInTx(movieBen)
    }

    fun onCacheClick(view: View) {
        if (viewModel.todLiveData.value==null){
            return
        }
        var _movieBen = App.getDaoSession().movieBenDao.load(movieBen.v_id)
        if(_movieBen==null){
            App.getDaoSession().movieBenDao.insert(movieBen)
        }

        val addCacheFragment = AddCacheDialogFragment.newInstance(viewModel.getPlayData(movieBen, ""))
        addCacheFragment.show(supportFragmentManager, "addCacheFragment")
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
            val binding = DataBindingUtil.bind<ItemMovie12Binding>(helper.itemView)
            binding?.movieBen = item
            helper.itemView.setOnClickListener {
                startActivity(this@MovieInfoActivity, item)
            }
        }
    }

}