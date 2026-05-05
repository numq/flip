package io.github.numq.flip.entrypoint

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import io.github.numq.flip.common.core.di.ScopeQualifier
import io.github.numq.flip.common.presentation.theme.ApplicationTheme
import io.github.numq.flip.feature.generator.presentation.feature.GeneratorView
import io.github.numq.flip.feature.navigation.presentation.feature.NavigationView
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import org.koin.core.context.startKoin
import org.koin.mp.KoinPlatform.getKoin

object Entrypoint {
    private const val APPLICATION_SCOPE_ID = "APPLICATION"

    init {
        startKoin {
            allowOverride(false)

            modules(applicationModule)
        }
    }

    @Composable
    fun Initialize() {
        val koin = getKoin()

        val applicationScope = remember {
            val qualifier = ScopeQualifier.Application

            koin.getOrCreateScope(
                scopeId = APPLICATION_SCOPE_ID, qualifier = qualifier, source = APPLICATION_SCOPE_ID
            ).apply {
                declare(instance = id, qualifier = qualifier)
            }
        }

        DisposableEffect(applicationScope.id) {
            onDispose {
                applicationScope.close()
            }
        }

        val scope = rememberCoroutineScope()

        val hostState = remember { SnackbarHostState() }

        val errors = remember { Channel<Throwable>(Channel.CONFLATED) }

        DisposableEffect(Unit) {
            var job: Job? = null

            val channelJob = scope.launch {
                for (error in errors) {
                    job?.cancel()

                    job = scope.launch {
                        hostState.showSnackbar(message = error.message ?: "Something went wrong")
                    }
                }
            }

            onDispose {
                channelJob.cancel()

                errors.close()
            }
        }

        ApplicationTheme(isDark = isSystemInDarkTheme()) {
            Scaffold(modifier = Modifier.fillMaxSize(), snackbarHost = {
                SnackbarHost(hostState = hostState)
            }) { paddingValues ->
                Surface(modifier = Modifier.padding(paddingValues)) {
                    NavigationView(
                        applicationScope = applicationScope, handleError = errors::trySend, generator = {
                            GeneratorView(applicationScope = applicationScope, handleError = errors::trySend)
                        })
                }
            }
        }
    }
}