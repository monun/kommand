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

import io.github.monun.kommand.KommandSource
import org.bukkit.entity.Entity
import org.bukkit.util.Vector

interface EntityAnchor {
    companion object {
        val FEET by lazy { WrapperSupport.entityAnchorFeet() }
        val EYES by lazy { WrapperSupport.entityAnchorEyes() }
    }

    val name: String

    fun applyTo(entity: Entity): Vector

    fun applyTo(source: KommandSource): Vector
}