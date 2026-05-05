package io.github.numq.flip.platform.web

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import io.github.numq.flip.entrypoint.Entrypoint

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport {
        Entrypoint.Initialize()
    }
}