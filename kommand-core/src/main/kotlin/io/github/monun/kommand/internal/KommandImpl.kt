package io.github.monun.kommand.internal

import io.github.monun.kommand.Kommand
import io.github.monun.kommand.KommandContext
import io.github.monun.kommand.LiteralKommand
import io.github.monun.kommand.TerminalKommand

abstract class KommandImpl : Kommand {
    val nodes = arrayListOf<KommandImpl>()
    var executor: ((KommandContext) -> Unit)? = null

    override fun then(name: String, vararg aliases: String, init: LiteralKommand.() -> Unit) {
        nodes.add(LiteralKommandImpl(name, aliases.toList()).apply(init))
    }

    override fun executes(executor: (KommandContext) -> Unit) {
        this.executor = executor
    }
}

abstract class TerminalKommandImpl(override val name: String) : KommandImpl(), TerminalKommand

class LiteralKommandImpl(name: String, val aliases: List<String>) : TerminalKommandImpl(name), LiteralKommand