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

package io.github.monun.kommand.v1_17_1

import com.mojang.brigadier.context.CommandContext
import io.github.monun.kommand.KommandContext
import io.github.monun.kommand.internal.AbstractKommandNode
import io.github.monun.kommand.internal.ArgumentNodeImpl
import net.minecraft.commands.CommandSourceStack

class NMSKommandContext(
    private val node: AbstractKommandNode,
    private val nms: CommandContext<CommandSourceStack>
) : KommandContext {
    override val input: String
        get() = nms.input

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(name: String): T {
        val argumentNode = node.findArgumentNode(name) ?: error("Not found argument node $name")
        val argument = argumentNode.argument as NMSKommandArgument<*>

        return argument.from(nms, name) as T
    }
}

private fun AbstractKommandNode.findArgumentNode(name: String): ArgumentNodeImpl? {
    var node: AbstractKommandNode? = this

    while (node != null) {
        if (node is ArgumentNodeImpl && node.name == name) return node
        node = node.parent
    }

    return null
}