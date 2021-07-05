package io.github.monun.kommand

interface KommandArgument<T> {
    companion object {

    }

    fun suggests(
        mode: KommandSuggestion.Mode = KommandSuggestion.Mode.include,
        provider: (context: KommandContext, suggestion: KommandSuggestion) -> Unit
    )
}

