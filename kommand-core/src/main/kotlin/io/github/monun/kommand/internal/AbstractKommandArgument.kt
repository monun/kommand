package io.github.monun.kommand.internal

import io.github.monun.kommand.KommandArgument
import io.github.monun.kommand.KommandContext
import io.github.monun.kommand.KommandSuggestion

abstract class AbstractKommandArgument<T> : KommandArgument<T> {
    var suggestionProvider: (KommandSuggestion.(KommandContext) -> Unit)? = null

    override fun suggests(provider: KommandSuggestion.(context: KommandContext) -> Unit) {
        this.suggestionProvider = provider
    }
}