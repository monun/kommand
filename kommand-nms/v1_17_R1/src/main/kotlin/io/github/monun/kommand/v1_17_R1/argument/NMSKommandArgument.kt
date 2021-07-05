package io.github.monun.kommand.v1_17_R1.argument

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import io.github.monun.kommand.KommandArgument
import net.minecraft.commands.CommandSourceStack

abstract class NMSKommandArgument<T, U: ArgumentType<*>>(
    val type: U
): KommandArgument<T> {
    abstract fun from(context: CommandContext<CommandSourceStack>, name: String): T
}