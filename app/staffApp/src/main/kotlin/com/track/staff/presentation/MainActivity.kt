package com.track.staff.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.track.staff.presentation.navigation.StaffNavHost
import com.track.ui.theme.AppTheme
import com.track.util.FirebaseInitializer
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var firebaseInitializer: FirebaseInitializer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // One-time setup for internal accounts
        firebaseInitializer.initializeInternalUsers()

        setContent {
            AppTheme {
                StaffNavHost()
            }
        }
    }
}
