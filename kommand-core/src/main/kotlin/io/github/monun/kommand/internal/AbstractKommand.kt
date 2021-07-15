package io.github.monun.kommand.internal

import io.github.monun.kommand.Kommand
import io.github.monun.kommand.node.LiteralNode
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.server.PluginDisableEvent
import org.bukkit.plugin.Plugin

abstract class AbstractKommand : Kommand {
    override fun register(
        plugin: Plugin,
        name: String,
        vararg aliases: String,
        init: LiteralNode.() -> Unit
    ) {
        require(plugin.isEnabled) { "Plugin disabled!" }

        KommandDispatcherImpl().apply {
            initialize(name)
            root.init()
            isMutable = false
        }.let {
            register(it, aliases.toList())
        }

        plugin.server.pluginManager.registerEvents(
            object : Listener {
                @EventHandler(priority = EventPriority.LOWEST)
                fun onPluginDisable(event: PluginDisableEvent) {
                    if (event.plugin === plugin) {
                        unregister(name)
                        aliases.forEach { unregister(it) }
                    }
                }
            },
            plugin
        )
    }

    protected abstract fun register(dispatcher: KommandDispatcherImpl, aliases: List<String>)
    protected abstract fun unregister(name: String)
}