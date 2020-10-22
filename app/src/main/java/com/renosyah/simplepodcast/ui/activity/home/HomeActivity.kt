package com.renosyah.simplepodcast.ui.activity.home

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.GridLayoutManager
import com.renosyah.simplepodcast.R
import com.renosyah.simplepodcast.di.component.DaggerActivityComponent
import com.renosyah.simplepodcast.di.module.ActivityModule
import com.renosyah.simplepodcast.model.RequestListModel
import com.renosyah.simplepodcast.model.music.Music
import com.renosyah.simplepodcast.ui.adapter.AdapterMusic
import com.renosyah.simplepodcast.ui.service.MediaPlayerService
import com.renosyah.simplepodcast.ui.util.EmptyLayout
import com.renosyah.simplepodcast.ui.util.ErrorLayout
import com.renosyah.simplepodcast.ui.util.LoadingLayout
import com.renosyah.simplepodcast.ui.util.MiniPlayerLayout
import com.renosyah.simplepodcast.util.SerializableSave
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_home.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class HomeActivity : AppCompatActivity(),HomeActivityContract.View {

    @Inject
    lateinit var presenter: HomeActivityContract.Presenter

    lateinit var context: Context

    private val musics = ArrayList<Music>()
    private lateinit var adapterMusic : AdapterMusic
    private val reqMusics = RequestListModel()

    lateinit var miniPlayerLayout : MiniPlayerLayout
    lateinit var emptyLayout: EmptyLayout
    lateinit var loading : LoadingLayout
    lateinit var error : ErrorLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        initWidget()
    }

    private fun initWidget(){
        this.context = this@HomeActivity

        injectDependency()
        presenter.attach(this)
        presenter.subscribe()

        setQuery()
        setAdapter()

        emptyLayout = EmptyLayout(context,empty_layout)
        emptyLayout.setMessage(getString(R.string.no_music_found))
        emptyLayout.hide()

        loading = LoadingLayout(context,loading_layout)
        loading.setMessage(getString(R.string.loading_musics))
        loading.hide()

        error = ErrorLayout(context,error_layout) {
            presenter.getAllMusic("", reqMusics,true)
        }
        error.hide()

        miniPlayerLayout = MiniPlayerLayout(context,mini_player)
        miniPlayerLayout.setMiniPlayerListener({
            loading.setVisibility(false)
            main_layout.visibility = View.VISIBLE
        },{
            loading.setVisibility(true)
            main_layout.visibility = View.GONE
        },{
            val rand = kotlin.random.Random(System.currentTimeMillis()).nextInt(musics.size)
            miniPlayerLayout.playMusic(musics[rand])
        })

        home_scrollview.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY >= v.getChildAt(v.childCount - 1).measuredHeight - v.measuredHeight) {
                reqMusics.offset += reqMusics.limit
                presenter.getAllMusic("", reqMusics,false)
            }
        })

        music_recycleview_swiper.setOnRefreshListener {
            reqMusics.offset = 0
            presenter.getAllMusic("", reqMusics,true)
            music_recycleview_swiper.isRefreshing = !music_recycleview_swiper.isRefreshing
        }

        presenter.getAllMusic("", reqMusics,true)
    }

    fun setQuery(){
        reqMusics.filterBy = "flag_status"
        reqMusics.filterValue = "0"
        reqMusics.searchValue = ""
        reqMusics.searchBy = "title"
        reqMusics.orderBy = "created_at"
        reqMusics.orderDir = "asc"
        reqMusics.offset = 0
        reqMusics.limit = 10
    }

    fun setAdapter(){
        adapterMusic = AdapterMusic(context, musics) { m,pos ->
            miniPlayerLayout.playMusic(m)
        }
        music_recycleview.adapter = adapterMusic
        music_recycleview.apply {
            layoutManager = GridLayoutManager(context, 2)
        }
    }


    override fun onEmptyGetAllMusic() {
        if (reqMusics.offset == 0){
            main_layout.visibility = View.GONE
            emptyLayout.show()
        }
    }

    override fun onGetAllMusic(data: ArrayList<Music>) {
        if (reqMusics.offset == 0){
            musics.clear()
        }
        musics.addAll(data)
        adapterMusic.notifyDataSetChanged()
        emptyLayout.hide()
    }

    override fun showProgressGetAllMusic(show: Boolean) {
        loading.setVisibility(show)
        main_layout.visibility = if (show) View.GONE else View.VISIBLE
    }

    override fun showErrorGetAllMusic(e: String) {
        main_layout.visibility = View.GONE
        error.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        miniPlayerLayout.unregisterAllReceiver()
        presenter.unsubscribe()
    }


    private fun injectDependency(){
        val listcomponent = DaggerActivityComponent.builder()
            .activityModule(ActivityModule(this))
            .build()

        listcomponent.inject(this)
    }
}