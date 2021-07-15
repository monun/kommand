package io.github.monun.kommand

import io.github.monun.kommand.loader.LibraryLoader
import io.github.monun.kommand.node.LiteralNode
import org.bukkit.plugin.Plugin

@KommandDSL
interface Kommand {
    companion object: Kommand by LibraryLoader.loadNMS(Kommand::class.java)

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