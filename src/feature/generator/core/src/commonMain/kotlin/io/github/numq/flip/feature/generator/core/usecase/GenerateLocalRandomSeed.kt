package io.github.numq.flip.feature.generator.core.usecase

import arrow.core.raise.Raise
import io.github.numq.flip.common.core.usecase.UseCase
import io.github.numq.flip.feature.generator.core.GeneratorService

class GenerateLocalRandomSeed(private val generatorService: GeneratorService) : UseCase.Action {
    override suspend fun Raise<Throwable>.action() = generatorService.generateRandomSeed().bind()
}