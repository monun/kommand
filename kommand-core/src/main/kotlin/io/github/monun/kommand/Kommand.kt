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

    fun executes(executor: (KommandContext) -> Unit)

    fun then(name: String, init: Kommand.() -> Unit)
}

interface KommandContext {
    val source: KommandSource
}

interface KommandSource

class KommandArgument

