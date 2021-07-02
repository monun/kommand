package io.github.monun.kommand.internal

import io.github.monun.kommand.Kommand
import io.github.monun.kommand.KommandContext

abstract class AbstractKommand(
    val name: String
) : Kommand {
    val nodes = arrayListOf<AbstractKommand>()
    var executor: ((KommandContext) -> Unit)? = null

    override fun then(name: String, init: Kommand.() -> Unit) {
        nodes.add(LiteralKommand(name).apply(init))
    }

    override fun executes(executor: (KommandContext) -> Unit) {
        this.executor = executor
    }
}

open class LiteralKommand(name: String) : AbstractKommand(name)

class RootKommand(name: String, val aliases: Iterable<String>) : LiteralKommand(name)
