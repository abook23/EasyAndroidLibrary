package com.abook23.tv.ui.cache

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.abook23.tv.R
import com.abook23.tv.ben.CacheVideoBean
import com.abook23.tv.ben.MovieBen
import com.abook23.tv.databinding.ItemMovie12Binding
import com.abook23.tv.ui.cachesub.CacheSubActivity
import com.android.easy.app.base.BaseAppCompatListActivity
import com.android.easy.app.base.DefaultAppBar
import com.android.easy.dialog.EasyDialog
import com.chad.library.adapter.base.BaseViewHolder

class CacheActivity : BaseAppCompatListActivity<List<CacheVideoBean>, MovieBen>() {

    private lateinit var viewModel: CacheViewModel
    private var checkButtonVisibility = false
    private var movieManagerView: View? = null
    private var checkAll = false

    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, CacheActivity::class.java)
            context.startActivity(starter)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CacheViewModel::class.java)
        defaultAppBar.setTitle("视频缓存")
        defaultAppBar.setAppbarRightView("管理", object : View.OnClickListener {
            override fun onClick(v: View?) {
                checkButtonVisibility = !checkButtonVisibility
                viewModel.checkButtonVisibility.value = checkButtonVisibility
                if (checkButtonVisibility) {
                    movieManagerView?.visibility = View.VISIBLE
                } else {
                    movieManagerView?.visibility = View.GONE
                }
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

    }

    override fun onResponseData(m: List<CacheVideoBean>?): MutableList<MovieBen> {
        mPageSize = 9999
        return viewModel.getCacheListData()
    }

    override fun onBaseQuickAdapterConvert(helper: BaseViewHolder, item: MovieBen) {
        val binding = DataBindingUtil.bind<ItemMovie12Binding>(helper.itemView)!!
        binding.lifecycleOwner = this
        binding.movieBen = item
        binding.viewModel = viewModel

        binding.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                viewModel.checkSet.add(item)
            } else {
                viewModel.checkSet.remove(item)
            }
        }
        helper.itemView.setOnClickListener {
            CacheSubActivity.start(this, item)
        }
    }


    private fun addBottomView() {
        movieManagerView = addContentBottomView(R.layout.movie_manager)
        movieManagerView?.visibility = View.GONE
        val checkBoxAll = movieManagerView?.findViewById<CheckBox>(R.id.checkBox)
        val deleteView = movieManagerView?.findViewById<TextView>(R.id.deleteView)
        checkBoxAll?.setOnClickListener {
            checkAll = checkBoxAll.isChecked
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
                                viewModel.clearAllCacheMovie(mContext)
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
}