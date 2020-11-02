package com.renosyah.simplepodcast.ui.activity.home

import com.renosyah.simplepodcast.base.BaseContract
import com.renosyah.simplepodcast.model.RequestListModel
import com.renosyah.simplepodcast.model.category.Category
import com.renosyah.simplepodcast.model.music.Music

class HomeActivityContract {
    interface View: BaseContract.View {

        // add more for request
        fun onEmptyGetAllMusic()
        fun onGetAllMusic(categoryId : String, data : ArrayList<Music>)
        fun showProgressGetAllMusic(show: Boolean)
        fun showErrorGetAllMusic(e: String)

        fun onEmptyGetAllCategories()
        fun onGetAllCategories(data : ArrayList<Category>)
        fun showProgressGetAllCategories(show: Boolean)
        fun showErrorGetAllCategories(e: String)
    }

    interface Presenter: BaseContract.Presenter<View> {

        // add for request
        fun getAllMusic(sessionId : String,categoryId : String, req : RequestListModel, enableLoading :Boolean)
        fun getAllCategories(sessionId : String, req : RequestListModel, enableLoading :Boolean)
    }
}