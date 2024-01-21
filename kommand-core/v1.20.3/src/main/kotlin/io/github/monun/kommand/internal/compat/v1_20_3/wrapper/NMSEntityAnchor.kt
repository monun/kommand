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

package io.github.monun.kommand.internal.compat.v1_20_3.wrapper

import io.github.monun.kommand.KommandSource
import io.github.monun.kommand.wrapper.EntityAnchor
import net.minecraft.commands.arguments.EntityAnchorArgument
import net.minecraft.world.entity.Entity
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity
import org.bukkit.util.Vector

class NMSEntityAnchor(
    private val handle: EntityAnchorArgument.Anchor
) : EntityAnchor {
    override val name: String
        get() = handle.name

    override fun applyTo(entity: org.bukkit.entity.Entity): Vector {
        val nmsEntity: Entity = (entity as CraftEntity).handle
        return handle.apply(nmsEntity).run { Vector(x, y, z) }
    }

    override fun applyTo(source: KommandSource): Vector {
        return applyTo(source.entity)
    }
}