package io.github.numq.flip.feature.generator.core

import arrow.core.Either
import kotlinx.coroutines.flow.Flow

interface GeneratorService : AutoCloseable {
    val randomSeed: Flow<RandomSeed>

    suspend fun generateRandomSeed(): Either<Throwable, Unit>
}