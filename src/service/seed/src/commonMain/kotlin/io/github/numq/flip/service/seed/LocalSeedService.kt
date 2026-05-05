package io.github.numq.flip.service.seed

import arrow.core.Either
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.consumeAsFlow
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

internal class LocalSeedService : SeedService {
    private companion object {
        const val MIN_DELAY_MS = 500L

        const val MAX_DELAY_MS = 1_500L
    }

    private val _seed = Channel<Seed>(Channel.CONFLATED)

    override val seed = _seed.consumeAsFlow()

    override suspend fun generateSeed() = Either.catch {
        val latency = (MIN_DELAY_MS..MAX_DELAY_MS).random()

        delay(latency.milliseconds)

        _seed.send(Seed(value = Random.nextLong()))
    }

    override fun close() {
        _seed.close()
    }
}