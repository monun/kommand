package io.github.monun.kommand

import io.github.monun.kommand.internal.Brigadier
import io.github.monun.kommand.internal.KommandDispatcherImpl
import org.bukkit.plugin.java.JavaPlugin


interface KommandDispatcher {
    fun register(name: String, vararg aliases: String, init: LiteralKommand.() -> Unit)
}

interface Kommand {
    fun then(name: String, vararg aliases: String, init: LiteralKommand.() -> Unit)

    fun executes(executor: (KommandContext) -> Unit)
}

interface TerminalKommand: Kommand {
    val name: String
}

interface LiteralKommand : TerminalKommand

class KommandContext

fun JavaPlugin.kommand(init: KommandDispatcher.() -> Unit) {
    Brigadier.nms.register(KommandDispatcherImpl().apply(init))
}