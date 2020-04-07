package com.github.noonmaru.kommand

class SuggestionBuilder(
    val target: String
) {
    internal val list = ArrayList<String>()

    fun add(suggestion: String) {
        list += suggestion
    }

    operator fun plusAssign(suggestion: String) {
        add(suggestion)
    }

    fun <T> addAll(source: Collection<String>) {
        list.addAll(source)
    }

    fun <T> addMatches(source: Collection<T>, transform: (T) -> String = { it.toString() }) {
        val target = this.target

        for (element in source) {
            val arg = transform(element)

            if (arg.startsWith(target)) {
                add(arg)
            }
        }
    }
}