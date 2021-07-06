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

    fun float(minimum: Float, maximum: Float): KommandArgument<Float>

    fun double(minimum: Double, maximum: Double): KommandArgument<Double>

    fun long(minimum: Long, maximum: Long): KommandArgument<Long>

    fun string(type: StringType = StringType.SINGLE_WORD): KommandArgument<String>
}

enum class StringType {
    SINGLE_WORD,
    QOUTABLE_PHRASE,
    GREEDY_PHRASE
}