package com.renosyah.simplepodcast.di.component

import com.renosyah.simplepodcast.di.module.ActivityModule
import com.renosyah.simplepodcast.ui.activity.home.HomeActivity
import dagger.Component

@Component(modules = arrayOf(ActivityModule::class))
interface ActivityComponent {
    // add for each new activity
    fun inject(homeActivity: HomeActivity)
}