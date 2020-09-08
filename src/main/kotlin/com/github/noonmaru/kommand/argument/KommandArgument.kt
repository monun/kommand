package com.github.noonmaru.kommand.argument

import com.github.noonmaru.kommand.KommandContext
import com.github.noonmaru.kommand.argument.KommandArgument.Companion.TOKEN
import com.google.common.collect.ImmutableList
import org.bukkit.Bukkit
import org.bukkit.entity.Player

interface KommandArgument<T> {
    companion object {
        const val TOKEN = "<argument>"
    }

    val parseFailMessage: String
        get() = "$TOKEN <-- 알 수 없는 인수입니다."

    fun parse(context: KommandContext, param: String): T?

    fun listSuggestion(context: KommandContext, target: String): Collection<String> = emptyList()
}

fun Collection<String>.suggestions(target: String): Collection<String> {
    if (isEmpty()) return emptyList()
    if (target.isEmpty()) return this

    return filter { it.startsWith(target, true) }
}

fun <T> Collection<T>.suggestions(target: String, transform: (T) -> String = { it.toString() }): Collection<String> {
    if (isEmpty()) return emptyList()
    if (target.isEmpty()) return map(transform)

    val list = ArrayList<String>()

    for (element in this) {
        transform(element).let { name ->
            if (name.startsWith(target, true))
                list += name
        }
    }

    return list
}

class StringArgument internal constructor(
    private val values: () -> Collection<String>
) : KommandArgument<String> {
    override fun parse(context: KommandContext, param: String): String? {
        val values = values()

        return param.takeIf { values.isEmpty() || param in values }
    }

    override fun listSuggestion(context: KommandContext, target: String): Collection<String> {
        return values().suggestions(target)
    }

    companion object {
        internal val emptyStringArgument = StringArgument { ImmutableList.of() }
    }
}

fun string(): StringArgument {
    return StringArgument.emptyStringArgument
}

fun string(vararg names: String): StringArgument {
    val list = ImmutableList.copyOf(names)
    return string { list }
}

fun string(names: Collection<String>): StringArgument {
    return string { names }
}

fun string(supplier: () -> Collection<String>): StringArgument {
    return StringArgument(supplier)
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

    override fun listSuggestion(context: KommandContext, target: String): Collection<String> {
        return Bukkit.getOnlinePlayers().suggestions(target) { it.name }
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

class MapArgument<T> internal constructor(
    private val map: Map<String, T>
) : KommandArgument<T> {
    override fun parse(context: KommandContext, param: String): T? {
        return map[param]
    }

    override fun listSuggestion(context: KommandContext, target: String): Collection<String> {
        return map.keys.suggestions(target)
    }
}

fun <T> map(map: Map<String, T>): KommandArgument<T> {
    return MapArgument(map)
}