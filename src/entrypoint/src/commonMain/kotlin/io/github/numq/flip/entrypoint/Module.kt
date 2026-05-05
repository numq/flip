package io.github.numq.flip.entrypoint

import io.github.numq.flip.feature.generator.core.generatorFeatureCoreModule
import io.github.numq.flip.feature.generator.presentation.generatorFeaturePresentationModule
import io.github.numq.flip.feature.navigation.core.navigationFeatureCoreModule
import io.github.numq.flip.feature.navigation.presentation.navigationFeaturePresentationModule
import io.github.numq.flip.service.seed.seedServiceModule
import org.koin.dsl.module

private val feature = module {
    includes(navigationFeatureCoreModule)
    includes(navigationFeaturePresentationModule)
    includes(generatorFeatureCoreModule)
    includes(generatorFeaturePresentationModule)
}

private val service = module {
    includes(seedServiceModule)
}

val applicationModule = feature + service