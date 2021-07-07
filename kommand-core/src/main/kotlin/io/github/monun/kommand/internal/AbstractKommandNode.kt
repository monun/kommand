package io.github.monun.kommand.internal

import io.github.monun.kommand.KommandArgument
import io.github.monun.kommand.KommandContext
import io.github.monun.kommand.KommandSource
import io.github.monun.kommand.node.ArgumentNode
import io.github.monun.kommand.node.KommandNode
import io.github.monun.kommand.node.LiteralNode

abstract class AbstractKommandNode : KommandNode {
    lateinit var kommand: KommandDispatcherImpl
    lateinit var name: String

    var parent: AbstractKommandNode? = null

    var requires: ((KommandSource) -> Boolean)? = null
        private set

    var executes: ((KommandContext) -> Unit)? = null
        private set

    protected fun initialize0(kommand: KommandDispatcherImpl, name: String) {
        this.kommand = kommand
        this.name = name
    }

    val nodes = arrayListOf<AbstractKommandNode>()

    override fun requires(requires: (KommandSource) -> Boolean) {
        kommand.checkState()
        require(this.requires == null) { "Cannot redefine requires" }
        this.requires = requires
    }

    override fun executes(executes: (context: KommandContext) -> Unit) {
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