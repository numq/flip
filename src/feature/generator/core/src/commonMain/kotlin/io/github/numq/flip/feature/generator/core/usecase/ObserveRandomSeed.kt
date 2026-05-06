package io.github.numq.flip.feature.generator.core.usecase

import arrow.core.raise.Raise
import io.github.numq.flip.common.core.usecase.UseCase
import io.github.numq.flip.feature.generator.core.GeneratorService
import io.github.numq.flip.feature.generator.core.RandomSeed
import io.github.numq.flip.feature.generator.core.toRandomSeed
import io.github.numq.flip.service.seed.SeedService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlin.time.Duration.Companion.milliseconds

class ObserveRandomSeed(
    val generatorService: GeneratorService, val seedService: SeedService,
) : UseCase.Query<Flow<RandomSeed>> {
    override suspend fun Raise<Throwable>.query() = combine(
        flow = generatorService.randomSeed, flow2 = seedService.seed
    ) { randomSeed, seed ->
        seed.toRandomSeed(localSeed = randomSeed.localSeed)
    }.onStart {
        delay(500.milliseconds)
    }
}