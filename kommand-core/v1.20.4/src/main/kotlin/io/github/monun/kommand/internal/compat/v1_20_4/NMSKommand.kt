/*
 * Copyright (C) 2023 Monun
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.monun.kommand.internal.compat.v1_20_4

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.tree.CommandNode
import com.mojang.brigadier.tree.LiteralCommandNode
import com.mojang.brigadier.tree.RootCommandNode
import io.github.monun.kommand.internal.*
import io.github.monun.kommand.internal.compat.v1_20_4.NMSKommandContext.Companion.wrapContext
import io.github.monun.kommand.internal.compat.v1_20_4.NMSKommandSource.Companion.wrapSource
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.server.MinecraftServer
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_20_R3.CraftServer
import org.bukkit.craftbukkit.v1_20_R3.command.VanillaCommandWrapper
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer
import org.bukkit.entity.Player


class NMSKommand : AbstractKommand() {
    private val server: MinecraftServer = (Bukkit.getServer() as CraftServer).server
    private val vanillaCommands: Commands = server.vanillaCommandDispatcher
    private val dispatcher: CommandDispatcher<CommandSourceStack> = vanillaCommands.dispatcher
    private val root: RootCommandNode<CommandSourceStack> = dispatcher.root

    private val children: MutableMap<String, CommandNode<CommandSourceStack>> = root["children"]
    private val literals: MutableMap<String, LiteralCommandNode<CommandSourceStack>> = root["literals"]

    private val commandMap = Bukkit.getCommandMap()

    override fun test(name: String, aliases: Array<out String>): Boolean {
        return literals[name] == null && aliases.all { literals[it] == null }
    }

    override fun register(dispatcher: KommandDispatcherImpl, aliases: List<String>) {
        val node = this.dispatcher.register(dispatcher.root.convert() as LiteralArgumentBuilder<CommandSourceStack>)
        aliases.forEach { this.dispatcher.register(literal(it).redirect(node)) }

        val root = dispatcher.root
        commandMap.register(
            root.fallbackPrefix,
            VanillaCommandWrapper(vanillaCommands, node).apply {
                description = root.description
                usage = root.usage
                permission = null

                setAliases(aliases.toList())
            }
        )
    }

    override fun unregister(name: String) {
        children.remove(name)
        literals.remove(name)
    }

    override fun sendCommandsPacket(player: Player) {
        vanillaCommands.sendCommands((player as CraftPlayer).handle)
    }
}

@Suppress("UNCHECKED_CAST")
private operator fun <T> CommandNode<*>.get(name: String): T {
    val field = CommandNode::class.java.getDeclaredField(name).apply { isAccessible = true }
    return field.get(this) as T
}

private fun AbstractKommandNode.convert(): ArgumentBuilder<CommandSourceStack, *> {
    return when (this) {
        is RootNodeImpl, is LiteralNodeImpl -> literal(name)
        is ArgumentNodeImpl -> {
            val kommandArgument = argument as NMSKommandArgument<*>
            val type = kommandArgument.type
            argument(name, type).apply {
                suggests { context, suggestionsBuilder ->
                    kommandArgument.listSuggestions(wrapContext(context), suggestionsBuilder)
                }
            }
        }

        else -> error("Unknown node type ${javaClass.name}")
    }.apply {
        requires { source ->
            kotlin.runCatching {
                requires(wrapSource(source))
            }.onFailure {
                if (it !is CommandSyntaxException) it.printStackTrace()
            }.getOrThrow()
        }

        executes?.let { executes ->
            executes { context ->
                wrapSource(context.source).runCatching {
                    executes(this@convert.wrapContext(context))
                }.onFailure {
                    if (it !is CommandSyntaxException) it.printStackTrace()
                }.getOrThrow()
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