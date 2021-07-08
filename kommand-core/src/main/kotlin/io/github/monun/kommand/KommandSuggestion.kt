package io.github.monun.kommand

import net.kyori.adventure.text.ComponentLike

// 추천 빌더
interface KommandSuggestion {
    fun suggestsDefault()

    fun suggest(value: Int, tooltip: (() -> ComponentLike)? = null)

    fun suggest(text: String, tooltip: (() -> ComponentLike)? = null)
}