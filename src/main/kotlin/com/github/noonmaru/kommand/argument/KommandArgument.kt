package com.github.noonmaru.kommand.argument

import com.github.noonmaru.kommand.KommandContext
import com.github.noonmaru.kommand.SuggestionBuilder

interface KommandArgument {
    fun listSuggestion(context: KommandContext, builder: SuggestionBuilder) {}
}

object Argument : KommandArgument