package io.github.monun.kommand

import io.github.monun.kommand.argument.IntArgument
import io.github.monun.kommand.loader.LibraryLoader

// 인수
interface KommandArgument<T> {
    companion object: KommandArgumentSupport by LibraryLoader.load(KommandArgumentSupport::class.java)
}

interface KommandArgumentSupport {
    fun int(): IntArgument
}