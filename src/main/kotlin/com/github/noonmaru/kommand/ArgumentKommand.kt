package com.github.noonmaru.kommand

import com.github.noonmaru.kommand.argument.KommandArgument
import org.bukkit.command.CommandSender

class ArgumentKommand(
    name: String,
    requirement: (CommandSender.() -> Boolean)?,
    executor: ((KommandContext) -> Unit)?,
    children: Collection<Kommand>,
    internal val argument: KommandArgument<Any>
) : Kommand(name, requirement, executor, children)

internal class ArgumentKommandBuilder(
    name: String, val argument: KommandArgument<Any>
) : KommandBuilder(name) {
    override fun build(): Kommand {
        return ArgumentKommand(name, requirement, executor, children.map { it.build() }, argument)
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