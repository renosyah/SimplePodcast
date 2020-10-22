package com.renosyah.simplepodcast.di.component

import com.renosyah.simplepodcast.di.module.FragmentModule
import dagger.Component

@Component(modules = arrayOf(FragmentModule::class))
interface FragmentComponent {
    // add for each new fragment
}