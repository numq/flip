package io.github.numq.flip.service.seed

import io.github.numq.flip.common.core.di.ScopeQualifier
import io.github.numq.flip.common.core.di.scopedOwner
import org.koin.dsl.bind
import org.koin.dsl.module

val seedServiceModule = module {
    scope<ScopeQualifier.Type.Application> {
        scopedOwner {
            LocalSeedService()
        } bind SeedService::class
    }
}