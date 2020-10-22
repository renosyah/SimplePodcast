package com.renosyah.simplepodcast.di.module

import dagger.Module
import dagger.Provides
import android.app.Activity
import com.renosyah.simplepodcast.service.RetrofitService
import com.renosyah.simplepodcast.ui.activity.home.HomeActivityContract
import com.renosyah.simplepodcast.ui.activity.home.HomeActivityPresenter

@Module
class ActivityModule(private var activity : Activity) {
    @Provides
    fun provideActivity() : Activity {
        return activity
    }

    @Provides
    fun provideApiService(): RetrofitService {
        return RetrofitService.create()
    }

    // add for each new activity
    @Provides
    fun provideHomeActivityPresenter(): HomeActivityContract.Presenter {
        return HomeActivityPresenter()
    }
}