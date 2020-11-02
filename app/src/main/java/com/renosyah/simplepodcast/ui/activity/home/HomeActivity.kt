package com.renosyah.simplepodcast.ui.activity.home

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import com.renosyah.simplepodcast.R
import com.renosyah.simplepodcast.di.component.DaggerActivityComponent
import com.renosyah.simplepodcast.di.module.ActivityModule
import com.renosyah.simplepodcast.model.RequestListModel
import com.renosyah.simplepodcast.model.category.Category
import com.renosyah.simplepodcast.model.music.Music
import com.renosyah.simplepodcast.ui.adapter.AdapterCategoryMusic
import com.renosyah.simplepodcast.ui.adapter.AdapterMusic
import com.renosyah.simplepodcast.ui.util.EmptyLayout
import com.renosyah.simplepodcast.ui.util.ErrorLayout
import com.renosyah.simplepodcast.ui.util.LoadingLayout
import com.renosyah.simplepodcast.ui.util.MiniPlayerLayout
import kotlinx.android.synthetic.main.activity_home.*
import javax.inject.Inject

class HomeActivity : AppCompatActivity(),HomeActivityContract.View {

    @Inject
    lateinit var presenter: HomeActivityContract.Presenter

    lateinit var context: Context

    private val categories = ArrayList<Category>()
    private lateinit var adapterCategories : AdapterCategoryMusic
    private val reqCategories = RequestListModel()

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
            presenter.getAllCategories("",reqCategories,true)
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
            miniPlayerLayout.hide()
        })
        mini_player.setOnClickListener {  }

        home_scrollview.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY >= v.getChildAt(v.childCount - 1).measuredHeight - v.measuredHeight) {
                reqCategories.offset += reqCategories.limit
                presenter.getAllCategories("",reqCategories,false)
            }
        })

        category_music_recycleview_swiper.setOnRefreshListener {
            reqCategories.offset = 0
            presenter.getAllCategories("",reqCategories,true)
            category_music_recycleview_swiper.isRefreshing = !category_music_recycleview_swiper.isRefreshing
        }

        presenter.getAllCategories("",reqCategories,true)
    }

    fun setQuery(){
        reqCategories.filterBy = "flag_status"
        reqCategories.filterValue = "0"
        reqCategories.searchValue = ""
        reqCategories.searchBy = "name"
        reqCategories.orderBy = "created_at"
        reqCategories.orderDir = "asc"
        reqCategories.offset = 0
        reqCategories.limit = 10
    }

    fun setAdapter(){
        adapterCategories = AdapterCategoryMusic(context, categories,{ m, pos ->

        },object : AdapterMusic.onMusicClickListener {
            override fun onImageClick(m: Music, pos: Int) {
                miniPlayerLayout.playMusic(m)
            }

            override fun onTitleClick(m: Music, pos: Int) {

            }
        })
        category_music_recycleview.adapter = adapterCategories
        category_music_recycleview.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
    }

    override fun onEmptyGetAllMusic() {

    }

    override fun onGetAllMusic(categoryId : String, data: ArrayList<Music>) {
        for (c in categories){
            if (c.id == categoryId){
                c.musics.addAll(data)
                break
            }
        }
        adapterCategories.notifyDataSetChanged()
    }

    override fun showProgressGetAllMusic(show: Boolean) {

    }

    override fun showErrorGetAllMusic(e: String) {
        main_layout.visibility = View.GONE
        error.show()
    }

    override fun onEmptyGetAllCategories() {
        if (reqCategories.offset == 0){
            main_layout.visibility = View.GONE
            emptyLayout.show()
        }
    }

    override fun onGetAllCategories(data: ArrayList<Category>) {
        if (reqCategories.offset == 0){
            categories.clear()
        }
        categories.addAll(data)
        adapterCategories.notifyDataSetChanged()
        emptyLayout.hide()

        for (c in categories){
            val reqMusic = RequestListModel()
            reqMusic.filterBy = "category_id"
            reqMusic.filterValue = c.id
            reqMusic.searchValue = ""
            reqMusic.searchBy = "title"
            reqMusic.orderBy = "title"
            reqMusic.orderDir = "asc"
            reqMusic.offset = 0
            reqMusic.limit = 4

            presenter.getAllMusic("",c.id,reqMusic,false)
        }
    }

    override fun showProgressGetAllCategories(show: Boolean) {
        loading.setVisibility(show)
        main_layout.visibility = if (show) View.GONE else View.VISIBLE
    }

    override fun showErrorGetAllCategories(e: String) {
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