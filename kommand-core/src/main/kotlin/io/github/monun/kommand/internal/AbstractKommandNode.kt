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

package io.github.monun.kommand.internal

import io.github.monun.kommand.KommandArgument
import io.github.monun.kommand.KommandArgumentSupport
import io.github.monun.kommand.KommandContext
import io.github.monun.kommand.KommandSource
import io.github.monun.kommand.node.*

abstract class AbstractKommandNode : KommandNode, KommandArgumentSupport by KommandArgumentSupport.INSTANCE {
    lateinit var kommand: KommandDispatcherImpl
    lateinit var name: String

    var parent: AbstractKommandNode? = null

    var requires: (KommandSource.() -> Boolean)? = null
        private set

    var executes: (KommandSource.(context: KommandContext) -> Unit)? = null
        private set

    protected fun initialize0(kommand: KommandDispatcherImpl, name: String) {
        this.kommand = kommand
        this.name = name
    }

    val nodes = arrayListOf<AbstractKommandNode>()

    override fun requires(requires: KommandSource.() -> Boolean) {
        kommand.checkState()
        require(this.requires == null) { "Cannot redefine requires" }
        this.requires = requires
    }

    override fun executes(executes: KommandSource.(context: KommandContext) -> Unit) {
        kommand.checkState()
        require(this.executes == null) { "Cannot redefine executes" }
        this.executes = executes
    }

    override fun then(name: String, init: LiteralNode.() -> Unit) {
        nodes += LiteralNodeImpl().apply {
            parent = this@AbstractKommandNode
            initialize(this@AbstractKommandNode.kommand, name)
            init()

        }
    }

    override fun then(argument: Pair<String, KommandArgument<*>>, init: ArgumentNode.() -> Unit) {
        kommand.checkState()
        nodes += ArgumentNodeImpl().apply {
            parent = this@AbstractKommandNode
            initialize(this@AbstractKommandNode.kommand, argument.first, argument.second)
            init()
        }
    }
}