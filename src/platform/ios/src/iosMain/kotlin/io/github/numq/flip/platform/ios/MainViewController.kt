package io.github.numq.flip.platform.ios

import androidx.compose.ui.window.ComposeUIViewController
import io.github.numq.flip.entrypoint.Entrypoint

fun MainViewController() = ComposeUIViewController {
    Entrypoint.Initialize()
}