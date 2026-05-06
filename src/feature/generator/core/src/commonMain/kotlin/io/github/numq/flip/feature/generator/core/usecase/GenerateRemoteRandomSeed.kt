package io.github.numq.flip.feature.generator.core.usecase

import arrow.core.raise.Raise
import io.github.numq.flip.common.core.usecase.UseCase
import io.github.numq.flip.service.seed.SeedService

class GenerateRemoteRandomSeed(private val seedService: SeedService) : UseCase.Action {
    override suspend fun Raise<Throwable>.action() = seedService.generateSeed().bind()
}