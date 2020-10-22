package com.renosyah.simplepodcast.ui.activity.play

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.renosyah.simplepodcast.R

class PlayActivity : AppCompatActivity() {

    lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)
        initWidget()
    }

    private fun initWidget(){
        this.context = this@PlayActivity
    }
}