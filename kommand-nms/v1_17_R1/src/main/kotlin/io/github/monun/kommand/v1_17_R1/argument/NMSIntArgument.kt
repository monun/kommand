package io.github.monun.kommand.v1_17_R1.argument

import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.context.CommandContext
import io.github.monun.kommand.argument.IntArgument
import net.minecraft.commands.CommandSourceStack

class NMSIntArgument : NMSKommandArgument<Int, IntegerArgumentType>(
    IntegerArgumentType.integer()
), IntArgument {
    override fun from(context: CommandContext<CommandSourceStack>, name: String): Int {
        return IntegerArgumentType.getInteger(context, name)
    }
}