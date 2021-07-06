package io.github.monun.kommand.v1_17_R1

import com.mojang.brigadier.arguments.*
import com.mojang.brigadier.context.CommandContext
import io.github.monun.kommand.KommandArgument
import io.github.monun.kommand.KommandArgumentSupport
import io.github.monun.kommand.StringType
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

    override fun float(minimum: Float, maximum: Float): KommandArgument<Float> {
        return FloatArgumentType.floatArg(minimum, maximum) to FloatArgumentType::getFloat convert { it }
    }

    override fun double(minimum: Double, maximum: Double): KommandArgument<Double> {
        return DoubleArgumentType.doubleArg(minimum, maximum) to DoubleArgumentType::getDouble convert { it }
    }

    override fun long(minimum: Long, maximum: Long): KommandArgument<Long> {
        return LongArgumentType.longArg(minimum, maximum) to LongArgumentType::getLong convert { it }
    }

    override fun string(type: StringType): KommandArgument<String> {
        return when (type) {
            StringType.SINGLE_WORD -> StringArgumentType.word()
            StringType.QOUTABLE_PHRASE -> StringArgumentType.string()
            StringType.GREEDY_PHRASE -> StringArgumentType.greedyString()
        } to StringArgumentType::getString convert { it }
    }
}