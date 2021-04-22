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
import org.bukkit.Bukkit
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import java.util.function.Predicate

class TargetArgument(
    filter: Predicate<Entity>? = null
) : KommandArgument<List<Entity>> {
    companion object {
        private val targetSelectors = ImmutableList.copyOf(
            "aeprs".map { "@$it" }
        )

        val instance = TargetArgument()

        val player = TargetArgument { it is Player }
    }

    private val filter = filter?.negate()

    override fun parse(context: KommandContext, param: String): List<Entity>? {
        Bukkit.getServer().runCatching {
            selectEntities(context.sender, param)
        }.onSuccess { list ->
            filter?.let { list.removeIf(it) }
            return list
        }

        return null
    }

    override fun suggest(context: KommandContext, target: String): Collection<String> {
        return (targetSelectors + Bukkit.getOnlinePlayers().map { it.name }).suggest(target)
    }
}