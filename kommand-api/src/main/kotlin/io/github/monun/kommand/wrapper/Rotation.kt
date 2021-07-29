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

import org.bukkit.util.Vector
import java.lang.Math.toRadians
import kotlin.math.cos
import kotlin.math.sin

class Rotation(
    val yaw: Float,
    val pitch: Float
)

fun Rotation.toDirection(): Vector {
    val vector = Vector()

    val rotX: Double = yaw.toDouble()
    val rotY: Double = pitch.toDouble()

    vector.y = -sin(toRadians(rotY))

    val xz = cos(toRadians(rotY))

    vector.x = -xz * sin(toRadians(rotX))
    vector.z = xz * cos(toRadians(rotX))

    return vector
}