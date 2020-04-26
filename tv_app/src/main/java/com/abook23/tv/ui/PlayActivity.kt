package com.abook23.tv.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.abook23.tv.R
import com.android.easy.app.base.BaseAppCompatActivity

class PlayActivity : BaseAppCompatActivity() {

    private lateinit var videoFragment: VideoFragment

    companion object {
        fun starPlayActivity(activity: Activity, videoName: String, playUrl: String) {
            var intent = Intent(activity, PlayActivity::class.java)
            intent.putExtra("videoName", videoName)
            intent.putExtra("playUrl", playUrl)
            activity.startActivity(intent)
        }

        fun starPlayActivity(activity: Activity, videoName: String, playUrl: String, urls: ArrayList<String>) {
            var intent = Intent(activity, PlayActivity::class.java)
            intent.putExtra("videoName", videoName)
            intent.putExtra("playUrl", playUrl)
            intent.putStringArrayListExtra("urls", urls)
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)
        val videoName = intent.getStringExtra("videoName")
        var playUrl = intent.getStringExtra("playUrl")
        var urls = intent.getStringArrayListExtra("urls")
        videoFragment = VideoFragment.newInstance()
        videoFragment.setVideoName(videoName)
        videoFragment.setUrls(urls)
        videoFragment.setPlayUrl(playUrl)
        supportFragmentManager.beginTransaction().add(R.id.playFrame, videoFragment).commit()
        toFullscreen()
    }

    override fun onBackPressed() {
        if (videoFragment.isLockView) {
            return
        }
        super.onBackPressed()
    }

}
