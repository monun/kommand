package io.github.monun.kommand.v1_17

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import io.github.monun.kommand.internal.*
import io.github.monun.kommand.node.KommandExecutor
import io.github.monun.kommand.node.KommandRequirement
import net.minecraft.commands.CommandSourceStack
import net.minecraft.server.MinecraftServer
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_17_R1.CraftServer

class NMSKommand : AbstractKommand() {
    override fun register(dispatcher: KommandDispatcherImpl, aliases: List<String>) {
        val server: MinecraftServer = (Bukkit.getServer() as CraftServer).server
        val nms = server.commands.dispatcher
        val node = nms.register(dispatcher.root.convert() as LiteralArgumentBuilder<CommandSourceStack>)
        aliases.forEach { nms.register(literal(it).redirect(node)) }
    }
}

private fun AbstractKommandNode.convert(): ArgumentBuilder<CommandSourceStack, *> {
    return when (this) {
        is LiteralNodeImpl -> literal(name)
        is ArgumentNodeImpl -> {
            val kommandArgument = argument as NMSKommandArgument<*>
            val type = kommandArgument.type
            argument(name, type).apply {
                suggests { context, suggestionsBuilder ->
                    kommandArgument.listSuggestions(this@convert, context, suggestionsBuilder)
                }
            }
        }
        else -> error("Unknown node type ${javaClass.name}")
    }.apply {
        requires?.let { requires ->
            requires { source ->
                KommandRequirement.requires(NMSKommandSource(source))
            }
        }
        executes?.let { executes ->
            executes { context ->
                KommandExecutor.executes(NMSKommandContext(this@convert, context))
                1
            }
        }

        nodes.forEach { node ->
            then(node.convert())
        }
    }
}

private fun literal(name: String): LiteralArgumentBuilder<CommandSourceStack> {
    return LiteralArgumentBuilder.literal(name)
}

private fun argument(name: String, argumentType: ArgumentType<*>): RequiredArgumentBuilder<CommandSourceStack, *> {
    return RequiredArgumentBuilder.argument(name, argumentType)
}