package io.github.monun.kommand.internal

import io.github.monun.kommand.RootNode

open class RootNodeImpl : LiteralNodeImpl(), RootNode {
    lateinit var aliases: List<String>

    internal fun initialize(kommand: KommandImpl, name: String, aliases: List<String>) {
        initialize0(kommand, name)
        this.aliases = aliases
    }
}