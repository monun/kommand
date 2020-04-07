package com.github.noonmaru.kommand

import com.github.noonmaru.kommand.argument.KommandArgument
import com.google.common.collect.ImmutableList
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin


abstract class Kommand(
    val name: String,
    var requirement: (CommandSender.() -> Boolean)?,
    var executor: ((KommandContext) -> Unit)?,
    children: Collection<Kommand>
) {
    val children: List<Kommand> = ImmutableList.copyOf(children)

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
    var requirement: (CommandSender.() -> Boolean)? = null
    var executor: ((KommandContext) -> Unit)? = null
    val children = LinkedHashSet<KommandBuilder>()

    fun require(requirement: CommandSender.() -> Boolean) {
        this.requirement = requirement
    }

    fun executes(executor: (context: KommandContext) -> Unit) {
        this.executor = executor
    }

    fun then(name: String, init: KommandBuilder.() -> Unit) {
        children += LiteralKommandBuilder(name).apply(init)
    }

    fun then(argument: Pair<String, KommandArgument<Any>>, init: KommandBuilder.() -> Unit) {
        children += ArgumentKommandBuilder(argument.first, argument.second).apply(init)
    }

    internal abstract fun build(): Kommand
}

fun JavaPlugin.kommand(init: KommandDispatcherBuilder.() -> Unit): KommandDispatcher {
    return KommandDispatcherBuilder(this).apply(init).build()
}