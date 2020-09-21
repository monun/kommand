package com.github.noonmaru.kommand.argument

import com.github.noonmaru.kommand.KommandContext

class MapArgument<T> internal constructor(
    private val map: Map<String, T>
) : KommandArgument<T> {
    override fun parse(context: KommandContext, param: String): T? {
        return map[param]
    }

    override fun listSuggestion(context: KommandContext, target: String): Collection<String> {
        return map.keys.suggestions(target)
    }
}