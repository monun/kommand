package io.github.monun.kommand.v1_17_R1.internal

import com.mojang.brigadier.context.CommandContext
import io.github.monun.kommand.KommandContext
import io.github.monun.kommand.KommandSource
import io.github.monun.kommand.internal.ArgumentNodeImpl
import io.github.monun.kommand.internal.AbstractKommandNode
import io.github.monun.kommand.v1_17_R1.argument.NMSKommandArgument
import net.minecraft.commands.CommandSourceStack

class NMSKommandContext(
    private val node: AbstractKommandNode,
    private val nms: CommandContext<CommandSourceStack>
): KommandContext {
    override val source: KommandSource by lazy { NMSKommandSource(nms.source) }
    override val input: String
        get() = nms.input

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(name: String): T {
        val argumentNode = node.findArgumentNode(name) ?: error("Not found argument node $name")
        val argument = argumentNode.argument as NMSKommandArgument<*, *>

        return argument.from(nms, name) as T
    }
}

private fun AbstractKommandNode.findArgumentNode(name: String): ArgumentNodeImpl? {
    var node: AbstractKommandNode? = this

    while (node != null) {
        if (node is ArgumentNodeImpl && node.name == name) return node
        node = node.parent
    }

    return null
}