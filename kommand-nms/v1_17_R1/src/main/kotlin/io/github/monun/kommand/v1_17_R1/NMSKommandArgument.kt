package io.github.monun.kommand.v1_17_R1

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionsBuilder
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