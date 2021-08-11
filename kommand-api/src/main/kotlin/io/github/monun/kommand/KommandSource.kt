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

import io.github.monun.kommand.wrapper.EntityAnchor
import io.github.monun.kommand.wrapper.Position3D
import io.github.monun.kommand.wrapper.Rotation
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.GameRule
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.command.CommandSender
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

// 명령 발신자 정보
@KommandDSL
interface KommandSource {
    val displayName: Component
    val sender: CommandSender
    val entity: Entity
    val entityOrNull: Entity?
    val player: Player
    val playerOrNull: Player?
    val position: Position3D
    val rotation: Rotation
    val anchor: EntityAnchor
    val world: World
    val location: Location

    val isPlayer
        get() = playerOrNull != null

    val isConsole
        get() = sender == Bukkit.getConsoleSender()

    fun hasPermission(level: Int): Boolean

    fun hasPermission(level: Int, bukkitPermission: String): Boolean

    fun feedback(message: ComponentLike) {
        val sender = sender

        if (sender !is Entity || world.getGameRuleValue(GameRule.SEND_COMMAND_FEEDBACK) == true) {
            sender.sendMessage(message)
        }
    }

    fun broadcast(message: ComponentLike, isAudience: CommandSender.() -> Boolean = { isOp }) {
        feedback(message)

        val sender = sender
        val broadcast =
            text().decorate(TextDecoration.ITALIC).color(NamedTextColor.GRAY).content("[").append(displayName)
                .append(text().content(": ")).append(
                    text().decoration(TextDecoration.ITALIC, false)
                        .append(message)
                ).append(text().content("]"))

        Bukkit.getOnlinePlayers().forEach { player ->
            if (player !== sender && player.isAudience() && player.world.getGameRuleValue(GameRule.SEND_COMMAND_FEEDBACK) == true) {
                player.sendMessage(broadcast)
            }
        }

        Bukkit.getConsoleSender().let { console ->
            if (console !== sender && console.isAudience()) {
                console.sendMessage(broadcast)
            }
        }
    }
}