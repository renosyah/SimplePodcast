package com.renosyah.simplepodcast.ui.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.wifi.WifiManager
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.renosyah.simplepodcast.BuildConfig
import com.renosyah.simplepodcast.R
import com.renosyah.simplepodcast.model.music.Music


class MediaPlayerService : Service() {

    companion object {
        val ACTION_PLAY_MUSIC = "${BuildConfig.APPLICATION_ID}.ACTION_PLAY_MUSIC"
        val ACTION_SEEK_TO = "${BuildConfig.APPLICATION_ID}.ACTION_SEEK_TO"
        val ACTION_STOP_MUSIC = "${BuildConfig.APPLICATION_ID}.ACTION_STOP_MUSIC"
        val ACTION_CONTINUE_MUSIC = "${BuildConfig.APPLICATION_ID}.ACTION_CONTINUE_MUSIC"
        val ACTION_CHECK_STATUS_PLAYING_CHANGE = "${BuildConfig.APPLICATION_ID}.ACTION_CHECK_STATUS_PLAYING_CHANGE"

        val MUSIC_IS_PLAYED = "${BuildConfig.APPLICATION_ID}.MUSIC_IS_PLAYED"
        val MUSIC_SEEK_CHANGE = "${BuildConfig.APPLICATION_ID}.MUSIC_SEEK_CHANGE"
        val MUSIC_IS_COMPLETED = "${BuildConfig.APPLICATION_ID}.MUSIC_IS_COMPLETED"
        val MUSIC_STATUS_PLAYING_CHANGE = "${BuildConfig.APPLICATION_ID}.MUSIC_STATUS_PLAYING_CHANGE"
        val ON_ERROR = "${BuildConfig.APPLICATION_ID}.ON_ERROR"

        val ACTION_STOP_SERVICE = "${BuildConfig.APPLICATION_ID}.ACTION_STOP_SERVICE"
    }

    lateinit var context: Context

    var currentMusic : Music? = null
    var mediaPlayer : MediaPlayer? = null
    var wifiLock : WifiManager.WifiLock? = null
    private lateinit var mediaPlayerControlReceiver: BroadcastReceiver
    private val mediaPlayerControlIntentFilter = IntentFilter()

    private var onCompleted : MediaPlayer.OnCompletionListener = MediaPlayer.OnCompletionListener {
        currentMusic!!.seekPos = 0
        val i = Intent(MUSIC_IS_COMPLETED)
        sendBroadcast(i)
    }

    private val onPrepared : MediaPlayer.OnPreparedListener = MediaPlayer.OnPreparedListener {
        currentMusic!!.duration = it.duration
        val i = Intent(MUSIC_IS_PLAYED)
        i.putExtra("current_music",currentMusic)
        sendBroadcast(i)

        it.start()
        Thread(seekTracker).start()
    }

    private val onError : MediaPlayer.OnErrorListener = MediaPlayer.OnErrorListener { mp, what, extra ->
        sendBroadcast(Intent(ON_ERROR))
        return@OnErrorListener true
    }

