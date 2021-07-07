package io.github.monun.kommand.v1_17_R1

import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import io.github.monun.kommand.Kommand
import io.github.monun.kommand.internal.ArgumentNodeImpl
import io.github.monun.kommand.internal.KommandDispatcherImpl
import io.github.monun.kommand.internal.AbstractKommandNode
import io.github.monun.kommand.internal.LiteralNodeImpl
import io.github.monun.kommand.v1_17_R1.internal.ArgumentSupport
import io.github.monun.kommand.v1_17_R1.internal.NMSKommandContext
import io.github.monun.kommand.v1_17_R1.internal.NMSKommandSource
import net.minecraft.commands.CommandSourceStack
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_17_R1.CraftServer

class NMSKommand : Kommand {
    override fun register(dispatcher: KommandDispatcherImpl, aliases: List<String>) {
        val nms = (Bukkit.getServer() as CraftServer).server.commands.dispatcher
        val node = nms.register(dispatcher.root.convert() as LiteralArgumentBuilder<CommandSourceStack>)
        aliases.forEach { nms.register(ArgumentSupport.literal(it).redirect(node)) }
    }
}

private fun AbstractKommandNode.convert(): ArgumentBuilder<CommandSourceStack, *> {
    return when (this) {
        is LiteralNodeImpl -> ArgumentSupport.literal(name)
        is ArgumentNodeImpl -> {
            val kommandArgument = argument as NMSKommandArgument<*>
            val type = kommandArgument.type
            ArgumentSupport.argument(name, type).apply {
                suggests { context, suggestionsBuilder ->
                    kommandArgument.listSuggestions(this@convert, context, suggestionsBuilder)
                }
            }
        }
        else -> error("Unknown node type ${javaClass.name}")
    }.apply {
        requires?.let { requires ->
            requires { source ->
                requires(NMSKommandSource(source))
            }
        }
        executes?.let { executes ->
            executes { context ->
                executes(NMSKommandContext(this@convert, context))
                1
            }
        }

        nodes.forEach { node ->
            then(node.convert())
        }
    }
}