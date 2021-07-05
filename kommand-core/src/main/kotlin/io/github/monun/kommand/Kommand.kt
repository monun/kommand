package io.github.monun.kommand

import io.github.monun.kommand.internal.KommandImpl

// API
interface Kommand {
    companion object {
        fun register(
            name: String,
            vararg aliases: String,
            init: RootNode.() -> Unit
        ) = KommandImpl().apply {
            initialize(name, aliases.toList())
            root.init()
            register()
        }
    }
}
