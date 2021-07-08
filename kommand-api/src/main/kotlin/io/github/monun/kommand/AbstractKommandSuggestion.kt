package io.github.monun.kommand

abstract class AbstractKommandSuggestion : KommandSuggestion {
    var suggestsDefault = false

    override fun suggestsDefault() {
        suggestsDefault = true
    }
}