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

package com.github.monun.kommand

import com.github.monun.kommand.argument.KommandArgument
import org.bukkit.command.CommandSender

class ArgumentKommand(
    name: String,
    permission: (() -> String)?,
    requirement: (CommandSender.() -> Boolean)?,
    executor: ((KommandContext) -> Unit)?,
    children: Collection<Kommand>,
    internal val argument: KommandArgument<*>
) : Kommand(name, permission, requirement, executor, children)

internal class ArgumentKommandBuilder(
    name: String,
    val argument: KommandArgument<*>
) : KommandBuilder(name) {

    override fun build(): Kommand {
        return ArgumentKommand(name, permission, requirement, executor, children.map { it.build() }, argument)
    }

    override fun hashCode() = name.hashCode().inv()

    override fun equals(other: Any?): Boolean {
        if (other == this) return true

        if (other is ArgumentKommandBuilder) {
            if (name == other.name && argument == other.argument)
                return true
        }

        return false
    }
}