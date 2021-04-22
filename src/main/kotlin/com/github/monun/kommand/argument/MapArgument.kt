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

class MapArgument<T>(
    private val parser: (String) -> T?,
    private val names: () -> Collection<String> = ::emptyList
) : KommandArgument<T> {
    override fun parse(context: KommandContext, param: String): T? {
        return parser(param)
    }

    override fun suggest(context: KommandContext, target: String): Collection<String> {
        return names().suggest(target)
    }
}