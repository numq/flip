package io.github.numq.flip.common.core.di

import org.koin.core.qualifier.named

object ScopeQualifier {
    sealed interface Type {
        data object Application : Type
    }

    val Application = named<Type.Application>()
}