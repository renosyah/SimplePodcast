package com.renosyah.simplepodcast.ui.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.renosyah.simplepodcast.R
import com.renosyah.simplepodcast.model.music.Music
import com.renosyah.simplepodcast.ui.service.MediaPlayerService
import com.renosyah.simplepodcast.util.SerializableSave
import com.squareup.picasso.Picasso
import java.util.concurrent.TimeUnit

class MiniPlayerLayout {
    private lateinit var c: Context
    private lateinit  var includeParent: View

    private var currentMusic : Music? =  null
    private var isPlaying = false

    private lateinit var mediaPlayerObserverReceiver : BroadcastReceiver
    private val mediaPlayerObserverIntentFilter = IntentFilter()

    private lateinit var imageMusic : ImageView
    private lateinit var titleMusic : TextView
    private lateinit var seekHint : TextView
    private lateinit var seekBarTrack : SeekBar
    private lateinit var playButton : ImageView

    private lateinit var onMusicIsPlayed :()-> Unit
    private lateinit var onPreparingMusic :()-> Unit
    private lateinit var onMusicIsCompleted :()-> Unit

    constructor(c: Context, includeParent: View) {
        this.c = c
        this.includeParent = includeParent
        this.imageMusic = includeParent.findViewById(R.id.current_music_imageview)
        this.titleMusic = includeParent.findViewById(R.id.current_music_title_textview)
        this.seekHint = includeParent.findViewById(R.id.current_music_seek_hint_textview)
        this.seekBarTrack = includeParent.findViewById(R.id.current_music_seekbar)
        this.playButton = includeParent.findViewById(R.id.current_play_button_imageview)

        initReceiver()

        hide()
        emptySeekBar()
        playButton.setImageDrawable(ContextCompat.getDrawable(c,R.drawable.play))
        c.sendBroadcast(Intent(MediaPlayerService.ACTION_CHECK_STATUS_PLAYING_CHANGE))

        if (SerializableSave(c,SerializableSave.lastMusicData).load() != null){
            currentMusic = SerializableSave(c,SerializableSave.lastMusicData).load() as Music

            show()
            setLayoutValue()

            seekBarTrack.max = currentMusic!!.duration.toInt()
            seekBarTrack.progress = currentMusic!!.seekPos.toInt()

            setSeekbarValue()
        }

        playButton.setOnClickListener {
            val img = if (isPlaying) R.drawable.play else R.drawable.pause
            playButton.setImageDrawable(ContextCompat.getDrawable(c,img))

            val flag = if (isPlaying) MediaPlayerService.ACTION_STOP_MUSIC else MediaPlayerService.ACTION_CONTINUE_MUSIC
            val i = Intent(flag)
            i.putExtra("current_music",currentMusic)
            i.putExtra("seek_pos",currentMusic!!.seekPos)
            c.sendBroadcast(i)

            isPlaying = !isPlaying
        }

        seekBarTrack.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                seekBarTrack.visibility = View.VISIBLE
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                currentMusic!!.seekPos = seekBarTrack.progress.toLong()

                setSeekbarValue()

                val i = Intent(MediaPlayerService.ACTION_SEEK_TO)
                i.putExtra("seek_position",currentMusic!!.seekPos)
                c.sendBroadcast(i)
            }
        })

    }

    fun setMiniPlayerListener(onMusicIsPlayed :()-> Unit,onPreparingMusic :()-> Unit,onMusicIsCompleted :()-> Unit){
        this.onMusicIsCompleted = onMusicIsCompleted
        this.onMusicIsPlayed = onMusicIsPlayed
        this.onPreparingMusic = onPreparingMusic
    }

    fun playMusic(m :Music){
        this.currentMusic = m

        isPlaying = true
        playButton.setImageDrawable(ContextCompat.getDrawable(c,R.drawable.pause))

        val i = Intent(MediaPlayerService.ACTION_PLAY_MUSIC)
        i.putExtra("music",this.currentMusic)
        c.sendBroadcast(i)

        onPreparingMusic.invoke()

        if (currentMusic != null) SerializableSave(this.c, SerializableSave.lastMusicData).save(currentMusic!!)
    }

    private fun initReceiver(){

        mediaPlayerObserverIntentFilter.addAction(MediaPlayerService.MUSIC_IS_PLAYED)
        mediaPlayerObserverIntentFilter.addAction(MediaPlayerService.MUSIC_SEEK_CHANGE)
        mediaPlayerObserverIntentFilter.addAction(MediaPlayerService.MUSIC_IS_COMPLETED)
        mediaPlayerObserverIntentFilter.addAction(MediaPlayerService.MUSIC_STATUS_PLAYING_CHANGE)
        mediaPlayerObserverIntentFilter.addAction(MediaPlayerService.ON_ERROR)

        mediaPlayerObserverReceiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                if (intent == null){
                    return
                }

                when (intent.action){
                    MediaPlayerService.MUSIC_STATUS_PLAYING_CHANGE -> {
                        isPlaying = intent.getBooleanExtra("is_playing",false)

                        val img = if (isPlaying) R.drawable.pause else R.drawable.play
                        playButton.setImageDrawable(ContextCompat.getDrawable(c,img))
                    }

                    MediaPlayerService.MUSIC_IS_PLAYED -> {
                        val lm = intent.getSerializableExtra("current_music") as Music
                        currentMusic = lm

                        show()
                        setLayoutValue()

                        seekBarTrack.max = currentMusic!!.duration.toInt()
                        seekBarTrack.progress = currentMusic!!.seekPos.toInt()

                        onMusicIsPlayed.invoke()

                    }
                    MediaPlayerService.MUSIC_SEEK_CHANGE -> {
                        val position = intent.getLongExtra("seek_position",0L)
                        currentMusic!!.seekPos = position

                        show()

                        seekBarTrack.max = currentMusic!!.duration.toInt()
                        seekBarTrack.progress = currentMusic!!.seekPos.toInt()

                        setSeekbarValue()
                    }
                    MediaPlayerService.MUSIC_IS_COMPLETED-> {
                        currentMusic!!.seekPos = 0
                        seekBarTrack.progress = currentMusic!!.seekPos.toInt()

                        emptySeekBar()

                        isPlaying = false
                        playButton.setImageDrawable(ContextCompat.getDrawable(c,R.drawable.play))

                        onMusicIsCompleted.invoke()
                    }
                    MediaPlayerService.ON_ERROR -> {
                        Toast.makeText(c,R.string.error_message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        this.c.registerReceiver(mediaPlayerObserverReceiver,mediaPlayerObserverIntentFilter)
    }

    private fun setLayoutValue(){
        Picasso.get().load(currentMusic!!.imageCoverUrl).into(imageMusic)
        titleMusic.text = currentMusic!!.title
    }

    fun unregisterAllReceiver(){
        if (currentMusic != null) SerializableSave(this.c, SerializableSave.lastMusicData).save(currentMusic!!)
        this.c.unregisterReceiver(mediaPlayerObserverReceiver)
    }

    private fun emptySeekBar(){
        seekHint.text = "--:-- / --:--"
    }

    private fun setSeekbarValue(){
        val milis = currentMusic!!.seekPos.toLong()
        val min = TimeUnit.MILLISECONDS.toMinutes(milis)
        val sec = TimeUnit.MILLISECONDS.toSeconds(milis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milis))

        val maxmilis = currentMusic!!.duration.toLong()
        val maxmin = TimeUnit.MILLISECONDS.toMinutes(maxmilis)
        val maxsec = TimeUnit.MILLISECONDS.toSeconds(maxmilis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(maxmilis))

        seekHint.text = "${String.format("%02d", min)}:${String.format("%02d", sec)} / ${String.format("%02d", maxmin)}:${String.format("%02d", maxsec)}"
    }

    fun show() {
        includeParent.visibility = (View.VISIBLE)
    }

    fun hide() {
        includeParent.visibility = (View.GONE)
    }
}