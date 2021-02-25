package com.abook23.tv.ui.main.movie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abook23.tv.R
import com.abook23.tv.base.BaseDataBindAdapter
import com.abook23.tv.ben.AppConfig
import com.abook23.tv.ben.MovieBen
import com.abook23.tv.ben.TypeMovie
import com.abook23.tv.databinding.FragmentMovieBinding
import com.abook23.tv.databinding.ItemMovie0Binding
import com.abook23.tv.databinding.ItemRecyclerViewBinding
import com.abook23.tv.ui.movieinfo.MovieInfoActivity
import com.abook23.tv.ui.search.SearchActivity

class MovieFragment : Fragment() {

    lateinit var adapter: Adapter
    lateinit var appConfig: AppConfig
    lateinit var binding: FragmentMovieBinding
    var typeId = -1
    var isLoadingData = false

    companion object {
        fun newInstance(appConfig: AppConfig): MovieFragment {
            val fragment = MovieFragment()
            fragment.appConfig = appConfig
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<FragmentMovieBinding>(inflater, R.layout.fragment_movie, container, false)
        initRecyclerView(binding.recyclerView)
        binding.searchLinearLayout.setOnClickListener {
            toSearchActivity()
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val viewModel = ViewModelProvider(this).get(VideoViewModel::class.java)
        viewModel.movieListLiveData.observe(viewLifecycleOwner, { t ->
            isLoadingData = true
            typeId = t[0].typeId
            adapter.setNewData(t)
            binding.swipeRefreshLayout.isRefreshing = false
        })
        //不必每次都请求数据,增加用户体验,减少服务器压力,
        if (!isLoadingData) {
            viewModel.loadMovieList(appConfig.id)
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadMovieList(appConfig.id)
        }
    }

    fun initRecyclerView(recyclerView: RecyclerView) {
        adapter = Adapter()
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
        adapter.setOnItemClickListener { adapter, view, position ->
            activity?.let { SearchActivity.starActivity(it, false, appConfig.tid, true) }
        }
    }

    fun startToPlayDataActivity(movieBen: MovieBen) {
        activity?.let { MovieInfoActivity.startActivity(it, movieBen) }
    }

    fun toSearchActivity() {
        activity?.let { SearchActivity.starActivity(it, false, typeId, true) }
    }

    inner class Adapter : BaseDataBindAdapter<TypeMovie, ItemRecyclerViewBinding>(R.layout.item_recycler_view) {
        override fun onBindItem(binding: ItemRecyclerViewBinding, item: TypeMovie) {
            binding.typeMovie = item
            val recyclerView = binding.recyclerView
            val adapterSub = AdapterSub()
            recyclerView.adapter = adapterSub
            adapterSub.setNewData(item.data)
            adapterSub.setOnItemClickListener { adapter, view, position ->
                val movieBen = adapter.getItem(position) as MovieBen
                startToPlayDataActivity(movieBen)
            }

        }

        inner class AdapterSub : BaseDataBindAdapter<MovieBen, ItemMovie0Binding>(R.layout.item_movie_0) {
            override fun onBindItem(binding: ItemMovie0Binding, item: MovieBen) {
                binding.movie = item
                binding.executePendingBindings()
            }
        }
    }


}
