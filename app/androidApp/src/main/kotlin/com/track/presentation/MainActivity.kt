package com.track.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.track.presentation.navigation.AppNavHost
import com.track.presentation.viewmodel.AppAuthViewModel
import com.track.presentation.viewmodel.AppCustomerViewModel
import com.track.presentation.viewmodel.AppSuperAdminViewModel
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
        enableEdgeToEdge()

        try {
            firebaseInitializer.initializeInternalUsers()
        } catch (e: Exception) {
            Log.e("MainActivity", "Error in FirebaseInitializer", e)
        }

        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    AppNavHost(
                        authViewModel = hiltViewModel<AppAuthViewModel>(),
                        customerViewModel = hiltViewModel<AppCustomerViewModel>(),
                        adminViewModel = hiltViewModel<AppSuperAdminViewModel>()
                    )
                }
            }
        }
    }
}
