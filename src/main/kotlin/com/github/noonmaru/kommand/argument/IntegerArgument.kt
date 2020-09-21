package com.github.noonmaru.kommand.argument

import com.github.noonmaru.kommand.KommandContext

class IntegerArgument internal constructor() : KommandArgument<Int> {
    override val parseFailMessage: String
        get() = "${KommandArgument.TOKEN} <-- $minimum ~ $maximum 사이의 정수(${radix}진수)가 아닙니다."
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