package com.abook23.tv.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.abook23.tv.App
import com.abook23.tv.R
import com.abook23.tv.ben.PlayData
import com.android.easy.app.base.BaseAppCompatActivity
import com.android.easy.play.VideoFragment
import com.android.easy.play.VideoFragment.OnVideoFragmentListener
import java.util.*


class PlayActivity : BaseAppCompatActivity() {

    private lateinit var videoFragment: VideoFragment

    companion object {

        fun starPlayActivity(activity: Activity, data: PlayData) {
            var intent = Intent(activity, PlayActivity::class.java)
            intent.putExtra("data", data)
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)
        val playData = intent.getSerializableExtra("data") as PlayData
        val videoName = playData.title
        val playUrl = playData.url
        videoFragment = VideoFragment.newInstance(videoName, playUrl, playData.movieInfos)
        //播放进度监听
        videoFragment.setOnVideoFragmentListener(object : OnVideoFragmentListener {
            override fun onMediaPlayer(url: String?, currentPosition: Long, duration: Long) {
                val cacheVideoBean = App.getDaoSession().cacheVideoBeanDao.load(url)
                if (cacheVideoBean != null) {
                    cacheVideoBean.play_rate = String.format("%.2f", currentPosition / duration.toFloat() * 100)
                    App.getDaoSession().cacheVideoBeanDao.update(cacheVideoBean)
                }
            }

            override fun onSettingVisibilityView(b: Boolean) {
                if (!b) {
                    hideSystemUI()
                }else{
                    showSystemUI()
                }
            }
        })
        supportFragmentManager.beginTransaction().add(R.id.playFrame, videoFragment).commit()
        toFullscreen()
    }

    var onBackPressedTime: Long = 0
    override fun onBackPressed() {
        videoFragment.onBackPressed()
        val nowTime = Date().time
        if (nowTime - onBackPressedTime > 2000) {
            onBackPressedTime = nowTime
            Toast.makeText(this, "再按一次退出", Toast.LENGTH_LONG).show()
            return
        }
        super.onBackPressed()
    }
}
