package io.github.numq.flip.common.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable

@Composable
fun ApplicationTheme(isDark: Boolean, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = when {
            isDark -> DarkColorScheme

            else -> LightColorScheme
        }, typography = Typography(), shapes = Shapes(), content = content
    )
}