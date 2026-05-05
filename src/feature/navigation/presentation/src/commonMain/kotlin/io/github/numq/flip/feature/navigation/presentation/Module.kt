package io.github.numq.flip.feature.navigation.presentation

import io.github.numq.flip.common.core.di.ScopeQualifier
import io.github.numq.flip.common.core.di.scopedOwner
import io.github.numq.flip.feature.navigation.presentation.feature.NavigationFeature
import io.github.numq.flip.feature.navigation.presentation.feature.NavigationReducer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.dsl.module

val navigationFeaturePresentationModule = module {
    scope<ScopeQualifier.Type.Application> {
        scopedOwner {
            NavigationReducer()
        }

        scopedOwner {
            val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

            NavigationFeature(scope = scope, reducer = get())
        }
    }
}