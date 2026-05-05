package io.github.numq.flip.feature.generator.core

import io.github.numq.flip.common.core.di.ScopeQualifier
import io.github.numq.flip.common.core.di.scopedOwner
import io.github.numq.flip.feature.generator.core.usecase.GenerateLocalRandomSeed
import io.github.numq.flip.feature.generator.core.usecase.GenerateRemoteRandomSeed
import io.github.numq.flip.feature.generator.core.usecase.ObserveRandomSeed
import org.koin.dsl.bind
import org.koin.dsl.module

val generatorFeatureCoreModule = module {
    scope<ScopeQualifier.Type.Application> {
        scopedOwner {
            LocalGeneratorService()
        } bind GeneratorService::class

        scopedOwner {
            ObserveRandomSeed(generatorService = get(), seedService = get())
        }

        scopedOwner {
            GenerateLocalRandomSeed(generatorService = get())
        }

        scopedOwner {
            GenerateRemoteRandomSeed(seedService = get())
        }
    }
}