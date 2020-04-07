package com.github.noonmaru.kommand.argument

import com.github.noonmaru.kommand.KommandContext
import com.github.noonmaru.kommand.SuggestionBuilder
import com.github.noonmaru.kommand.argument.KommandArgument.Companion.TOKEN
import com.google.common.collect.ImmutableSet
import org.bukkit.Bukkit
import org.bukkit.entity.Player

interface KommandArgument<T> {
    companion object {
        const val TOKEN = "<argument>"
    }


    val parseFailMessage: String
        get() = "$TOKEN <-- 알 수 없는 인수입니다."

    fun parse(context: KommandContext, param: String): T?

    fun listSuggestion(context: KommandContext, builder: SuggestionBuilder) {}
}

class StringArgument internal constructor(
    private val set: Set<String> = emptySet()
) : KommandArgument<String> {
    override fun parse(context: KommandContext, param: String): String? {
        return param.takeIf { set.isEmpty() || param in set }
    }

    override fun listSuggestion(context: KommandContext, builder: SuggestionBuilder) {
        if (set.isEmpty()) return

        builder.addMatches(set)
    }

    companion object {
        internal val emptyStringArgument = StringArgument(ImmutableSet.of())
    }
}

fun string(): StringArgument {
    return StringArgument.emptyStringArgument
}

fun string(vararg names: String): StringArgument {
    return StringArgument(ImmutableSet.copyOf(names))
}

fun string(names: Collection<String>): StringArgument {
    return StringArgument(ImmutableSet.copyOf(names))
}

class IntegerArgument internal constructor() : KommandArgument<Int> {
    override val parseFailMessage: String
        get() = "$TOKEN <-- $minimum ~ $maximum 사이의 정수(${radix}진수)가 아닙니다."
    var maximum = Int.MAX_VALUE
        set(value) {
            require(value >= minimum) { "maximum $value was not more than minimum $minimum." }
            field = value
        }

    var minimum = Int.MIN_VALUE
        set(value) {
            require(value <= maximum) { "minimum $value was not less than maximum $maximum." }
            field = value
        }
    var radix = 10
        set(value) {
            require(value in 2..36) { "radix $value was not in valid range 2..36" }
            field = value
        }

    override fun parse(context: KommandContext, param: String): Int? {
        return param.toIntOrNull(radix)?.coerceIn(minimum, maximum)
    }
}

fun integer(): IntegerArgument {
    return IntegerArgument()
}

class DoubleArgument internal constructor() : KommandArgument<Double> {
    override val parseFailMessage: String
        get() = "$TOKEN <-- $minimum ~ $maximum 사이의 실수가 아닙니다."

    var maximum = Double.MAX_VALUE
        set(value) {
            require(value >= minimum) { "maximum $value was not more than minimum $minimum." }
            field = value
        }
    var minimum = -Double.MIN_VALUE
        set(value) {
            require(value <= maximum) { "minimum $value was not less than maximum $maximum." }
            field = value
        }
    var allowInfinite = false
    var allowNaN = false

    override fun parse(context: KommandContext, param: String): Double? {
        return param.toDoubleOrNull()?.coerceIn(minimum, maximum)?.takeIf {
            when {
                it.isInfinite() -> allowInfinite
                it.isNaN() -> allowNaN
                else -> true
            }
        }
    }
}

fun double(): DoubleArgument {
    return DoubleArgument()
}

class PlayerArgument internal constructor() : KommandArgument<Player> {
    override val parseFailMessage: String
        get() = "$TOKEN 플레이어를 찾지 못했습니다."

    override fun parse(context: KommandContext, param: String): Player? {
        return Bukkit.getPlayerExact(param)
    }

    override fun listSuggestion(context: KommandContext, builder: SuggestionBuilder) {
        builder.addMatches(Bukkit.getOnlinePlayers()) { player ->
            player.name
        }
    }

    companion object {
        internal val instance by lazy {
            PlayerArgument()
        }
    }
}

fun player(): PlayerArgument {
    return PlayerArgument.instance
}