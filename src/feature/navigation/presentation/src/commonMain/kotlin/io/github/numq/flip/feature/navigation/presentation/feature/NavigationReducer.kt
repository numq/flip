package io.github.numq.flip.feature.navigation.presentation.feature

import io.github.numq.flip.common.presentation.feature.Reducer
import io.github.numq.flip.common.presentation.feature.event

internal class NavigationReducer : Reducer<NavigationState, NavigationCommand, NavigationEvent> {
    override fun reduce(state: NavigationState, command: NavigationCommand) = when (command) {
        is NavigationCommand.HandleFailure -> transition(state).event(NavigationEvent.HandleFailure(throwable = command.throwable))

        is NavigationCommand.NavigateTo -> transition(state.copy(destination = command.destination))
    }
}