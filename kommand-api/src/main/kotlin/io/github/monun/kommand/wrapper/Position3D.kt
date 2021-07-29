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

package io.github.monun.kommand.wrapper

import org.bukkit.Location
import org.bukkit.World
import org.bukkit.util.Vector

data class Position3D(val x: Double, val y: Double, val z: Double) {
    val asVector: Vector
        get() = Vector(x, y, z)

    fun toLocation(world: World?, yaw: Float, pitch: Float): Location {
        return Location(world, x, y, z, yaw, pitch)
    }

    fun toLocation(world: World?, rotation: Rotation): Location {
        return Location(world, x, y, z, rotation.yaw, rotation.pitch)
    }
}