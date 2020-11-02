package com.renosyah.simplepodcast.ui.activity.home

import com.renosyah.simplepodcast.model.RequestListModel
import com.renosyah.simplepodcast.model.ResponseModel
import com.renosyah.simplepodcast.model.category.Category
import com.renosyah.simplepodcast.model.music.Music
import com.renosyah.simplepodcast.service.RetrofitService
import com.renosyah.simplepodcast.util.util
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class HomeActivityPresenter : HomeActivityContract.Presenter {
    private val subscriptions = CompositeDisposable()
    private val api: RetrofitService = RetrofitService.create()
    private lateinit var view: HomeActivityContract.View

    override fun getAllMusic(sessionId : String,categoryId : String, req: RequestListModel, enableLoading: Boolean) {
        if (enableLoading) {
            view.showProgressGetAllMusic(true)
        }
        val subscribe = api.allMusic(sessionId,req.clone())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result: ResponseModel<ArrayList<Music>>? ->
                if (enableLoading) {
                    view.showProgressGetAllMusic(false)
                }
                if (result != null) {

                    if (result.errors.isNotEmpty()){
                        view.showErrorGetAllMusic(util.errorToString(result.errors))
                    }
                    if (result.data != null) {
                        view.onGetAllMusic(categoryId,result.data!!)
                        if (result.data!!.isEmpty()){
                            view.onEmptyGetAllMusic()
                        }
                    } else {
                        view.onEmptyGetAllMusic()
                    }
                }

            }, { t: Throwable ->
                if (enableLoading) {
                    view.showProgressGetAllMusic(false)
                }
                view.showErrorGetAllMusic(t.message!!)
            })

        subscriptions.add(subscribe)
    }

    override fun getAllCategories(sessionId: String, req: RequestListModel, enableLoading: Boolean) {
        if (enableLoading) {
            view.showProgressGetAllCategories(true)
        }
        val subscribe = api.allCategories(sessionId,req.clone())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result: ResponseModel<ArrayList<Category>>? ->
                if (enableLoading) {
                    view.showProgressGetAllCategories(false)
                }
                if (result != null) {

                    if (result.errors.isNotEmpty()){
                        view.showErrorGetAllCategories(util.errorToString(result.errors))
                    }
                    if (result.data != null) {
                        view.onGetAllCategories(result.data!!)
                        if (result.data!!.isEmpty()){
                            view.onEmptyGetAllCategories()
                        }
                    } else {
                        view.onEmptyGetAllCategories()
                    }
                }

            }, { t: Throwable ->
                if (enableLoading) {
                    view.showProgressGetAllCategories(false)
                }
                view.showErrorGetAllCategories(t.message!!)
            })

        subscriptions.add(subscribe)
    }


    override fun subscribe() {

    }

    override fun unsubscribe() {
        subscriptions.clear()
    }

    override fun attach(view: HomeActivityContract.View) {
        this.view = view
    }
}