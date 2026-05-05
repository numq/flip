package io.github.numq.flip.platform.desktop

import androidx.compose.runtime.SideEffect
import androidx.compose.ui.window.singleWindowApplication
import io.github.numq.flip.entrypoint.Entrypoint
import java.awt.Dimension

private const val WINDOW_WIDTH = 512

private const val WINDOW_HEIGHT = 768

internal fun main() = singleWindowApplication {
    SideEffect {
        val windowSize = Dimension(WINDOW_WIDTH, WINDOW_HEIGHT)

        window.apply {
            minimumSize = windowSize

            size = windowSize
        }
    }

    Entrypoint.Initialize()
}