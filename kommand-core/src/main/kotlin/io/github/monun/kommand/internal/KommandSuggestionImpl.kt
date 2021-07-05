package io.github.monun.kommand.internal

import io.github.monun.kommand.KommandSuggestion

class KommandSuggestionImpl: KommandSuggestion {
    var suggestions = arrayListOf<Pair<Any, String?>>()

    override fun suggest(value: Int, tooltip: String?) {
        suggestions += value to tooltip
    }

    override fun suggest(text: String, tooltip: String?) {
        suggestions += text to tooltip
    }
}