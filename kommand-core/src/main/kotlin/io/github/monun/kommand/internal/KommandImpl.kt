package io.github.monun.kommand.internal

import io.github.monun.kommand.*

class KommandImpl : Kommand {
    private var isMutable = true

    lateinit var root: RootNodeImpl
        private set

    internal fun initialize(name: String, aliases: List<String>) {
        root = RootNodeImpl().apply {
            initialize(this@KommandImpl, name, aliases)
        }
    }

    fun checkState() {
        require(isMutable) { "DSL Error!" }
    }

    fun register() {
        isMutable = false

        TODO("Brigadier에 등록")
    }
}


