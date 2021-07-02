package io.github.monun.kommand.internal

interface Brigadier {
    companion object {
        val nms = LibraryLoader.load(Brigadier::class.java)
    }

    fun register(dispatcher: KommandDispatcherImpl)
}