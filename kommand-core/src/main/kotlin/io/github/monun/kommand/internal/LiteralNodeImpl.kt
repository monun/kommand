package io.github.monun.kommand.internal

import io.github.monun.kommand.LiteralNode

open class LiteralNodeImpl : KommandNodeImpl(), LiteralNode {
    internal fun initialize(kommand: KommandImpl, name: String) {
        initialize0(kommand, name)
    }
}