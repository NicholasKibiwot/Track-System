package com.track

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.track.desktop.DesktopApp

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Track Desktop",
    ) {
        DesktopApp()
    }
}
