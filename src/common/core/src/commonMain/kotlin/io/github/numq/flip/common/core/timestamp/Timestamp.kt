package io.github.numq.flip.common.core.timestamp

import kotlin.jvm.JvmInline
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@JvmInline
value class Timestamp(val nanoseconds: Long) : Comparable<Timestamp> {
    @OptIn(ExperimentalTime::class)
    companion object {
        fun now() = Clock.System.now().let { instant ->
            Timestamp(nanoseconds = (instant.epochSeconds * 1_000_000_000L) + instant.nanosecondsOfSecond)
        }
    }

    operator fun minus(other: Timestamp) = nanoseconds - other.nanoseconds

    override fun compareTo(other: Timestamp) = nanoseconds.compareTo(other.nanoseconds)
}