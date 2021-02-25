package com.abook23.tv.ui.cache

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.abook23.tv.App
import com.abook23.tv.R
import com.abook23.tv.ben.PlayData
import com.abook23.tv.service.CacheVideoService
import com.android.easy.play.DownloadVideoManager
import com.android.easy.play.MovieInfo
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.fragment_add_cache.*
import java.io.File

class AddCacheDialogFragment : DialogFragment() {
    private var cacheVideoService: CacheVideoService? = null
    lateinit var playData: PlayData

    companion object {
        fun newInstance(playData: PlayData): AddCacheDialogFragment {
            return AddCacheDialogFragment().apply {
                this.playData = playData
            }
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        setStyle(STYLE_NO_TITLE, 0)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_cache, container, false)
    }

    override fun onStart() {
        super.onStart()
        // 设置宽度为屏宽, 靠近屏幕底部。
        val window = dialog!!.window
        // 一定要设置Background，如果不设置，window属性设置无效
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val dm = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(dm)
        val params = window.attributes
        params.gravity = Gravity.BOTTOM
        // 使用ViewGroup.LayoutParams，以便Dialog 宽度充满整个屏幕
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
        window.attributes = params
//        window.setDimAmount(0f);//覆盖成透明度
//        dialog!!.setCanceledOnTouchOutside(false)
//        dialog!!.setCancelable(true)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
    }

    fun initView(rootView: View) {
        cacheVideoService = App.getCacheVideoService()
        val adapter = Adapter(playData.movieInfos)
        recyclerView.layoutManager = GridLayoutManager(context, 5)
        recyclerView.adapter = adapter
        adapter.setOnItemClickListener { adapter, view, position ->
            view.findViewById<TextView>(R.id.text).setTextColor(ContextCompat.getColor(context!!,R.color.blue))
            cacheVideoService?.download(playData.movieInfos[position].url, playData.videoId, position)
        }
    }

    class Adapter(val datas: List<MovieInfo>) : BaseQuickAdapter<MovieInfo, BaseViewHolder>(R.layout.item_cache_tod, datas) {
        private var cacheVideoService: CacheVideoService? = null

        fun setCacheVideoService(cacheVideoService: CacheVideoService?) {
            this.cacheVideoService = cacheVideoService;
        }

        override fun convert(helper: BaseViewHolder, item: MovieInfo) {
            val textView = helper.getView<TextView>(R.id.text)
            val progressBar = helper.getView<ProgressBar>(R.id.progressBar)

            textView.text = "${(datas[helper.adapterPosition].num)}".toString()
            var cacheVideoBean = App.getDaoSession().cacheVideoBeanDao.load(item.url)
            if (cacheVideoBean != null) {
                progressBar.visibility = View.VISIBLE
                progressBar.max = cacheVideoBean.download_max.toInt()
                progressBar.progress = cacheVideoBean.download_progress.toInt()
                textView.setTextColor(ContextCompat.getColor(mContext,R.color.blue))
            } else {
                progressBar.visibility = View.GONE
                textView.setTextColor(ContextCompat.getColor(mContext,R.color.white))
            }
            cacheVideoService?.addListener(item.url, object : DownloadVideoManager.Call {
                override fun onComplete(file: File) {

                }

                override fun onProgress(progress: Long, max: Long,bytes:Long) {
                    progressBar.progress = progress.toInt()
                    progressBar.progress = max.toInt()
                }

                override fun onStart(file: File?, max: Long) {
                    progressBar.visibility = View.VISIBLE
                }

            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cacheVideoService?.clearListener()
    }
}