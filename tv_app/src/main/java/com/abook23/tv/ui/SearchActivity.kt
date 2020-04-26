package com.abook23.tv.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.abook23.tv.App
import com.abook23.tv.R
import com.abook23.tv.URL
import com.abook23.tv.ben.MovieBen
import com.abook23.tv.ben.ResponseBen
import com.abook23.tv.dao.MovieBenDao
import com.android.easy.app.HttpCall
import com.android.easy.app.base.BaseAppCompatActivity
import com.android.easy.base.util.SoftInputMethodUtils
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.activity_search.*

/**
 *@author abook23@163.com
 * 2019/12/08
 */
class SearchActivity : BaseAppCompatActivity() {
    lateinit var adapter: Adapter

    var page = 0
    var pageSize = 15
    var isLocalData = false
    var showSearchInput = false

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search, false)
        isLocalData = intent.getBooleanExtra("local", false)
        showSearchInput = intent.getBooleanExtra("showSearchInput", false)

        adapter = Adapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        adapter.setOnItemClickListener { adapter, view, position ->
            val data = adapter.getItem(position) as MovieBen
            MovieInfoActivity.starPlayActivity(this, data)
        }
        adapter.setOnLoadMoreListener({
            requestData(page, searchEditText.text.toString())
        }, recyclerView)

        searchEditText.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                adapter.data.clear()
                adapter.notifyDataSetChanged()
                requestData(0, v.text.toString())
            }
            return@OnEditorActionListener false
        })
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.data.clear()
                adapter.notifyDataSetChanged()
                requestData(0, s.toString())
            }

        })

        clearImageView.setOnClickListener {
            searchEditText.setText("")
            SoftInputMethodUtils.openSoftInput(searchEditText)
        }

        requestData(0, "")
        if (showSearchInput) {
            SoftInputMethodUtils.openSoftInput(searchEditText)
        }
    }

    fun requestData(page: Int, searchVal: String) {
        if (isLocalData) {
            loadLocalData(searchVal)
        } else {
            loadNetworkData(page, searchVal)
        }
    }

    fun pageAdd() {
        page++
    }

    fun loadNetworkData(page: Int, searchVal: String) {
        var tid = intent.getIntExtra("tid", -1)
        if (searchVal.length > 0) {
            tid = -1
        }
        get(URL.movieList, mapOf("tid" to tid, "name" to searchVal, "start" to page * pageSize, "num" to pageSize), object : HttpCall<ResponseBen<List<MovieBen>>>() {
            override fun onSuccess(t: ResponseBen<List<MovieBen>>) {
                pageAdd()
                adapter.addData(t.data)
                adapter.loadMoreComplete()
                if (t.data.size == 0) {
                    adapter.loadMoreEnd()
                }
                if (!showSearchInput)
                    SoftInputMethodUtils.hideSoftInput(searchEditText)
            }
        })
    }

    fun loadLocalData(searchVal: String) {
        var state = intent.getIntExtra("state", -1)
        var queryBuilder = App.getDaoSession().movieBenDao.queryBuilder()
        queryBuilder.where(MovieBenDao.Properties.V_name.like("%$searchVal%"))
        if (state == 1) {
            queryBuilder.where(MovieBenDao.Properties.IsPlay.eq(true))
        }
        if (state == 2) {
            queryBuilder.where(MovieBenDao.Properties.IsCollect.eq(true))
        }
        val data = queryBuilder.list()
        adapter.addData(data)
        adapter.loadMoreEnd()
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
        }
    }
}