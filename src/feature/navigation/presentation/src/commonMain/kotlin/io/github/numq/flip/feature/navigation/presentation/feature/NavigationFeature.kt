package io.github.numq.flip.feature.navigation.presentation.feature

import io.github.numq.flip.common.presentation.feature.Feature
import io.github.numq.flip.feature.navigation.core.Destination
import kotlinx.coroutines.CoroutineScope

internal class NavigationFeature(
    scope: CoroutineScope, reducer: NavigationReducer,
) : Feature<NavigationState, NavigationCommand, NavigationEvent> by Feature(
    initialState = NavigationState(destination = Destination.Generator), scope = scope, reducer = reducer
)