package com.track

import android.app.Application
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp // 👈 CRITICAL: This starts Hilt
class TrackApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Firebase for this application
        FirebaseApp.initializeApp(this)
    }
}
