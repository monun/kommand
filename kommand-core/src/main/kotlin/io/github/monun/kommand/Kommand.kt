package io.github.monun.kommand

import io.github.monun.kommand.internal.Brigadier
import io.github.monun.kommand.internal.RootKommand


interface Kommand {
    companion object {
        fun register(name: String, vararg aliases: String, init: Kommand.() -> Unit) {
            Brigadier.nms.register(RootKommand(name, aliases.toList()).apply(init))
        }
    }

    fun requires(requires: (KommandSource) -> Boolean)

    fun executes(executor: (KommandContext) -> Int)

    fun then(name: String, init: Kommand.() -> Unit)

    fun then(argument: Pair<String, KommandArgument<*>>, init: Kommand.() -> Unit)
}

