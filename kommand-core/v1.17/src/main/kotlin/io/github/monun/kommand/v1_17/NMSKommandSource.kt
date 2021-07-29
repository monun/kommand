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

package io.github.monun.kommand.v1_17

import io.github.monun.kommand.KommandSource
import io.github.monun.kommand.v1_17.wrapper.NMSEntityAnchor
import io.github.monun.kommand.wrapper.EntityAnchor
import io.github.monun.kommand.wrapper.Position3D
import io.github.monun.kommand.wrapper.Rotation
import io.papermc.paper.brigadier.PaperBrigadier
import net.kyori.adventure.text.Component
import net.minecraft.commands.CommandSourceStack
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.command.CommandSender
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

class NMSKommandSource(
    val nms: CommandSourceStack
) : KommandSource {
    override val displayName: Component
        get() = PaperBrigadier.componentFromMessage(nms.displayName)

    override val sender: CommandSender
        get() = nms.bukkitSender

    override val entity: Entity
        get() = nms.entityOrException.bukkitEntity

    override val entityOrNull: Entity?
        get() = nms.entity?.bukkitEntity

    override val player: Player
        get() = nms.playerOrException.bukkitEntity

    override val playerOrNull: Player?
        get() = nms.entity?.bukkitEntity?.takeIf { it is Player } as Player?

    override val position: Position3D
        get() = nms.position.run { Position3D(x, y, z) }

    override val rotation: Rotation
        get() = nms.rotation.run { Rotation(x, y) }

    override val anchor: EntityAnchor
        get() = NMSEntityAnchor(nms.anchor)

    override val world: World
        get() = nms.level.world

    override val location: Location
        get() = position.toLocation(nms.level.world, rotation)

    override fun hasPermission(level: Int): Boolean {
        return nms.hasPermission(level)
    }

    override fun hasPermission(level: Int, bukkitPermission: String): Boolean {
        return nms.hasPermission(level, bukkitPermission)
    }
}