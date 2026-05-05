package io.github.numq.flip.feature.generator.presentation.feature

import io.github.numq.flip.common.presentation.feature.*
import io.github.numq.flip.feature.generator.core.usecase.GenerateLocalRandomSeed
import io.github.numq.flip.feature.generator.core.usecase.GenerateRemoteRandomSeed
import io.github.numq.flip.feature.generator.core.usecase.ObserveRandomSeed
import kotlinx.coroutines.flow.map

internal class GeneratorReducer(
    private val observeRandomSeed: ObserveRandomSeed,
    private val generateLocalRandomSeed: GenerateLocalRandomSeed,
    private val generateRemoteRandomSeed: GenerateRemoteRandomSeed,
) : Reducer<GeneratorState, GeneratorCommand, GeneratorEvent> {
    private fun requestLocalUpdate(key: GeneratorCommand.Key) = action(
        key = key, fallback = GeneratorCommand::HandleFailure, block = {
            generateLocalRandomSeed(input = Unit).fold(
                ifLeft = GeneratorCommand::HandleFailure, ifRight = { GeneratorCommand.GenerateLocalRandomSeedSuccess })
        })

    private fun requestRemoteUpdate(key: GeneratorCommand.Key) = action(
        key = key, fallback = GeneratorCommand::HandleFailure, block = {
            generateRemoteRandomSeed(input = Unit).fold(
                ifLeft = GeneratorCommand::HandleFailure, ifRight = { GeneratorCommand.GenerateLocalRandomSeedSuccess })
        })

    override fun reduce(
        state: GeneratorState, command: GeneratorCommand,
    ): Transition<GeneratorState, GeneratorEvent> = when (command) {
        is GeneratorCommand.HandleFailure -> transition(state).event(GeneratorEvent.HandleFailure(throwable = command.throwable))

        is GeneratorCommand.ObserveGenerator -> transition(state).effect(
            action(
                key = command.key, fallback = GeneratorCommand::HandleFailure, block = {
                    observeRandomSeed(input = Unit).fold(
                        ifLeft = GeneratorCommand::HandleFailure, ifRight = GeneratorCommand::ObserveGeneratorSuccess
                    )
                })
        )

        is GeneratorCommand.ObserveGeneratorSuccess -> transition(state).effect(
            effect = stream(
                key = command.key, flow = command.flow.map(GeneratorCommand::UpdateGenerator), fallback = { throwable ->
                    GeneratorCommand.HandleFailure(throwable = throwable)
                })
        )

        is GeneratorCommand.UpdateGenerator -> when (state) {
            is GeneratorState.Empty -> transition(state)

            is GeneratorState.Loading -> transition(GeneratorState.Ready(randomSeed = command.randomSeed))

            is GeneratorState.Ready -> transition(state.copy(randomSeed = command.randomSeed))
        }

        is GeneratorCommand.GenerateLocalRandomSeed -> transition(state).effect(
            effect = action(
                key = command.key, fallback = GeneratorCommand::HandleFailure, block = {
                    generateLocalRandomSeed(input = Unit).fold(
                        ifLeft = GeneratorCommand::HandleFailure,
                        ifRight = { GeneratorCommand.GenerateLocalRandomSeedSuccess })
                })
        )

        is GeneratorCommand.GenerateLocalRandomSeedSuccess -> transition(state)

        is GeneratorCommand.GenerateRemoteRandomSeed -> when (state) {
            is GeneratorState.Empty -> transition(state)

            is GeneratorState.Loading -> transition(state)

            is GeneratorState.Ready -> transition(state.copy(isRemoteSeedRequested = true)).effect(
                effect = action(
                    key = command.key, fallback = GeneratorCommand::HandleFailure, block = {
                        generateRemoteRandomSeed(input = Unit).fold(
                            ifLeft = GeneratorCommand::HandleFailure,
                            ifRight = { GeneratorCommand.GenerateRemoteRandomSeedSuccess })
                    })
            )
        }

        is GeneratorCommand.GenerateRemoteRandomSeedSuccess -> when (state) {
            is GeneratorState.Empty -> transition(state)

            is GeneratorState.Loading -> transition(state)

            is GeneratorState.Ready -> transition(state.copy(isRemoteSeedRequested = false))
        }

        is GeneratorCommand.GetRandomSeed -> when (state) {
            is GeneratorState.Empty -> transition(GeneratorState.Loading).effects(
                requestLocalUpdate(key = command.key), requestRemoteUpdate(key = command.key)
            )

            is GeneratorState.Loading -> transition(state)

            is GeneratorState.Ready -> transition(state)
        }
    }
}