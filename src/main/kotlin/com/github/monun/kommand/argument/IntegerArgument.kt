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

class IntegerArgument : KommandArgument<Int> {
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