package com.github.noonmaru.kommand.argument

import com.github.noonmaru.kommand.KommandContext

class DoubleArgument internal constructor() : KommandArgument<Double> {
    override val parseFailMessage: String
        get() = "${KommandArgument.TOKEN} <-- $minimum ~ $maximum 사이의 실수가 아닙니다."

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