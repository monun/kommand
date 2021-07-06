package io.github.monun.kommand

import io.github.monun.kommand.loader.LibraryLoader

// 인수
interface KommandArgument<T> {
    companion object : KommandArgumentSupport by LibraryLoader.load(KommandArgumentSupport::class.java)

    fun suggests(provider: KommandSuggestion.(context: KommandContext) -> Unit)
}

interface KommandArgumentSupport {
    fun bool(): KommandArgument<Boolean>

    fun int(minimum: Int = Int.MIN_VALUE, maximum: Int = Int.MAX_VALUE): KommandArgument<Int>
}