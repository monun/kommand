package com.github.noonmaru.kommand

interface KommandArgument {
    fun listSuggestion(context: KommandContext, builder: SuggestionBuilder)
}