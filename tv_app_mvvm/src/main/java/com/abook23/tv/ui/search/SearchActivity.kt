package com.abook23.tv.ui.search

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.abook23.tv.R
import com.abook23.tv.ben.MovieBen
import com.abook23.tv.databinding.ActivitySearchBinding
import com.abook23.tv.databinding.ItemMovie12Binding
import com.abook23.tv.ui.movieinfo.MovieInfoActivity
import com.android.easy.app.base.BaseAppCompatActivity
import com.android.easy.base.util.SoftInputMethodUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class SearchActivity : BaseAppCompatActivity() {

    companion object {
        fun starActivity(activity: Activity, local: Boolean, tid: Int) {
            starActivity(activity, local, tid, false)
        }

        fun starActivity(activity: Activity, local: Boolean, tid: Int, showSearchInput: Boolean) {
            var intent = Intent(activity, SearchActivity::class.java)
            intent.putExtra("tid", tid)
            intent.putExtra("local", local)
            intent.putExtra("showSearchInput", showSearchInput)
            activity.startActivity(intent)
        }

        fun starActivity(activity: Activity, local: Boolean, tid: Int, showSearchInput: Boolean, state: Int/*1:播放记录，2收藏*/) {
            var intent = Intent(activity, SearchActivity::class.java)
            intent.putExtra("tid", tid)
            intent.putExtra("local", local)
            intent.putExtra("showSearchInput", showSearchInput)
            intent.putExtra("state", state)
            activity.startActivity(intent)
        }
    }

    private var tid: Int = -1
    var isLocalData = false
    var showSearchInput = false
    lateinit var viewModel:SearchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search, false)
        isLocalData = intent.getBooleanExtra("local", false)
        showSearchInput = intent.getBooleanExtra("showSearchInput", false)
        val binding = DataBindingUtil.bind<ActivitySearchBinding>(rootView)!!
//        val binding = DataBindingUtil.setContentView<ActivitySearchBinding>(this, R.layout.activity_search)
        binding.lifecycleOwner = this
        viewModel = ViewModelProvider(this).get(SearchViewModel::class.java)
        binding.viewMode = viewModel
        tid = intent.getIntExtra("tid", -1);

        val adapter = Adapter()
        val recyclerView = binding.recyclerView
//        recyclerView.layoutManager = LinearLayoutManager(this) 在xml 设置,可以不用写
        recyclerView.adapter = adapter

        binding.searchEditText.doOnTextChanged { text, start, before, count ->
            viewModel.requestNetworkData(0, tid, text.toString())

            //用于是否显示view 的绑定
            //android:visibility="@{viewMode.clearViewVisibility?View.VISIBLE:View.GONE}"
            viewModel.clearViewVisibility.value = count > 0
        }

        binding.clearImageView.setOnClickListener {
            binding.searchEditText.setText("")
            requestData(0, "")
        }

        adapter.setOnLoadMoreListener({
            requestData(viewModel.mPage, binding.searchEditText.text.toString())
        }, recyclerView)

        requestData(0, "")
        if (showSearchInput) {
            SoftInputMethodUtils.openSoftInput(binding.searchEditText)
        }
    }

    fun requestData(page: Int, searchVal: String) {
        if (isLocalData) {
            var state = intent.getIntExtra("state", -1)
            viewModel.requestLocalData(state,searchVal)
        } else {
            viewModel.requestNetworkData(page, tid,searchVal)
        }
    }

    inner class Adapter : BaseQuickAdapter<MovieBen, BaseViewHolder>(R.layout.item_movie_1_2) {
        override fun convert(helper: BaseViewHolder, item: MovieBen) {
            val binding = DataBindingUtil.bind<ItemMovie12Binding>(helper.itemView)
            binding?.movieBen = item
            binding?.root?.setOnClickListener {
                MovieInfoActivity.startActivity(this@SearchActivity, item)
            }
        }
    }

}