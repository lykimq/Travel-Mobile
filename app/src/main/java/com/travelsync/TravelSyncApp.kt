package com.travelsync

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TravelSyncApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize other global services if necessary
    }
}
