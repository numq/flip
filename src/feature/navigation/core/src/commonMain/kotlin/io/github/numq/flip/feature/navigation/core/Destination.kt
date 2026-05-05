package io.github.numq.flip.feature.navigation.core

sealed interface Destination {
    data object Generator : Destination
}