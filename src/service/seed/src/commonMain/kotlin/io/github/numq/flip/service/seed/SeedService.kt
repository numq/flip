package io.github.numq.flip.service.seed

import arrow.core.Either
import kotlinx.coroutines.flow.Flow

interface SeedService : AutoCloseable {
    val seed: Flow<Seed>

    suspend fun generateSeed(): Either<Throwable, Unit>
}