package com.abook23.tv.ui.cache

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.abook23.tv.App
import com.abook23.tv.R
import com.abook23.tv.ben.MovieBen
import com.abook23.tv.dao.CacheVideoBeanDao
import com.abook23.tv.util.RoundedCornersFitStart
import com.android.easy.app.base.BaseAppCompatListActivity
import com.android.easy.base.util.AndroidUtils
import com.android.easy.dialog.EasyDialog
import com.android.easy.play.DownloadVideoManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseViewHolder
import java.io.File

class CacheActivity : BaseAppCompatListActivity<List<MovieBen>, MovieBen>() {
    private var checkButtonVisibility = false
    private var checkAll = false
    private var checkSet = HashSet<MovieBen>()
    private var movieManagerView: View? = null

    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, CacheActivity::class.java)
            context.startActivity(starter)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        defaultAppBar.setTitle("视频缓存")
        defaultAppBar.setAppbarRightView("管理", object : View.OnClickListener {
            override fun onClick(v: View?) {
                checkButtonVisibility = !checkButtonVisibility
                if (checkButtonVisibility) {
                    movieManagerView?.visibility = View.VISIBLE
                } else {
                    movieManagerView?.visibility = View.GONE
                }
                notifyDataSetChanged()
            }
        })
        addBottomView()
    }

    override fun getItemLayout(): Int {
        return R.layout.item_movie_1_2
    }

    override fun getApiUrl(): String? {
        return null
    }

    override fun setParams(params: MutableMap<String, Any>) {
        mPageSize = 100000//设置加载很多,下拉加载更多
    }

    override fun onResponseData(t: List<MovieBen>?): MutableList<MovieBen>? {
        var vids = ""
        var cacheVideoBeanList = App.getDaoSession().cacheVideoBeanDao.loadAll()
        cacheVideoBeanList.forEach {
            vids += it.v_id.toString() + ","
        }
        if (vids.isNotEmpty()) {
            var  where = "where _id in (${vids.substring(0, vids.length - 1)}) "
            return App.getDaoSession().movieBenDao.queryRaw(where)
        }
        return ArrayList()
    }

    override fun onBaseQuickAdapterConvert(helper: BaseViewHolder, item: MovieBen) {
        helper.getView<TextView>(R.id.name).text = item.v_name
        helper.getView<TextView>(R.id.score).text = String.format("%.1f", item.v_score.toFloat())
        helper.getView<TextView>(R.id.textView2).text = "${item.v_publishyear}/${item.v_publisharea}/${item.tname}"
        helper.getView<TextView>(R.id.v_actor).text = item.v_actor
        helper.getView<TextView>(R.id.content).text = item.body
        val imageView = helper.getView<ImageView>(R.id.imageView)
        Glide.with(mContext)
                .load(item.v_pic)
                .apply(RequestOptions.bitmapTransform(RoundedCornersFitStart(16))) //圆角半径
                .into(imageView)
        val checkBoxButton = helper.getView<CheckBox>(R.id.checkBoxButton)
        checkBoxButton.setOnClickListener {
            if (checkBoxButton.isChecked) {
                checkSet.add(item)
            } else {
                checkSet.remove(item)
            }
        }
        if (checkButtonVisibility) {
            checkBoxButton.visibility = View.VISIBLE
        } else {
            checkBoxButton.visibility = View.GONE
        }
        checkBoxButton.isChecked = checkAll

        helper.itemView.setOnClickListener {
            CacheSub2Activity.start(context, item)
        }
    }

    /**
     * 添加底部 view
     */
    private fun addBottomView() {
        movieManagerView = addContentBottomView(R.layout.movie_manager)
        movieManagerView?.visibility = View.GONE
        var checkBox = movieManagerView?.findViewById<CheckBox>(R.id.checkBox)
        var deleteView = movieManagerView?.findViewById<TextView>(R.id.deleteView)
        checkBox?.setOnClickListener {
            checkAll = checkBox.isChecked
            notifyDataSetChanged()
        }
        deleteView?.setOnClickListener {
            EasyDialog.Builder()
                    .title("提示")
                    .content("是否删除下载缓存")
                    .progress(true)
                    .positive("确定", object : EasyDialog.OnClickListener {
                        override fun onClick(dialog: EasyDialog, view: View) {
                            deleteCache()
                            dialog.dismiss()
                        }
                    })
                    .build().show(supportFragmentManager)

        }
    }

    fun deleteCache() {
        if (checkAll) {
            App.getDaoSession().cacheVideoBeanDao.deleteAll()
            AndroidUtils.deleteFiles(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES))
        } else {
            checkSet.forEach {
                val cacheVideoBeans = App.getDaoSession().cacheVideoBeanDao.queryBuilder()
                        .where(CacheVideoBeanDao.Properties.V_id.eq(it.v_id)).list()
                cacheVideoBeans.forEach {
                    App.getDaoSession().cacheVideoBeanDao.delete(it)
                    val videoPath = DownloadVideoManager.getCacheLocalPath(context, it.url)
                    AndroidUtils.deleteFiles(File(videoPath))
                }
            }
        }
        loadRequestData()
    }
}