package io.github.monun.kommand.v1_17_R1

import com.mojang.brigadier.arguments.*
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import io.github.monun.kommand.KommandArgument
import io.github.monun.kommand.KommandArgumentSupport
import io.github.monun.kommand.StringType
import io.github.monun.kommand.internal.AbstractKommandArgument
import net.minecraft.commands.CommandSourceStack
import java.lang.reflect.Method

open class NMSKommandArgument<T, U>(
    val type: ArgumentType<T>,
    private val provider: (CommandContext<CommandSourceStack>, name: String) -> T,
    private val converter: (T) -> U
) : AbstractKommandArgument<U>() {
    private companion object {
        private val originalMethod: Method = ArgumentType::class.java.declaredMethods.find { method ->
            val parameterTypes = method.parameterTypes

            parameterTypes.count() == 2
                    && parameterTypes[0] == CommandContext::class.java
                    && parameterTypes[1] == SuggestionsBuilder::class.java
        } ?: error("Not found listSuggestion")

        private val defaultSuggestions = hashMapOf<Class<*>, Boolean>()

        private fun checkDefaultSuggestions(type: Class<*>): Boolean = defaultSuggestions.computeIfAbsent(type) {
            originalMethod.declaringClass != type.getMethod(
                originalMethod.name,
                *originalMethod.parameterTypes
            ).declaringClass
        }
    }

    internal

    val hasDefaultSuggestion: Boolean by lazy(LazyThreadSafetyMode.NONE) {
        checkDefaultSuggestions(type.javaClass)
    }

    @Suppress("UNCHECKED_CAST")
    fun from(context: CommandContext<CommandSourceStack>, name: String): U {
        val value = provider(context, name)
        return converter(value)
    }
}

private infix fun <T, U> Pair<ArgumentType<T>, (CommandContext<CommandSourceStack>, name: String) -> T>.convert(
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