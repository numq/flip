package io.github.numq.flip.feature.generator.core

import arrow.core.Either
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlin.random.Random

internal class LocalGeneratorService : GeneratorService {
    private val _randomSeed = Channel<RandomSeed>(Channel.CONFLATED)

    override val randomSeed = _randomSeed.consumeAsFlow()

    override suspend fun generateRandomSeed() = Either.catch {
        _randomSeed.send(RandomSeed(localSeed = Random.nextLong(), remoteSeed = null))
    }

    override fun close() {
        _randomSeed.close()
    }
}