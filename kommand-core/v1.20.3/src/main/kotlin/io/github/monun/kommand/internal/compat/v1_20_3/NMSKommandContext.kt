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

package io.github.monun.kommand.internal.compat.v1_20_3

import com.mojang.brigadier.context.CommandContext
import io.github.monun.kommand.KommandContext
import io.github.monun.kommand.KommandSource
import io.github.monun.kommand.internal.AbstractKommandNode
import io.github.monun.kommand.internal.ArgumentNodeImpl
import io.github.monun.kommand.ref.getValue
import io.github.monun.kommand.ref.weak
import io.github.monun.kommand.internal.compat.v1_20_3.NMSKommandSource.Companion.wrapSource
import net.minecraft.commands.CommandSourceStack
import java.util.*

class NMSKommandContext private constructor(
    private val node: AbstractKommandNode,
    handle: CommandContext<CommandSourceStack>
) : KommandContext {
    companion object {
        private val refs = WeakHashMap<CommandContext<CommandSourceStack>, NMSKommandContext>()

        fun AbstractKommandNode.wrapContext(context: CommandContext<CommandSourceStack>): NMSKommandContext =
            refs.computeIfAbsent(context) {
                NMSKommandContext(this, context)
            }
    }

    internal val handle by weak(handle)

    override val source: KommandSource by lazy { wrapSource(handle.source) }

    override val input: String
        get() = handle.input

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(name: String): T {
        val argumentNode = node.findArgumentNode(name) ?: error("Not found argument node $name")
        val argument = argumentNode.argument as NMSKommandArgument<*>

        return argument.from(this, name) as T
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