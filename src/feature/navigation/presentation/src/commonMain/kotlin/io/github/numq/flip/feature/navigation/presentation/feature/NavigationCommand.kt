package io.github.numq.flip.feature.navigation.presentation.feature

import io.github.numq.flip.feature.navigation.core.Destination

internal sealed interface NavigationCommand {
    data class HandleFailure(val throwable: Throwable) : NavigationCommand

    data class NavigateTo(val destination: Destination) : NavigationCommand
}