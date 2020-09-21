package com.github.noonmaru.kommand.argument

import com.github.noonmaru.kommand.KommandContext
import com.google.common.collect.ImmutableList

class StringArgument internal constructor(
    private val values: () -> Collection<String>
) : KommandArgument<String> {
    override fun parse(context: KommandContext, param: String): String? {
        val values = values()

        return param.takeIf { values.isEmpty() || param in values }
    }

    override fun listSuggestion(context: KommandContext, target: String): Collection<String> {
        return values().suggestions(target)
    }

    companion object {
        internal val emptyStringArgument = StringArgument { ImmutableList.of() }
    }
}