package com.abook23.tv.ui.cachesub

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.format.Formatter
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.abook23.tv.App
import com.abook23.tv.R
import com.abook23.tv.ben.CacheVideoBean
import com.abook23.tv.ben.MovieBen
import com.abook23.tv.databinding.ActivityCacheSub2Binding
import com.abook23.tv.databinding.ItemCacheVideoSubBinding
import com.abook23.tv.service.CacheVideoService
import com.abook23.tv.ui.PlayActivity
import com.abook23.tv.ui.movieinfo.MovieInfoActivity
import com.android.easy.app.base.BaseAppCompatListActivity
import com.android.easy.dialog.EasyDialog
import com.android.easy.play.DownloadVideoManager
import com.chad.library.adapter.base.BaseViewHolder
import java.io.File

class CacheSubActivity : BaseAppCompatListActivity<List<CacheVideoBean>, CacheVideoBean>() {

    private var checkButtonVisibility =false
    private var checkAll = false
    private var movieManagerView: View? = null

    lateinit var movieBen: MovieBen
    var cacheVideoService: CacheVideoService? = null
    lateinit var viewModel: CacheSubViewModel

    companion object {
        @JvmStatic
        fun start(context: Context, movieBen: MovieBen) {
            val starter = Intent(context, CacheSubActivity::class.java)
                    .putExtra("data", movieBen)
            context.startActivity(starter)
        }
    }

    //R.layout.activity_cache_sub2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        movieBen = intent.getSerializableExtra("data") as MovieBen
        val binding = DataBindingUtil.bind<ActivityCacheSub2Binding>(rootView)!!
        viewModel = ViewModelProvider(this).get(CacheSubViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        defaultAppBar.setTitle(movieBen.v_name)
        defaultAppBar.setAppbarRightView("管理", object : View.OnClickListener {
            override fun onClick(v: View?) {
                checkButtonVisibility = !checkButtonVisibility
                viewModel.checkButtonVisibility.value =checkButtonVisibility
                if (checkButtonVisibility) {
                    movieManagerView?.visibility = View.VISIBLE
                } else {
                    movieManagerView?.visibility = View.GONE
                }
            }
        })
        addBottomView()
        cacheVideoService = App.getCacheVideoService()
    }

    override fun getContentViewLayout(): ContentViewLayout {
        return object : ContentViewLayout {
            override fun getLayoutResId(): Int {
                return R.layout.activity_cache_sub2
            }

            override fun getRecyclerViewId(): Int {
                return R.id.recyclerView
            }

            override fun getSwipeRefreshLayoutId(): Int {
                return R.id.swipeRefreshLayout
            }
        }
    }

    fun onAddCacheMovieClick(view: View) {
        MovieInfoActivity.startActivity(this, movieBen)
    }

    override fun getItemLayout(): Int {
        return R.layout.item_cache_video_sub
    }

    override fun getApiUrl(): String? {
        return null
    }

    override fun setParams(params: MutableMap<String, Any>) {

    }

    override fun onResponseData(t: List<CacheVideoBean>?): MutableList<CacheVideoBean>? {
        mPageSize = 9999
        return viewModel.getCacheListData(movieBen.v_id)
    }

    override fun onBaseQuickAdapterConvert(helper: BaseViewHolder, item: CacheVideoBean) {
        val binding = DataBindingUtil.bind<ItemCacheVideoSubBinding>(helper.itemView)!!
        binding.lifecycleOwner = this
        binding.item = item
        binding.bytes = ""
        binding.vPic = movieBen.v_pic
        binding.viewModel = viewModel
//        binding.executePendingBindings();//加一行，问题解决

        helper.itemView.setOnClickListener {

            movieBen.isPlay = true
            App.getDaoSession().movieBenDao.insertOrReplace(movieBen)

            PlayActivity.starPlayActivity(this, viewModel.getPlayData(movieBen, item))
        }

        cacheVideoService?.addListener(item.url, object : DownloadVideoManager.Call {
            override fun onComplete(file: File) {
                item.download_complete = true
                binding.bytes = ""
                binding.item = item
            }

            override fun onProgress(progress: Long, max: Long, bytes: Long) {
                item.download_max = max
                item.download_progress = progress
                binding.item = item
                binding.bytes = Formatter.formatFileSize(mContext, bytes)+"/s"
            }

            override fun onStart(file: File, max: Long) {

            }
        })

        val checkBoxButton = helper.getView<CheckBox>(R.id.checkBoxButton)
        checkBoxButton.setOnClickListener {
            if (checkBoxButton.isChecked) {
                viewModel.checkSet.add(item)
            } else {
                viewModel.checkSet.remove(item)
            }
        }
    }

    override fun onAdapterViewDetachedFromWindow(holder: BaseViewHolder) {
        super.onAdapterViewDetachedFromWindow(holder)
        val position = holder.adapterPosition
        //try 避免 recycler 有头 有尾巴 等其他
        try {
            val data = listData[position]
            cacheVideoService?.removeListener(data.url)
        } catch (e: Exception) {
        }
    }


    /**
     * 添加底部 view
     */
    private fun addBottomView() {
        movieManagerView = addContentBottomView(R.layout.movie_manager)
        movieManagerView?.visibility = View.GONE
        val checkBox = movieManagerView?.findViewById<CheckBox>(R.id.checkBox)
        val deleteView = movieManagerView?.findViewById<TextView>(R.id.deleteView)
        checkBox?.setOnClickListener {
            checkAll = checkBox.isChecked
            viewModel.checked.value = checkAll
        }
        deleteView?.setOnClickListener {
            EasyDialog.Builder()
                    .title("提示")
                    .content("是否删除下载缓存")
                    .progress(true)
                    .positive("确定", object : EasyDialog.OnClickListener {
                        override fun onClick(dialog: EasyDialog, view: View) {
                            if (checkAll) {
                                viewModel.clearAllCacheMovie(mContext, movieBen)
                            } else {
                                viewModel.removeCacheMovie(mContext)
                            }
                            dialog.dismiss()
                            loadRequestData()
                        }
                    })
                    .build().show(supportFragmentManager)

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cacheVideoService?.clearListener()
    }
}