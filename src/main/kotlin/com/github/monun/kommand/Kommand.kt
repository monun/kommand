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
import com.google.common.collect.ImmutableList
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin


abstract class Kommand(
    val name: String,
    val permission: (() -> String)?,
    val requirement: (CommandSender.() -> Boolean)?,
    val executor: ((KommandContext) -> Unit)?,
    children: Collection<Kommand>
) {
    internal val children: List<Kommand> = ImmutableList.copyOf(children)

    fun getChild(arg: String): Kommand? {
        for (child in children) {
            if (child is ArgumentKommand
                || (child is LiteralKommand && child.name == arg)
            ) {
                return child
            }
        }

        return null
    }
}

abstract class KommandBuilder(val name: String) {
    internal var permission: (() -> String)? = null
    internal var requirement: (CommandSender.() -> Boolean)? = null
    internal var executor: ((KommandContext) -> Unit)? = null
    internal val children = LinkedHashSet<KommandBuilder>()

    fun permission(permission: (() -> String)?) {
        this.permission = permission
    }

    fun require(requirement: CommandSender.() -> Boolean) {
        this.requirement = requirement
    }

    fun executes(executor: (context: KommandContext) -> Unit) {
        this.executor = executor
    }

    fun then(name: String, init: KommandBuilder.() -> Unit) {
        children += LiteralKommandBuilder(name).apply(init)
    }

    fun then(
        argument: Pair<String, KommandArgument<*>>,
        vararg subArguments: Pair<String, KommandArgument<*>>,
        init: KommandBuilder.() -> Unit
    ) {
        var child = ArgumentKommandBuilder(argument.first, argument.second)
        this.children += child

        for ((name, arg) in subArguments) {
            val grandChild = ArgumentKommandBuilder(name, arg)
            child.children += grandChild
            child = grandChild
        }

        child.apply(init)
    }

    internal abstract fun build(): Kommand
}

fun JavaPlugin.kommand(init: KommandDispatcherBuilder.() -> Unit): KommandDispatcher {
    return KommandDispatcherBuilder(this).apply(init).build()
}