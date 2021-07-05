package io.github.monun.kommand.internal.v1_17_R1

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import io.github.monun.kommand.KommandArgument
import io.github.monun.kommand.KommandSuggestion
import io.github.monun.kommand.argument.IntArgument
import io.github.monun.kommand.internal.*
import io.github.monun.kommand.internal.v1_17_R1.NMSArgument.argument
import io.github.monun.kommand.internal.v1_17_R1.NMSArgument.literal
import net.minecraft.commands.CommandSourceStack
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_17_R1.CraftServer
import kotlin.reflect.KClass

// text ->

class NMSBrigadier : Brigadier {
    private val argumentConverters = hashMapOf<KClass<*>, (KommandArgument<*>) -> ArgumentType<*>>()

    init {
        registerArgumentConverter(IntArgument::class) { IntegerArgumentType.integer(it.minimum, it.maximum) }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : KommandArgument<*>> registerArgumentConverter(
        argument: KClass<T>,
        converter: (T) -> ArgumentType<*>
    ) {
        argumentConverters[argument] = converter as (KommandArgument<*>) -> ArgumentType<*>
    }


    override fun register(kommand: RootKommand) {
        val nms = (Bukkit.getServer() as CraftServer).handle.server.commands.dispatcher
        val node = nms.register(kommand.convert() as LiteralArgumentBuilder<CommandSourceStack>)
        kommand.aliases.forEach { nms.register(literal(it).redirect(node)) }
    }

    private fun AbstractKommand.convert(): ArgumentBuilder<CommandSourceStack, *> {
        return when (this) {
            is LiteralKommand -> literal(name)
            is ArgumentKommand -> {
                val argument = argument as AbstractKommandArgument
                val type = argumentConverters[this::class]?.invoke(argument) ?: error("Unknown argument type ${this.javaClass.name}")
                argument(name, type).apply {
                    argument.suggestion?.let { (mode, provider) ->
                        suggests { context, suggestionsBuilder ->

                            val suggestion = KommandSuggestionImpl()
                            provider(NMSKommandContext(context), suggestion)

                            if (mode == KommandSuggestion.Mode.include) type.listSuggestions(context, suggestionsBuilder)
                            else suggestionsBuilder.buildFuture()
                        }
                    }
                }
            }
            else -> error("Unknown kommand type ${this.javaClass.name}")
        }.also { nms ->
            requires?.run { nms.requires { invoke(NMSKommandSource(it)) } }
            executor?.run { nms.executes { invoke(NMSKommandContext(it)) } }
            nodes.forEach { nms.then(it.convert()) }
        }
    }
}
