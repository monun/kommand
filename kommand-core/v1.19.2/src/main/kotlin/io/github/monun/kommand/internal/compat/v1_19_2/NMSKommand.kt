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

package io.github.monun.kommand.internal.compat.v1_19_2

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
import io.github.monun.kommand.internal.compat.v1_19_2.NMSKommandContext.Companion.wrapContext
import io.github.monun.kommand.internal.compat.v1_19_2.NMSKommandSource.Companion.wrapSource
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.server.MinecraftServer
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_19_R1.CraftServer
import org.bukkit.craftbukkit.v1_19_R1.command.VanillaCommandWrapper
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer
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

        /**
         * 버킷의 바닐라 명령 실행 순서는 다음과 같음
         * 채팅 패킷 수신 -> CommandMap -> VanillaCommandWrapper -> Brigadier
         *
         * 플러그인 로딩 후 CommandMap에 바닐라 명령을 VanillaCommandWrapper로 등록
         * VanillaCommandWrapper는 바닐라 커맨드는 생성자에서 'minecraft.command.<name>'의 권한을 적용
         * 따라서 일반적으로 OP권한이 없다면 Brigadier에 등록한 명령을 실행 할 수 없음 (requires도 호출되지 않음)
         *
         * bukkit에서 CommandMap에 VanillaCommandWrapper를 등록하기 전에 미리 등록
         *
         * 어차피 requires에서 테스트하는데 왜 추가 권한을 요구하는지 이해가 되질 않음
         */
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
            /**
             * 권한 테스트 순서
             * requirement -> permission
             */
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