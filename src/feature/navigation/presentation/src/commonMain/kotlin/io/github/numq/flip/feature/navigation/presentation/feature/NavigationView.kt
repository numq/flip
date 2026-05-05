package io.github.numq.flip.feature.navigation.presentation.feature

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import io.github.numq.flip.feature.navigation.core.Destination
import org.koin.compose.koinInject
import org.koin.core.scope.Scope

@Composable
fun NavigationView(applicationScope: Scope, handleError: (Throwable) -> Unit, generator: @Composable () -> Unit) {
    val feature = koinInject<NavigationFeature>(scope = applicationScope)

    val state by feature.state.collectAsState()

    LaunchedEffect(Unit) {
        feature.events.collect { event ->
            when (event) {
                is NavigationEvent.HandleFailure -> handleError(event.throwable)
            }
        }
    }

    when (state.destination) {
        is Destination.Generator -> generator()
    }
}