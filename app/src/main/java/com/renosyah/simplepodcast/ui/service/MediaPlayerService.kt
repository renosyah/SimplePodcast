package com.renosyah.simplepodcast.ui.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.net.wifi.WifiManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.android.exoplayer2.*
import com.renosyah.simplepodcast.BuildConfig
import com.renosyah.simplepodcast.R
import com.renosyah.simplepodcast.model.music.Music
import com.renosyah.simplepodcast.ui.activity.home.HomeActivity
import com.renosyah.simplepodcast.util.util.Companion.getBitmapWithPicasso

class MediaPlayerService : Service() {

    companion object {
        const val ACTION_PLAY_MUSIC = "${BuildConfig.APPLICATION_ID}.ACTION_PLAY_MUSIC"
        const val ACTION_SEEK_TO = "${BuildConfig.APPLICATION_ID}.ACTION_SEEK_TO"
        const val ACTION_STOP_MUSIC = "${BuildConfig.APPLICATION_ID}.ACTION_STOP_MUSIC"
        const val ACTION_CONTINUE_MUSIC = "${BuildConfig.APPLICATION_ID}.ACTION_CONTINUE_MUSIC"
        const val ACTION_CHECK_STATUS_PLAYING_CHANGE = "${BuildConfig.APPLICATION_ID}.ACTION_CHECK_STATUS_PLAYING_CHANGE"

        const val MUSIC_IS_PLAYED = "${BuildConfig.APPLICATION_ID}.MUSIC_IS_PLAYED"
        const val MUSIC_SEEK_CHANGE = "${BuildConfig.APPLICATION_ID}.MUSIC_SEEK_CHANGE"
        const val MUSIC_IS_COMPLETED = "${BuildConfig.APPLICATION_ID}.MUSIC_IS_COMPLETED"
        const val MUSIC_STATUS_PLAYING_CHANGE = "${BuildConfig.APPLICATION_ID}.MUSIC_STATUS_PLAYING_CHANGE"
        const val ON_ERROR = "${BuildConfig.APPLICATION_ID}.ON_ERROR"

        const val ACTION_STOP_SERVICE = "${BuildConfig.APPLICATION_ID}.ACTION_STOP_SERVICE"
    }

    lateinit var context: Context

    var currentMusic : Music? = null
    var exoPlayer : SimpleExoPlayer? = null
    var wifiLock : WifiManager.WifiLock? = null
    private lateinit var exoPlayerControlReceiver: BroadcastReceiver
    private val exoPlayerControlIntentFilter = IntentFilter()

