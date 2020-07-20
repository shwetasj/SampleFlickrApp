package com.example.sampleflickrapp

import android.app.Activity
import android.app.Application
import com.example.sampleflickrapp.di.AppInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

class FlickrApp : Application(), HasActivityInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun activityInjector() = dispatchingAndroidInjector


    override fun onCreate() {
        super.onCreate()

        AppInjector.init(this)
    }

}