package io.github.numq.flip.feature.generator.presentation.feature

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.core.scope.Scope

@Composable
fun GeneratorView(applicationScope: Scope, handleError: (Throwable) -> Unit) {
    val scope = rememberCoroutineScope()

    val feature = koinInject<GeneratorFeature>(scope = applicationScope)

    val state by feature.state.collectAsState()

    LaunchedEffect(Unit) {
        feature.events.collect { event ->
            when (event) {
                is GeneratorEvent.HandleFailure -> handleError(event.throwable)
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 8.dp, alignment = Alignment.CenterVertically)
    ) {
        when (val currentState = state) {
            is GeneratorState.Empty -> TextButton(onClick = {
                scope.launch {
                    feature.execute(GeneratorCommand.GetRandomSeed)
                }
            }) {
                Text(text = "Get random seed", style = MaterialTheme.typography.labelLarge)
            }

            is GeneratorState.Loading -> CircularProgressIndicator()

            is GeneratorState.Ready -> Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card {
                    Column(
                        modifier = Modifier.clickable {
                            scope.launch {
                                feature.execute(GeneratorCommand.GenerateLocalRandomSeed)
                            }
                        }.width(256.dp).height(128.dp).padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(
                            space = 8.dp, alignment = Alignment.CenterVertically
                        )
                    ) {
                        Box(modifier = Modifier.weight(1f).padding(8.dp), contentAlignment = Alignment.Center) {
                            Text(text = "Local random seed", style = MaterialTheme.typography.titleSmall)
                        }
                        Box(modifier = Modifier.weight(1f).padding(8.dp), contentAlignment = Alignment.Center) {
                            Text(
                                text = "${currentState.randomSeed.localSeed}",
                                style = MaterialTheme.typography.headlineSmall
                            )
                        }
                    }
                }
                Card {
                    Column(
                        modifier = Modifier.clickable(enabled = !currentState.isRemoteSeedRequested) {
                            scope.launch {
                                feature.execute(GeneratorCommand.GenerateRemoteRandomSeed)
                            }
                        }.width(256.dp).height(128.dp).padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(modifier = Modifier.weight(1f).padding(8.dp), contentAlignment = Alignment.Center) {
                            Text(text = "Remote random seed", style = MaterialTheme.typography.titleSmall)
                        }
                        Box(modifier = Modifier.weight(1f).padding(8.dp), contentAlignment = Alignment.Center) {
                            when {
                                currentState.isRemoteSeedRequested -> LinearProgressIndicator()

                                else -> Text(
                                    text = "${currentState.randomSeed.remoteSeed}",
                                    style = MaterialTheme.typography.headlineSmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}