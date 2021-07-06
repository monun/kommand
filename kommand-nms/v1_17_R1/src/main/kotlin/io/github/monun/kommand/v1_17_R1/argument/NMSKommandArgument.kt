package io.github.monun.kommand.v1_17_R1.argument

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import io.github.monun.kommand.internal.AbstractKommandArgument
import net.minecraft.commands.CommandSourceStack
import java.lang.reflect.Method

abstract class NMSKommandArgument<T, U : ArgumentType<*>>(
    val type: U
) : AbstractKommandArgument<T>() {
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

    val hasDefaultSuggestion: Boolean by lazy(LazyThreadSafetyMode.NONE) {
        checkDefaultSuggestions(type.javaClass)
    }

    abstract fun from(context: CommandContext<CommandSourceStack>, name: String): T
}