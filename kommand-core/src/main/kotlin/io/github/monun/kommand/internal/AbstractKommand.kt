package io.github.monun.kommand.internal

import io.github.monun.kommand.Kommand
import io.github.monun.kommand.node.LiteralNode

abstract class AbstractKommand : Kommand {
    override fun register(
        name: String,
        vararg aliases: String,
        init: LiteralNode.() -> Unit
    ) {
        KommandDispatcherImpl().apply {
            initialize(name)
            root.init()
            isMutable = false
        }.let {
            register(it, aliases.toList())
        }
    }

    protected abstract fun register(dispatcher: KommandDispatcherImpl, aliases: List<String>)
}