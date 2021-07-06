package io.github.monun.kommand.v1_17_R1

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.context.CommandContext
import io.github.monun.kommand.KommandArgument
import io.github.monun.kommand.KommandArgumentSupport
import net.minecraft.commands.CommandSourceStack

infix fun <T, U> Pair<ArgumentType<T>, (CommandContext<CommandSourceStack>, name: String) -> T>.convert(
    converter: (T) -> U
) = NMSKommandArgument(first, second, converter)


class NMSKommandArgumentSupport : KommandArgumentSupport {
    override fun bool(): KommandArgument<Boolean> {
        return BoolArgumentType.bool() to BoolArgumentType::getBool convert { it }
    }

    override fun int(minimum: Int, maximum: Int): KommandArgument<Int> {
        return IntegerArgumentType.integer(minimum, maximum) to IntegerArgumentType::getInteger convert { it }
    }
}