package io.github.monun.kommand.internal

import io.github.monun.kommand.KommandArgument
import io.github.monun.kommand.node.ArgumentNode

class ArgumentNodeImpl : AbstractKommandNode(), ArgumentNode {
    lateinit var argument: KommandArgument<*>

    internal fun initialize(kommand: KommandDispatcherImpl, name: String, argument: KommandArgument<*>) {
        initialize0(kommand, name)
        this.argument = argument
    }
}

