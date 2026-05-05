package io.github.numq.flip.feature.generator.presentation.feature

import io.github.numq.flip.feature.generator.core.RandomSeed
import kotlinx.coroutines.flow.Flow

internal sealed interface GeneratorCommand {
    enum class Key {
        OBSERVE_RANDOM_SEED, OBSERVE_RANDOM_SEED_SUCCESS, GENERATE_LOCAL_RANDOM_SEED, GENERATE_REMOTE_RANDOM_SEED, GET_RANDOM_SEED
    }

    data class HandleFailure(val throwable: Throwable) : GeneratorCommand

    data object ObserveGenerator : GeneratorCommand {
        val key = Key.OBSERVE_RANDOM_SEED
    }

    data class ObserveGeneratorSuccess(val flow: Flow<RandomSeed>) : GeneratorCommand {
        val key = Key.OBSERVE_RANDOM_SEED_SUCCESS
    }

    data class UpdateGenerator(val randomSeed: RandomSeed) : GeneratorCommand

    data object GenerateLocalRandomSeed : GeneratorCommand {
        val key = Key.GENERATE_LOCAL_RANDOM_SEED
    }

    data object GenerateLocalRandomSeedSuccess : GeneratorCommand

    data object GenerateRemoteRandomSeed : GeneratorCommand {
        val key = Key.GENERATE_REMOTE_RANDOM_SEED
    }

    data object GenerateRemoteRandomSeedSuccess : GeneratorCommand

    data object GetRandomSeed : GeneratorCommand {
        val key = Key.GET_RANDOM_SEED
    }
}