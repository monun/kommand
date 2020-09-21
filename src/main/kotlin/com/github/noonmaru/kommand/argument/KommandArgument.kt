/*
 * Copyright (c) 2020 Noonmaru
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

package com.github.noonmaru.kommand.argument

import com.github.noonmaru.kommand.KommandContext
import com.google.common.collect.ImmutableList

interface KommandArgument<T> {
    companion object {
        const val TOKEN = "<argument>"
    }

    val parseFailMessage: String
        get() = "$TOKEN <-- 알 수 없는 인수입니다."

    fun parse(context: KommandContext, param: String): T?

    fun listSuggestion(context: KommandContext, target: String): Collection<String> = emptyList()
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

fun integer(): IntegerArgument {
    return IntegerArgument()
}

fun double(): DoubleArgument {
    return DoubleArgument()
}

fun player(): PlayerArgument {
    return PlayerArgument.instance
}

fun <T> map(map: Map<String, T>): KommandArgument<T> {
    return MapArgument(map)
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