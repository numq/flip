package io.github.numq.flip.feature.generator.presentation.feature

import io.github.numq.flip.feature.generator.core.RandomSeed

internal sealed interface GeneratorState {
    data object Empty : GeneratorState

    data object Loading : GeneratorState

    data class Ready(val randomSeed: RandomSeed, val isRemoteSeedRequested: Boolean = false) : GeneratorState
}