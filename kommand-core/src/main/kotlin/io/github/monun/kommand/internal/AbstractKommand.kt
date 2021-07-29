/*
 * Kommand
 * Copyright (C) 2021 Monun
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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