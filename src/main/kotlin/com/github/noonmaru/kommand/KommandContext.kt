package com.github.noonmaru.kommand

import com.github.noonmaru.kommand.argument.KommandArgument
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

    private val argumentsByName: Map<String, Pair<String, KommandArgument<Any>>>

    init {
        val arguments = HashMap<String, Pair<String, KommandArgument<Any>>>()

        nodes.forEachIndexed { index, kommand ->
            if (kommand is ArgumentKommand) {
                arguments[kommand.name] = rawArguments[index - 1] to kommand.argument
            }
        }

        this.argumentsByName = arguments
    }

    private fun argumentBy(name: String): Pair<String, KommandArgument<Any>> {
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