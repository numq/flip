package io.github.numq.flip.feature.generator.presentation.feature

internal sealed interface GeneratorEvent {
    data class HandleFailure(val throwable: Throwable) : GeneratorEvent
}