package io.github.monun.kommand

import io.github.monun.kommand.loader.LibraryLoader
import io.github.monun.kommand.node.LiteralNode

@KommandDSL
interface Kommand {
    companion object: Kommand by LibraryLoader.loadNMS(Kommand::class.java)

    fun register(name: String, vararg aliases: String, init: LiteralNode.() -> Unit)
}

@DslMarker
annotation class KommandDSL