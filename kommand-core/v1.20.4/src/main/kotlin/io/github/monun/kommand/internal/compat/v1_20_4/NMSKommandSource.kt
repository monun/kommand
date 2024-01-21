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

import io.github.monun.kommand.KommandSource
import io.github.monun.kommand.ref.getValue
import io.github.monun.kommand.ref.weak
import io.github.monun.kommand.internal.compat.v1_20_4.wrapper.NMSEntityAnchor
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
import java.util.*

class NMSKommandSource private constructor(
    handle: CommandSourceStack
) : KommandSource {
    companion object {
        private val refs = WeakHashMap<CommandSourceStack, NMSKommandSource>()

        fun wrapSource(source: CommandSourceStack): NMSKommandSource =
            refs.computeIfAbsent(source) {
                NMSKommandSource(source)
            }
    }

    private val handle by weak(handle)

    override val displayName: Component
        get() = PaperBrigadier.componentFromMessage(handle.displayName)

    override val sender: CommandSender
        get() = handle.bukkitSender

    override val entity: Entity
        get() = handle.entityOrException.bukkitEntity

    override val entityOrNull: Entity?
        get() = handle.entity?.bukkitEntity

    override val player: Player
        get() = handle.playerOrException.bukkitEntity

    override val playerOrNull: Player?
        get() = handle.entity?.bukkitEntity?.takeIf { it is Player } as Player?

    override val position: Position3D
        get() = handle.position.run { Position3D(x, y, z) }

    override val rotation: Rotation
        get() = handle.rotation.run { Rotation(x, y) }

    override val anchor: EntityAnchor
        get() = NMSEntityAnchor(handle.anchor)

    override val world: World
        get() = handle.level.world

    override val location: Location
        get() = position.toLocation(handle.level.world, rotation)

    override fun hasPermission(level: Int): Boolean {
        return handle.hasPermission(level)
    }

    override fun hasPermission(level: Int, bukkitPermission: String): Boolean {
        return handle.hasPermission(level, bukkitPermission)
    }
}