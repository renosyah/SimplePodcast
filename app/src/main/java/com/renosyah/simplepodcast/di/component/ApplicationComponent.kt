package com.renosyah.simplepodcast.di.component

import com.renosyah.simplepodcast.BaseApp
import com.renosyah.simplepodcast.di.module.ApplicationModule
import dagger.Component

@Component(modules = arrayOf(ApplicationModule::class))
interface ApplicationComponent {
    fun inject(application: BaseApp)
}