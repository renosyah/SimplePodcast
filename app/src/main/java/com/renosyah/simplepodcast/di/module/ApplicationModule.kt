package com.renosyah.simplepodcast.di.module

import android.app.Application
import com.renosyah.simplepodcast.BaseApp
import com.renosyah.simplepodcast.di.scope.PerApplication
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class ApplicationModule(private val baseApp: BaseApp) {

    @Provides
    @Singleton
    @PerApplication
    fun provideApplication(): Application {
        return baseApp
    }
}