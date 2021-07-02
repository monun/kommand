package io.github.monun.kommand

import io.github.monun.kommand.internal.Brigadier
import io.github.monun.kommand.internal.LiteralKommandImpl


interface Kommand {
    companion object {
        fun register(name: String, vararg aliases: String, init: Kommand.() -> Unit) {
            Brigadier.nms.register(LiteralKommandImpl(name, aliases.toList()).apply(init))
        }
    }

    fun then(name: String, vararg aliases: String, init: LiteralKommand.() -> Unit)

    fun executes(executor: (KommandContext) -> Unit)
}

interface TerminalKommand : Kommand {
    val name: String
}

interface LiteralKommand : TerminalKommand

class KommandContext