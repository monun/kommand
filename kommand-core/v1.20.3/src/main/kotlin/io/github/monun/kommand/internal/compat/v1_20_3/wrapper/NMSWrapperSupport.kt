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

import io.github.monun.kommand.wrapper.EntityAnchor
import io.github.monun.kommand.wrapper.WrapperSupport
import net.minecraft.commands.arguments.EntityAnchorArgument

class NMSWrapperSupport : WrapperSupport {
    override fun entityAnchorFeet(): EntityAnchor {
        return NMSEntityAnchor(EntityAnchorArgument.Anchor.FEET)
    }

    override fun entityAnchorEyes(): EntityAnchor {
        return NMSEntityAnchor(EntityAnchorArgument.Anchor.EYES)
    }
}