package io.github.numq.flip.feature.generator.core

import io.github.numq.flip.service.seed.Seed

internal fun Seed.toRandomSeed(localSeed: Long) = RandomSeed(localSeed = localSeed, remoteSeed = value)