    private val playerListener = object : Player.EventListener {

        override fun onPlayerError(error: ExoPlaybackException) {
            super.onPlayerError(error)
            val i = Intent(ON_ERROR)
            i.putExtra("message",error.toString())
            sendBroadcast(i)
        }


        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            super.onPlayerStateChanged(playWhenReady, playbackState)

            when (playbackState){
                ExoPlayer.STATE_ENDED -> {
                    currentMusic!!.seekPos = 0
                    val i = Intent(MUSIC_IS_COMPLETED)
                    sendBroadcast(i)
                }
                ExoPlayer.STATE_READY -> {

                }
                Player.STATE_BUFFERING -> {

                }
                Player.STATE_IDLE -> {

                }
            }

            if (playWhenReady && currentMusic != null){

                currentMusic!!.duration = exoPlayer!!.duration

                val i = Intent(MUSIC_IS_PLAYED)
                i.putExtra("current_music",currentMusic)
                sendBroadcast(i)

                exoPlayer!!.play()
                Thread(seekTracker).start()

                getBitmapWithPicasso(currentMusic!!.imageCoverUrl){
                    createNotification(it,currentMusic!!.title,currentMusic!!.description)
                }
            }
        }
    }

    private val seekTracker : Runnable = object : Runnable {
        override fun run() {

            var currentPosition = exoPlayer!!.currentPosition
            val total = exoPlayer!!.duration

            while (exoPlayer != null && exoPlayer!!.isPlaying && currentPosition < total) {

                val i = Intent(MUSIC_SEEK_CHANGE)
                i.putExtra("seek_position",currentPosition)
                sendBroadcast(i)

                currentPosition = try {
                    Thread.sleep(1000)
                    exoPlayer!!.currentPosition
                } catch (e: InterruptedException) {
                    return
                } catch (e: Exception) {
                    return
                }
            }
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        context = this

        if (BuildConfig.ENABLE_FOREGROUND) {
            startForeground()
        }

        wifiLock = (context.getSystemService(Context.WIFI_SERVICE) as WifiManager)
                .createWifiLock(WifiManager.WIFI_MODE_FULL, "${BuildConfig.APPLICATION_ID}.WIFI.LOCK")
        wifiLock!!.acquire()

        setMediaPlayer()

        exoPlayerControlIntentFilter.addAction(ACTION_PLAY_MUSIC)
        exoPlayerControlIntentFilter.addAction(ACTION_SEEK_TO)
        exoPlayerControlIntentFilter.addAction(ACTION_STOP_MUSIC)
        exoPlayerControlIntentFilter.addAction(ACTION_CONTINUE_MUSIC)
        exoPlayerControlIntentFilter.addAction(ACTION_CHECK_STATUS_PLAYING_CHANGE)

        exoPlayerControlReceiver = object : BroadcastReceiver(){
            override fun onReceive(ctx: Context?, intent: Intent?) {
                if (intent == null){
                    return
                }

                 if (exoPlayer== null){
                     return
                 }

                when (intent.action){
                    ACTION_CHECK_STATUS_PLAYING_CHANGE -> {
                        val i = Intent(MUSIC_STATUS_PLAYING_CHANGE)
                        i.putExtra("is_playing",exoPlayer!!.isPlaying)
                        sendBroadcast(i)
                    }
                    ACTION_CONTINUE_MUSIC -> {
                        setMediaPlayer()

                        val music = intent.getSerializableExtra("current_music") as Music
                        currentMusic = music

                        val pos = intent.getLongExtra("seek_pos",0L)
                        currentMusic!!.seekPos = pos

                        exoPlayer!!.setMediaItem(MediaItem.fromUri(currentMusic!!.getUrl()))
                        exoPlayer!!.seekTo(currentMusic!!.seekPos)
                        exoPlayer!!.prepare()
                    }
                    ACTION_PLAY_MUSIC -> {
                        setMediaPlayer()

                        val music = intent.getSerializableExtra("music") as Music
                        currentMusic = music
                        exoPlayer!!.setMediaItem(MediaItem.fromUri(music.getUrl()))
                        exoPlayer!!.prepare()
                    }
                    ACTION_SEEK_TO -> {
                        val seekValue = intent.getLongExtra("seek_position", 0L)
                        if (exoPlayer!!.isPlaying) {
                            currentMusic!!.seekPos = seekValue
                            exoPlayer!!.seekTo(seekValue.toLong())
                        }
                    }
                    ACTION_STOP_MUSIC -> {
                        if (exoPlayer!!.isPlaying){
                            exoPlayer!!.stop()
                        }
                    }
                    ACTION_STOP_SERVICE -> {
                        onDestroy()
                    }
                }
            }
        }
    }

    private fun setMediaPlayer(){
        if (exoPlayer != null) exoPlayer!!.release(); exoPlayer = null

        exoPlayer = SimpleExoPlayer.Builder(context).build()
        exoPlayer!!.addListener(playerListener)
        exoPlayer!!.playWhenReady = true
    }

    private val ONGOING_NOTIFICATION_ID: Int = kotlin.random.Random(System.currentTimeMillis()).nextInt(100)

    private fun startForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "${BuildConfig.APPLICATION_ID}.FOREGROUND.SERVICE",
                "${BuildConfig.APPLICATION_ID}.FOREGROUND.SERVICE.MEDIA",
                NotificationManager.IMPORTANCE_LOW
            )
            channel.description = "${BuildConfig.APPLICATION_ID}.FOREGROUND.SERVICE.DESC"
            channel.setShowBadge(false)

            val notificationManager : NotificationManager? = context.getSystemService(NotificationManager::class.java) as NotificationManager
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel)

                val notification = NotificationCompat.Builder(
                    context,
                    "${BuildConfig.APPLICATION_ID}.FOREGROUND.SERVICE"
                )
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
                    .setSmallIcon(R.drawable.icn)
                    .build()

                startForeground(ONGOING_NOTIFICATION_ID, notification)
            }
        }
    }

    fun createNotification(bmp : Bitmap,title : String, text : String){

        val openApp = Intent(context,HomeActivity::class.java)
        openApp.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

        val notification = NotificationCompat.Builder(context, "${BuildConfig.APPLICATION_ID}.FOREGROUND.SERVICE")
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setColor(ContextCompat.getColor(context,R.color.colorPrimaryDark))
            .setSmallIcon(R.drawable.icn)
            .setContentIntent(
                PendingIntent.getActivity(
                    context,0,openApp, PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
            )
            .setContentTitle(title)
            .setContentText(text)
            .setLargeIcon(bmp)
            .build()

        startForeground(ONGOING_NOTIFICATION_ID, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        registerReceiver(exoPlayerControlReceiver,exoPlayerControlIntentFilter)
        return Service.START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        if (exoPlayer != null) exoPlayer!!.release(); exoPlayer = null
        if (wifiLock != null) wifiLock!!.release(); wifiLock = null

        try {
            unregisterReceiver(exoPlayerControlReceiver)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }

        stopSelf()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        onDestroy()
    }
}
