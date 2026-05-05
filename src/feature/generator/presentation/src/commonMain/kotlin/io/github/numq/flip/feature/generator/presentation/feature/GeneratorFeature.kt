package io.github.numq.flip.feature.generator.presentation.feature

import io.github.numq.flip.common.presentation.feature.Feature
import kotlinx.coroutines.CoroutineScope

internal class GeneratorFeature(
    scope: CoroutineScope, reducer: GeneratorReducer,
) : Feature<GeneratorState, GeneratorCommand, GeneratorEvent> by Feature(
    initialState = GeneratorState.Empty, scope = scope, reducer = reducer, GeneratorCommand.ObserveGenerator
)