package io.github.monun.kommand.internal

import io.github.monun.kommand.node.LiteralNode

open class LiteralNodeImpl : AbstractKommandNode(), LiteralNode {
    internal fun initialize(kommand: KommandDispatcherImpl, name: String) {
        initialize0(kommand, name)
    }
}