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
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class KommandContext(
    val command: Command,
    val rawArguments: Array<out String>,
    val nodes: List<Kommand>
) {
    internal lateinit var executor: (KommandContext) -> Unit

    lateinit var sender: CommandSender
        internal set
    lateinit var alias: String
        internal set

    private val argumentsByName: Map<String, Pair<String, KommandArgument<*>>>

    init {
        val arguments = HashMap<String, Pair<String, KommandArgument<*>>>()

        nodes.forEachIndexed { index, kommand ->
            if (kommand is ArgumentKommand) {
                arguments[kommand.name] = rawArguments[index - 1] to kommand.argument
            }
        }

        this.argumentsByName = arguments
    }

    private fun argumentBy(name: String): Pair<String, KommandArgument<*>> {
        return argumentsByName[name] ?: throw IllegalArgumentException("[$name] is unknown argument name")
    }

    fun getArgument(name: String) = argumentBy(name).first

    @Suppress("UNCHECKED_CAST")
    fun <T> parseOrNullArgument(name: String): T? {
        val pair = argumentBy(name)
        val param = pair.first
        val argument = pair.second

        return argument.parse(this, param) as T
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> parseArgument(name: String): T {
        val pair = argumentBy(name)
        val param = pair.first
        val argument = pair.second

        return argument.parse(this, param) as T
            ?: throw KommandSyntaxException(argument.parseFailMessage.replace(KommandArgument.TOKEN, param))
    }
}