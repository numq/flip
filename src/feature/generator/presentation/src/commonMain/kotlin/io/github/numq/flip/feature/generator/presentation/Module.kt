package io.github.numq.flip.feature.generator.presentation

import io.github.numq.flip.common.core.di.ScopeQualifier
import io.github.numq.flip.common.core.di.scopedOwner
import io.github.numq.flip.feature.generator.presentation.feature.GeneratorFeature
import io.github.numq.flip.feature.generator.presentation.feature.GeneratorReducer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.dsl.module

val generatorFeaturePresentationModule = module {
    scope<ScopeQualifier.Type.Application> {
        scopedOwner {
            GeneratorReducer(
                observeRandomSeed = get(), generateLocalRandomSeed = get(), generateRemoteRandomSeed = get()
            )
        }

        scopedOwner {
            val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

            GeneratorFeature(scope = scope, reducer = get())
        }
    }
}