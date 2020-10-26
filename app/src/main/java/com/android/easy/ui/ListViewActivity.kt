package com.android.easy.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.easy.R

class ListViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_view)

        supportFragmentManager.beginTransaction().add(R.id.frameLayout, UserInfoBaseListFragment()).commit()
    }
}