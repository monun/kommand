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

import com.github.monun.kommand.KommandBuilder
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

    fun suggest(context: KommandContext, target: String): Collection<String> = emptyList()
}

fun KommandBuilder.string() = StringArgument.emptyStringArgument

fun KommandBuilder.string(vararg names: String) = string { ImmutableList.copyOf(names) }

fun KommandBuilder.string(names: Collection<String>) = string { names }

fun KommandBuilder.string(supplier: () -> Collection<String>) = StringArgument(supplier)

fun KommandBuilder.bool() = BooleanArgument

fun KommandBuilder.integer(
    min: Int = Int.MIN_VALUE,
    max: Int = Int.MAX_VALUE,
    radix: Int = 10
) = IntegerArgument().apply {
    this.minimum = min
    this.maximum = max
    this.radix = radix
}

fun KommandBuilder.long(
    min: Long = Long.MIN_VALUE,
    max: Long = Long.MAX_VALUE,
    radix: Int = 10
) = LongArgument().apply {
    this.minimum = min
    this.maximum = max
    this.radix = radix
}

fun KommandBuilder.double(
    min: Double = Double.MIN_VALUE,
    max: Double = Double.MAX_VALUE,
    allowInfinite: Boolean = false,
    allowNaN: Boolean = false
) = DoubleArgument().apply {
    this.minimum = min
    this.maximum = max
    this.allowInfinite = allowInfinite
    this.allowNaN = allowNaN
}

fun KommandBuilder.player() = PlayerArgument

fun KommandBuilder.target(filter: Predicate<Entity>? = null): TargetArgument {
    return if (filter == null) TargetArgument.instance else TargetArgument(filter)
}

fun KommandBuilder.playerTarget(filter: Predicate<Player>? = null): TargetArgument {
    return if (filter == null) TargetArgument.player else TargetArgument { it is Player && filter.test(it) }
}

fun KommandBuilder.world() = WorldArgument

fun <T> KommandBuilder.map(map: Map<String, T>): MapArgument<T> = MapArgument(map::get, map::keys)

fun <T> KommandBuilder.map(parser: (String) -> T, names: () -> Collection<String> = ::emptyList) =
    MapArgument(parser, names)

fun <T : Enum<*>> KommandBuilder.enum(values: List<T>) = EnumArgument(values)

fun <T : Enum<*>> KommandBuilder.enum(values: Array<T>): EnumArgument<T> = enum(values.asList())

fun Collection<String>.suggest(target: String): Collection<String> {
    if (isEmpty()) return emptyList()
    if (target.isEmpty()) return this

    return filter { it.startsWith(target, true) }
}

fun <T> Collection<T>.suggest(target: String, transform: (T) -> String = { it.toString() }): Collection<String> {
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