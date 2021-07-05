package io.github.monun.kommand.internal

import io.github.monun.kommand.ArgumentNode
import io.github.monun.kommand.KommandArgument

class ArgumentNodeImpl : KommandNodeImpl(), ArgumentNode {
    lateinit var argument: KommandArgument

    internal fun initialize(kommand: KommandImpl, name: String, argument: KommandArgument) {
        initialize0(kommand, name)
        this.argument = argument
    }
}

