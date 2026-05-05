package io.github.numq.flip.common.presentation.feature

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedSendChannelException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.fetchAndUpdate
import kotlin.concurrent.atomics.update
import kotlin.coroutines.cancellation.CancellationException

@OptIn(ExperimentalAtomicApi::class)
internal class ReducerFeature<State, in Command, out Event>(
    initialState: State,
    initialCommands: List<Command> = emptyList(),
    private val scope: CoroutineScope,
    private val reducer: Reducer<State, Command, Event>,
) : Feature<State, Command, Event> {
    private val isClosed = AtomicBoolean(false)

    private val jobs = AtomicReference(mapOf<Any, Job>())

    private val _commands = Channel<Command>(Channel.UNLIMITED)

    private val _events = MutableSharedFlow<Event>(0, Int.MAX_VALUE)

    override val events = _events.asSharedFlow()

    override val state = _commands.receiveAsFlow().scan(initialState) { state, command ->
        val transition = reducer.reduce(state, command)

        transition.effects.forEach(::processEffect)

        transition.events.forEach(_events::tryEmit)

        transition.state
    }.stateIn(scope = scope, started = SharingStarted.Eagerly, initialValue = initialState)

    init {
        initialCommands.forEach { command ->
            scope.launch { execute(command) }
        }
    }

    private fun processEffect(effect: Effect) {
        when (effect) {
            is Effect.Stream<*> -> launchManaged(effect.key) {
                try {
                    when (effect.strategy) {
                        Effect.Stream.Strategy.Sequential -> effect.flow.collect { cmd ->
                            @Suppress("UNCHECKED_CAST") execute(cmd as Command)
                        }

                        Effect.Stream.Strategy.Restart -> effect.flow.collectLatest { cmd ->
                            @Suppress("UNCHECKED_CAST") execute(cmd as Command)
                        }
                    }
                } catch (exception: CancellationException) {
                    throw exception
                } catch (throwable: Throwable) {
                    val cmd = effect.fallback?.invoke(throwable)

                    if (cmd != null) {
                        @Suppress("UNCHECKED_CAST") execute(cmd as Command)
                    }
                }
            }

            is Effect.Action<*> -> launchManaged(effect.key) {
                val cmd = try {
                    effect.block()
                } catch (exception: CancellationException) {
                    throw exception
                } catch (throwable: Throwable) {
                    effect.fallback?.invoke(throwable)
                }

                if (cmd != null) {
                    @Suppress("UNCHECKED_CAST") execute(cmd as Command)
                }
            }

            is Effect.Cancel -> cancelJob(effect.key)
        }
    }

    private fun launchManaged(key: Any, block: suspend () -> Unit) {
        val newJob = scope.launch { block() }

        val oldJob = jobs.fetchAndUpdate { current ->
            current + Pair(key, newJob)
        }[key]

        oldJob?.cancel()

        newJob.invokeOnCompletion {
            jobs.update { current ->
                when {
                    current[key] === newJob -> current - key

                    else -> current
                }
            }
        }
    }

    private fun cancelJob(key: Any) {
        jobs.update { current ->
            current[key]?.cancel()

            current - key
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override suspend fun execute(command: Command) {
        if (isClosed.load()) return

        try {
            _commands.send(command)
        } catch (_: ClosedSendChannelException) {
        }
    }

    override fun close() {
        if (!isClosed.compareAndSet(false, true)) return

        scope.cancel()

        _commands.close()

        jobs.update { current ->
            current.values.forEach(Job::cancel)

            emptyMap()
        }
    }
}