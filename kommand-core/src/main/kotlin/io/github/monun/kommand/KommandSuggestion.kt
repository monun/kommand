package io.github.monun.kommand

interface KommandSuggestion {
    sealed interface Mode {
        object include : Mode
        object replace : Mode
    }
    fun suggest(value: Int, tooltip: String? = null)
    fun suggest(text: String, tooltip: String? = null)
}