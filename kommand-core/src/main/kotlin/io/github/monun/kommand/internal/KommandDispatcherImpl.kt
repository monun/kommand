package io.github.monun.kommand.internal

import io.github.monun.kommand.KommandDispatcher

class KommandDispatcherImpl : KommandDispatcher {
    internal var isMutable = true

    lateinit var root: LiteralNodeImpl
        private set

    internal fun initialize(name: String) {
        root = LiteralNodeImpl().apply {
            initialize(this@KommandDispatcherImpl, name)
        }
    }

    fun checkState() {
        require(isMutable) { "DSL Error!" }
    }
}


