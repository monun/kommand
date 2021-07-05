package io.github.monun.kommand.internal

import io.github.monun.kommand.KommandArgument
import io.github.monun.kommand.KommandContext
import io.github.monun.kommand.KommandSource
import io.github.monun.kommand.KommandSuggestion

abstract class AbstractKommandArgument : KommandArgument {
    var suggestion: Pair<KommandSuggestion.Mode, (context: KommandContext, suggestion: KommandSuggestion) -> Unit>? = null

    override fun suggests(
        mode: KommandSuggestion.Mode,
        provider: (context: KommandContext, suggestion: KommandSuggestion) -> Unit
    ) {
        this.suggestion = mode to provider
    }
}