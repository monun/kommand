package io.github.monun.kommand.internal

import io.github.monun.kommand.Kommand
import io.github.monun.kommand.KommandArgument
import io.github.monun.kommand.KommandContext
import io.github.monun.kommand.KommandSource

abstract class AbstractKommand(
    val name: String
) : Kommand {
    var requires: ((KommandSource) -> Boolean)? = null

    var executor: ((KommandContext) -> Int)? = null

    val nodes = arrayListOf<AbstractKommand>()

    override fun requires(requires: (KommandSource) -> Boolean) {
        this.requires = requires
    }

    override fun executes(executor: (KommandContext) -> Int) {
        this.executor = executor
    }

    override fun then(name: String, init: Kommand.() -> Unit) {
        nodes.add(LiteralKommand(name).apply(init))
    }

    override fun then(argument: Pair<String, KommandArgument<*>>, init: Kommand.() -> Unit) {
        nodes.add(ArgumentKommand(argument.first, argument.second).apply(init))
    }
}

