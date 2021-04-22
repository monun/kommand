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

object BooleanArgument : KommandArgument<Boolean> {
    override val parseFailMessage: String
        get() = "${KommandArgument.TOKEN} <-- true 혹은 false를 입력하세요."

    override fun parse(context: KommandContext, param: String): Boolean {
        return param.toBoolean()
    }

    override fun suggest(context: KommandContext, target: String): Collection<String> {
        return listOf("true", "false")
    }
}