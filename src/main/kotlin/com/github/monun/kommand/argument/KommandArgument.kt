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
import com.google.common.collect.ImmutableList
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import java.util.function.Predicate

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

fun bool(): BooleanArgument {
    return BooleanArgument
}

fun integer(): IntegerArgument {
    return IntegerArgument()
}

fun double(): DoubleArgument {
    return DoubleArgument()
}

fun player(): PlayerArgument {
    return PlayerArgument
}

fun target(filter: Predicate<Entity>? = null): TargetArgument {
    return if (filter == null) TargetArgument.instance else TargetArgument(filter)
}

fun playerTarget(filter: Predicate<Player>? = null): TargetArgument {
    return if (filter == null) TargetArgument.player else TargetArgument { it is Player && filter.test(it) }
}

fun <T> map(map: Map<String, T>): MapArgument<T> {
    return MapArgument(map)
}

fun <T : Enum<*>> enum(values: List<T>): EnumArgument<T> {
    return EnumArgument(values)
}

fun <T : Enum<*>> enum(values: Array<T>): EnumArgument<T> = enum(values.asList())

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