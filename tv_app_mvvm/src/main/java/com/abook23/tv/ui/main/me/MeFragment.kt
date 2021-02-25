package com.abook23.tv.ui.main.me

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import com.abook23.tv.App
import com.abook23.tv.R
import com.abook23.tv.ben.MovieBen
import com.abook23.tv.databinding.ItemMovie12Binding
import com.abook23.tv.databinding.MeFragmentBinding
import com.abook23.tv.ui.cache.CacheActivity
import com.abook23.tv.ui.movieinfo.MovieInfoActivity
import com.abook23.tv.ui.search.SearchActivity
import com.android.easy.base.util.AndroidUtils
import com.android.easy.dialog.EasyDialog
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class MeFragment : Fragment() {

    lateinit var binding: MeFragmentBinding

    companion object {
        fun newInstance() = MeFragment()
    }

    private lateinit var viewModel: MeViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<MeFragmentBinding>(inflater, R.layout.me_fragment, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val adapter0 = Adapter0()
        val adapter1 = Adapter1()
        binding.recyclerView0.adapter = adapter0
        binding.recyclerView1.adapter = adapter1
        viewModel = ViewModelProvider(this).get(MeViewModel::class.java)
        binding.viewModel = viewModel


        adapter0.setOnItemClickListener { adapter, view, position ->
            if (position == 0 || position == 1) {
                SearchActivity.starActivity(activity!!, true, -1, false, position + 1)
            } else if (position == 2) {
                CacheActivity.start(context!!)
            } else if (position == 3) {
                clearCache()
            }
        }

        adapter1.setOnItemClickListener { adapter, view, position ->
            val movieBen = adapter1.getItem(position) as MovieBen
            MovieInfoActivity.startActivity(activity!!, movieBen)
        }

    }

    override fun onStart() {
        super.onStart()
        viewModel.getMovieHistoryList()
    }

    private fun clearCache() {
        EasyDialog.Builder()
                .title("缓存清理")
                .content("加载中...")
                .positive("确认", EasyDialog.OnClickListener { dialog, view ->
                    dialog.dismiss()
                    AndroidUtils.selectCacheSize(context, true)
                    App.getDaoSession().cacheVideoBeanDao.deleteAll()
                })
                .build()
                .setOnViewCreatedListener { dialog, view ->
                    Thread {
                        val cacheSize = AndroidUtils.selectCacheSize(context, false)
                        activity?.runOnUiThread {
                            dialog.setContent(cacheSize)
                        }
                    }.start()
                }
                .show(childFragmentManager)


    }

    inner class Adapter0 : BaseQuickAdapter<Array<Any>, BaseViewHolder>(R.layout.item_type_0) {
        override fun convert(helper: BaseViewHolder, item: Array<Any>) {
            helper.setText(R.id.text, item[0].toString())
            helper.getView<ImageView>(R.id.imageView).setImageResource(item[1].toString().toInt())
        }
    }

    inner class Adapter1 : BaseQuickAdapter<MovieBen, BaseViewHolder>(R.layout.item_movie_1_2) {
        override fun convert(helper: BaseViewHolder, item: MovieBen) {
            val binding = DataBindingUtil.bind<ItemMovie12Binding>(helper.itemView)
            binding?.movieBen = item
        }
    }

}