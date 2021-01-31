/*
 * Copyright (c) 2021 Noonmaru
 *
 *  Licensed under the General Public License, Version 3.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://opensource.org/licenses/gpl-3.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.monun.kommand.argument

import com.github.monun.kommand.KommandContext

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