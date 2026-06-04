package com.track.common.base

import android.app.Application
import com.google.firebase.FirebaseApp

open class TrackApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Firebase for this application
        FirebaseApp.initializeApp(this)
    }
}
