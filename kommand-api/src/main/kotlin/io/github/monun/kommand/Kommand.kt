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

package io.github.monun.kommand

import io.github.monun.kommand.loader.LibraryLoader
import io.github.monun.kommand.node.LiteralNode
import org.bukkit.plugin.Plugin

@KommandDSL
interface Kommand {
    companion object : Kommand by LibraryLoader.loadNMS(Kommand::class.java)

    fun register(plugin: Plugin, name: String, vararg aliases: String, init: LiteralNode.() -> Unit)
}

@DslMarker
annotation class KommandDSL

@KommandDSL
class PluginKommand internal constructor(private val plugin: Plugin) {
    fun register(name: String, vararg aliases: String, init: LiteralNode.() -> Unit) {
        Kommand.register(plugin, name, *aliases) { init() }
    }
}

fun Plugin.kommand(init: PluginKommand.() -> Unit) {
    PluginKommand(this).init()
}
