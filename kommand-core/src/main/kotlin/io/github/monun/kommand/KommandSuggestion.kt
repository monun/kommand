package io.github.monun.kommand

// 추천 빌더
interface KommandSuggestion {
    fun suggest(value: Int)

    fun suggest(text: String)
}