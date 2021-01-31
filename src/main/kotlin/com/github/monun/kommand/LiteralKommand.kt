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

import org.bukkit.command.CommandSender

class LiteralKommand(
    name: String,
    permission: (() -> String)?,
    requirement: (CommandSender.() -> Boolean)?,
    executor: ((KommandContext) -> Unit)?,
    children: Collection<Kommand>
) : Kommand(name, permission, requirement, executor, children)

class LiteralKommandBuilder(name: String) : KommandBuilder(name) {
    override fun build(): LiteralKommand {
        return LiteralKommand(name, permission, requirement, executor, children.map { it.build() }.toSet())
    }

    override fun hashCode() = name.hashCode()

    override fun equals(other: Any?): Boolean {
        if (other === this) return true

        if (other is LiteralKommandBuilder) {
            if (name == other.name) return true
        }

        return false
    }
}