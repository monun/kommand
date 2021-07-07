package io.github.monun.kommand.v1_17_R1

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.mojang.brigadier.arguments.*
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import io.github.monun.kommand.KommandArgument
import io.github.monun.kommand.KommandArgumentSupport
import io.github.monun.kommand.StringType
import io.github.monun.kommand.internal.AbstractKommandArgument
import io.papermc.paper.brigadier.PaperBrigadier
import net.kyori.adventure.text.Component
import net.md_5.bungee.api.ChatColor
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.arguments.*
import org.bukkit.World
import java.lang.reflect.Method

open class NMSKommandArgument<T>(
    val type: ArgumentType<*>,
    private val provider: (CommandContext<CommandSourceStack>, name: String) -> T,
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

    internal val hasDefaultSuggestion: Boolean by lazy(LazyThreadSafetyMode.NONE) {
        checkDefaultSuggestions(type.javaClass)
    }

    @Suppress("UNCHECKED_CAST")
    fun from(context: CommandContext<CommandSourceStack>, name: String): T {
        return provider(context, name)
    }
}

infix fun <T> ArgumentType<*>.provide(provider: (CommandContext<CommandSourceStack>, String) -> T): NMSKommandArgument<T> {
    return NMSKommandArgument(this, provider)
}

class NMSKommandArgumentSupport : KommandArgumentSupport {
    override fun bool(): KommandArgument<Boolean> {
        return BoolArgumentType.bool() provide BoolArgumentType::getBool
    }

    override fun int(minimum: Int, maximum: Int): KommandArgument<Int> {
        return IntegerArgumentType.integer(minimum, maximum) provide IntegerArgumentType::getInteger
    }

    override fun float(minimum: Float, maximum: Float): KommandArgument<Float> {
        return FloatArgumentType.floatArg(minimum, maximum) provide FloatArgumentType::getFloat
    }

    override fun double(minimum: Double, maximum: Double): KommandArgument<Double> {
        return DoubleArgumentType.doubleArg(minimum, maximum) provide DoubleArgumentType::getDouble
    }

    override fun long(minimum: Long, maximum: Long): KommandArgument<Long> {
        return LongArgumentType.longArg(minimum, maximum) provide LongArgumentType::getLong
    }

    override fun string(type: StringType): KommandArgument<String> {
        return when (type) {
            StringType.SINGLE_WORD -> StringArgumentType.word()
            StringType.QOUTABLE_PHRASE -> StringArgumentType.string()
            StringType.GREEDY_PHRASE -> StringArgumentType.greedyString()
        } provide StringArgumentType::getString
    }

    override fun angle(): KommandArgument<Float> {
        return AngleArgument.angle() provide AngleArgument::getAngle
    }

    override fun color(): KommandArgument<ChatColor> {
        return ColorArgument.color() provide { context, name ->
            ColorArgument.getColor(context, name)
                .let { ChatColor.of(it.getName()) ?: error("Not found color") }
        }
    }

    override fun component(): KommandArgument<Component> {
        return ComponentArgument.textComponent() provide { context, name ->
            val nmsComponent = ComponentArgument.getComponent(context, name)
            PaperBrigadier.componentFromMessage(nmsComponent)
        }
    }

    override fun compoundTag(): KommandArgument<JsonObject> {
        return CompoundTagArgument.compoundTag() provide { context, name ->
            val compoundTag = CompoundTagArgument.getCompoundTag(context, name)
            JsonParser().parse(compoundTag.toString()) as JsonObject
        }
    }

    override fun dimension(): KommandArgument<World> {
        return DimensionArgument.dimension() provide { context, name ->
            DimensionArgument.getDimension(context, name).world
        }
    }

//    fun anchor(): KommandArgument<World> {
//        return EntityAnchorArgument.anchor() provide { context, name ->
//            val nmsAnchor = EntityAnchorArgument.getAnchor(context, name)
//            EntityAnchorArgument.Anchor.EYES
//        }
//    }
}
