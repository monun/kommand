package io.github.monun.kommand

import io.github.monun.kommand.internal.KommandDispatcherImpl
import io.github.monun.kommand.loader.LibraryLoader
import io.github.monun.kommand.node.LiteralNode

interface Kommand {
    companion object {
        private val NMS = LibraryLoader.load(Kommand::class.java)

        fun register(
            name: String,
            vararg aliases: String,
            init: LiteralNode.() -> Unit
        ) = KommandDispatcherImpl().apply {
            initialize(name)
            root.init()
            isMutable = false
        }.also { NMS.register(it, aliases.toList()) }
    }

    fun register(dispatcher: KommandDispatcherImpl, aliases: List<String>)
}
