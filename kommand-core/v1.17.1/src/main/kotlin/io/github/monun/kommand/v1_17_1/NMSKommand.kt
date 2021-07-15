package io.github.monun.kommand.v1_17_1

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.tree.CommandNode
import com.mojang.brigadier.tree.LiteralCommandNode
import com.mojang.brigadier.tree.RootCommandNode
import io.github.monun.kommand.internal.*
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.server.MinecraftServer
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_17_R1.CraftServer

class NMSKommand : AbstractKommand() {
    private val server: MinecraftServer = (Bukkit.getServer() as CraftServer).server
    private val vanillaCommands: Commands = server.vanillaCommandDispatcher
    private val dispatcher: CommandDispatcher<CommandSourceStack> = vanillaCommands.dispatcher
    private val root: RootCommandNode<CommandSourceStack> = dispatcher.root

    private val children: MutableMap<String, CommandNode<CommandSourceStack>> = root["children"]
    private val literals: MutableMap<String, LiteralCommandNode<CommandSourceStack>> = root["literals"]

    override fun register(dispatcher: KommandDispatcherImpl, aliases: List<String>) {
        val node = this.dispatcher.register(dispatcher.root.convert() as LiteralArgumentBuilder<CommandSourceStack>)
        aliases.forEach { this.dispatcher.register(literal(it).redirect(node)) }
    }

    override fun unregister(name: String) {
        children.remove(name)
        literals.remove(name)
    }
}

@Suppress("UNCHECKED_CAST")
private operator fun <T> CommandNode<*>.get(name: String): T {
    val field = CommandNode::class.java.getDeclaredField(name).apply { isAccessible = true }
    return field.get(this) as T
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
                NMSKommandSource(source).requires()
            }
        }
        executes?.let { executes ->
            executes { context ->
                NMSKommandSource(context.source).executes(NMSKommandContext(this@convert, context))
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