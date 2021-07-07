package io.github.monun.kommand.v1_17_R1

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.arguments.FloatArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.LongArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import io.github.monun.kommand.KommandArgument
import io.github.monun.kommand.KommandArgumentSupport
import io.github.monun.kommand.PositionLoadType
import io.github.monun.kommand.StringType
import io.github.monun.kommand.internal.AbstractKommandArgument
import io.github.monun.kommand.util.BlockPosition
import io.github.monun.kommand.util.BlockPosition2D
import io.github.monun.kommand.util.Position
import io.github.monun.kommand.util.Position2D
import io.github.monun.kommand.util.Rotation
import io.papermc.paper.brigadier.PaperBrigadier
import net.kyori.adventure.text.Component
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.arguments.AngleArgument
import net.minecraft.commands.arguments.ColorArgument
import net.minecraft.commands.arguments.ComponentArgument
import net.minecraft.commands.arguments.CompoundTagArgument
import net.minecraft.commands.arguments.DimensionArgument
import net.minecraft.commands.arguments.blocks.BlockPredicateArgument
import net.minecraft.commands.arguments.blocks.BlockStateArgument
import net.minecraft.commands.arguments.coordinates.BlockPosArgument
import net.minecraft.commands.arguments.coordinates.ColumnPosArgument
import net.minecraft.commands.arguments.coordinates.RotationArgument
import net.minecraft.commands.arguments.coordinates.SwizzleArgument
import net.minecraft.commands.arguments.coordinates.Vec2Argument
import net.minecraft.commands.arguments.coordinates.Vec3Argument
import net.minecraft.commands.arguments.item.FunctionArgument
import net.minecraft.commands.arguments.item.ItemArgument
import net.minecraft.commands.arguments.item.ItemPredicateArgument
import net.minecraft.world.level.block.state.pattern.BlockInWorld
import org.bukkit.Axis
import org.bukkit.ChatColor
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.block.data.BlockData
import org.bukkit.craftbukkit.v1_17_R1.block.CraftBlock
import org.bukkit.craftbukkit.v1_17_R1.block.data.CraftBlockData
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack
import org.bukkit.craftbukkit.v1_17_R1.util.CraftChatMessage
import org.bukkit.inventory.ItemStack
import java.lang.reflect.Method
import java.util.EnumSet

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
    // com.mojang.brigadier.arguments

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
            StringType.QUOTABLE_PHRASE -> StringArgumentType.string()
            StringType.GREEDY_PHRASE -> StringArgumentType.greedyString()
        } provide StringArgumentType::getString
    }

    // net.minecraft.commands.arguments

    override fun angle(): KommandArgument<Float> {
        return AngleArgument.angle() provide AngleArgument::getAngle
    }

    override fun color(): KommandArgument<ChatColor> {
        return ColorArgument.color() provide { context, name ->
            CraftChatMessage.getColor(ColorArgument.getColor(context, name))
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

    // net.minecraft.commands.arguments.blocks

    override fun blockPredicate(): KommandArgument<(Block) -> Boolean> {
        return BlockPredicateArgument.blockPredicate() provide { context, name ->
            { block ->
                BlockPredicateArgument.getBlockPredicate(context, name)
                    .test(BlockInWorld(context.source.level, (block as CraftBlock).position, true))
            }
        }
    }

    override fun blockState(): KommandArgument<BlockData> {
        return BlockStateArgument.block() provide { context, name ->
            CraftBlockData.fromData(BlockStateArgument.getBlock(context, name).state)
        }
    }

    // net.minecraft.commands.arguments.coordinates

    override fun blockPosition(type: PositionLoadType): KommandArgument<BlockPosition> {
        return BlockPosArgument.blockPos() provide { context, name ->
            val blockPosition = when (type) {
                PositionLoadType.LOADED -> BlockPosArgument.getLoadedBlockPos(context, name)
                PositionLoadType.SPAWNABLE -> BlockPosArgument.getSpawnablePos(context, name)
            }
            BlockPosition(blockPosition.x, blockPosition.y, blockPosition.z)
        }
    }

    override fun blockPosition2D(): KommandArgument<BlockPosition2D> {
        return ColumnPosArgument.columnPos() provide { context, name ->
            val columnPosition = ColumnPosArgument.getColumnPos(context, name)
            BlockPosition2D(columnPosition.x, columnPosition.z)
        }
    }

    override fun position(): KommandArgument<Position> {
        return Vec3Argument.vec3() provide { context, name ->
            val vec3 = Vec3Argument.getVec3(context, name)
            Position(vec3.x, vec3.y, vec3.z)
        }
    }

    override fun position2D(): KommandArgument<Position2D> {
        return Vec2Argument.vec2() provide { context, name ->
            val vec2 = Vec2Argument.getVec2(context, name)
            Position2D(vec2.x.toDouble(), vec2.y.toDouble())
        }
    }

    override fun rotation(): KommandArgument<Rotation> {
        return RotationArgument.rotation() provide { context, name ->
            val rotation = RotationArgument.getRotation(context, name).getRotation(context.source)
            Rotation(rotation.x, rotation.y)
        }
    }

    override fun swizzle(): KommandArgument<EnumSet<Axis>> {
        return SwizzleArgument.swizzle() provide { context, name ->
            EnumSet.copyOf(SwizzleArgument.getSwizzle(context, name).map { axis ->
                Axis.valueOf(axis.getName().uppercase())
            })
        }
    }

    // net.minecraft.commands.arguments.item

    override fun function(): KommandArgument<() -> Unit> {
        return FunctionArgument.functions() provide { context, name ->
            {
                FunctionArgument.getFunctions(context, name).map { function ->
                    context.source.server.functions.execute(function, context.source)
                }
            }
        }
    }

    override fun item(): KommandArgument<ItemStack> {
        return ItemArgument.item() provide { context, name ->
            CraftItemStack.asBukkitCopy(ItemArgument.getItem(context, name).createItemStack(1, false))
        }
    }

    override fun itemPredicate(): KommandArgument<(ItemStack) -> Boolean> {
        return ItemPredicateArgument.itemPredicate() provide { context, name ->
            { itemStack ->
                ItemPredicateArgument.getItemPredicate(context, name).test(CraftItemStack.asNMSCopy(itemStack))
            }
        }
    }
}
