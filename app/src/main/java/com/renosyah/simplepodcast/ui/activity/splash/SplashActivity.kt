package com.renosyah.simplepodcast.ui.activity.splash

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.renosyah.simplepodcast.R
import com.renosyah.simplepodcast.ui.activity.home.HomeActivity
import com.renosyah.simplepodcast.ui.service.MediaPlayerService
import com.renosyah.simplepodcast.util.util
import java.util.*
import kotlin.concurrent.schedule

class SplashActivity : AppCompatActivity() {

    lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        initWidget()
    }

    private fun initWidget(){
        this.context = this@SplashActivity

        Timer(false).schedule(3000) {

            if (!util.isMyServiceRunning(context, MediaPlayerService::class.java)) {
                val i = Intent(context, MediaPlayerService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(i)
                } else {
                    context.startService(i)
                }
            }

            startActivity(Intent(context, HomeActivity::class.java))
            finish()
        }
    }
}