    private val seekTracker : Runnable = object : Runnable {
        override fun run() {

            var currentPosition = mediaPlayer!!.currentPosition
            val total = mediaPlayer!!.duration

            while (mediaPlayer != null && mediaPlayer!!.isPlaying && currentPosition < total) {

                val i = Intent(MUSIC_SEEK_CHANGE)
                i.putExtra("seek_position",currentPosition)
                sendBroadcast(i)

                currentPosition = try {
                    Thread.sleep(1000)
                    mediaPlayer!!.currentPosition
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

        mediaPlayerControlIntentFilter.addAction(ACTION_PLAY_MUSIC)
        mediaPlayerControlIntentFilter.addAction(ACTION_SEEK_TO)
        mediaPlayerControlIntentFilter.addAction(ACTION_STOP_MUSIC)
        mediaPlayerControlIntentFilter.addAction(ACTION_CONTINUE_MUSIC)
        mediaPlayerControlIntentFilter.addAction(ACTION_CHECK_STATUS_PLAYING_CHANGE)

        mediaPlayerControlReceiver = object : BroadcastReceiver(){
            override fun onReceive(ctx: Context?, intent: Intent?) {
                if (intent == null){
                    return
                }

                 if (mediaPlayer == null){
                     return
                 }

                when (intent.action){
                    ACTION_CHECK_STATUS_PLAYING_CHANGE -> {
                        val i = Intent(MUSIC_STATUS_PLAYING_CHANGE)
                        i.putExtra("is_playing",mediaPlayer!!.isPlaying)
                        sendBroadcast(i)
                    }
                    ACTION_CONTINUE_MUSIC -> {
                        setMediaPlayer()

                        val music = intent.getSerializableExtra("current_music") as Music
                        currentMusic = music

                        mediaPlayer!!.setDataSource(currentMusic!!.getUrl())
                        mediaPlayer!!.setOnPreparedListener {
                            onPrepared.onPrepared(it)
                            mediaPlayer!!.seekTo(currentMusic!!.seekPos)
                        }
                        mediaPlayer!!.prepareAsync()
                    }
                    ACTION_PLAY_MUSIC -> {
                        setMediaPlayer()

                        val music = intent.getSerializableExtra("music") as Music
                        currentMusic = music
                        mediaPlayer!!.setDataSource(music.getUrl())
                        mediaPlayer!!.prepareAsync()
                    }
                    ACTION_SEEK_TO -> {
                        val seekValue = intent.getIntExtra("seek_position", 0)
                        if (mediaPlayer!!.isPlaying) {
                            currentMusic!!.seekPos = seekValue
                            mediaPlayer!!.seekTo(seekValue)
                        }
                    }
                    ACTION_STOP_MUSIC -> {
                        if (mediaPlayer!!.isPlaying){
                            mediaPlayer!!.stop()
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
        if (mediaPlayer != null) mediaPlayer!!.release(); mediaPlayer = null

        mediaPlayer = MediaPlayer()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mediaPlayer!!.setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
        }
        mediaPlayer!!.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK)
        mediaPlayer!!.setOnCompletionListener(onCompleted)
        mediaPlayer!!.setOnErrorListener(onError)
        mediaPlayer!!.setOnPreparedListener(onPrepared)
        mediaPlayer!!.isLooping = false
    }

    private val ONGOING_NOTIFICATION_ID: Int = kotlin.random.Random(System.currentTimeMillis()).nextInt(100)

    private fun startForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "${BuildConfig.APPLICATION_ID}.FOREGROUND.SERVICE",
                "${BuildConfig.APPLICATION_ID}.FOREGROUND.SERVICE.MEDIA",
                NotificationManager.IMPORTANCE_LOW
            )
            channel.setDescription("${BuildConfig.APPLICATION_ID}.FOREGROUND.SERVICE.DESC")
            channel.setShowBadge(false)

            val notificationManager : NotificationManager? = context.getSystemService(NotificationManager::class.java) as NotificationManager
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel)

                val notification = Notification.Builder(context, "${BuildConfig.APPLICATION_ID}.FOREGROUND.SERVICE")
                    .setSmallIcon(R.drawable.icn)
                    .setLargeIcon(BitmapFactory.decodeResource(context.resources,R.drawable.icn))
                    .setContentText(getText(R.string.foreground_notification_message))
                    .setOngoing(true)
                    .build()

                startForeground(ONGOING_NOTIFICATION_ID, notification)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        registerReceiver(mediaPlayerControlReceiver,mediaPlayerControlIntentFilter)
        return Service.START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mediaPlayer != null) mediaPlayer!!.release(); mediaPlayer = null
        if (wifiLock != null) wifiLock!!.release(); wifiLock = null

        try {
            unregisterReceiver(mediaPlayerControlReceiver)
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
