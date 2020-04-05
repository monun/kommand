package com.github.noonmaru.kommand

import org.bukkit.command.CommandSender

class LiteralKommand(
    name: String,
    requirement: (CommandSender.() -> Boolean)?,
    executor: ((KommandContext) -> Unit)?,
    children: Collection<Kommand>
) : Kommand(name, requirement, executor, children)

class LiteralKommandBuilder(name: String) : KommandBuilder(name) {
    override fun build(): LiteralKommand {
        return LiteralKommand(name, requirement, executor, children.map { it.build() }.toSet())
    }

    override fun hashCode() = name.hashCode()

    override fun equals(other: Any?): Boolean {
        if (other == this) return true

        if (other is LiteralKommandBuilder) {
            if (name == other.name) return true
        }

        return false
    }
}