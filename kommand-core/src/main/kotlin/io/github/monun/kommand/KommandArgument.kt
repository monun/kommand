package io.github.monun.kommand

import com.google.gson.JsonObject
import io.github.monun.kommand.loader.LibraryLoader
import io.github.monun.kommand.util.BlockPosition
import io.github.monun.kommand.util.BlockPosition2D
import io.github.monun.kommand.util.Position
import io.github.monun.kommand.util.Position2D
import io.github.monun.kommand.util.Rotation
import net.kyori.adventure.text.Component
import org.bukkit.Axis
import org.bukkit.ChatColor
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.block.data.BlockData
import org.bukkit.inventory.ItemStack
import java.util.EnumSet

// 인수
interface KommandArgument<T> {
    companion object : KommandArgumentSupport by LibraryLoader.load(KommandArgumentSupport::class.java)

    fun suggests(provider: KommandSuggestion.(context: KommandContext) -> Unit)
}

interface KommandArgumentSupport {
    // com.mojang.brigadier.arguments

    fun bool(): KommandArgument<Boolean>

    fun int(minimum: Int = Int.MIN_VALUE, maximum: Int = Int.MAX_VALUE): KommandArgument<Int>

    fun float(minimum: Float, maximum: Float): KommandArgument<Float>

    fun double(minimum: Double, maximum: Double): KommandArgument<Double>

    fun long(minimum: Long, maximum: Long): KommandArgument<Long>

    fun string(type: StringType = StringType.SINGLE_WORD): KommandArgument<String>

    // net.minecraft.commands.arguments

    fun angle(): KommandArgument<Float>

    fun color(): KommandArgument<ChatColor>

    fun component(): KommandArgument<Component>

    fun compoundTag(): KommandArgument<JsonObject>

    fun dimension(): KommandArgument<World>

    // net.minecraft.commands.arguments.blocks

    fun blockPredicate(): KommandArgument<(Block) -> Boolean>

    fun blockState(): KommandArgument<BlockData>

    // net.minecraft.commands.arguments.coordinates

    fun blockPosition(type: PositionLoadType = PositionLoadType.LOADED): KommandArgument<BlockPosition>

    fun blockPosition2D(): KommandArgument<BlockPosition2D>

    fun position(): KommandArgument<Position>

    fun position2D(): KommandArgument<Position2D>

    fun rotation(): KommandArgument<Rotation>

    fun swizzle(): KommandArgument<EnumSet<Axis>>

    // net.minecraft.commands.arguments.item

    fun function(): KommandArgument<() -> Unit>

    fun item(): KommandArgument<ItemStack>

    fun itemPredicate(): KommandArgument<(ItemStack) -> Boolean>
}

enum class StringType {
    SINGLE_WORD,
    QUOTABLE_PHRASE,
    GREEDY_PHRASE
}

enum class PositionLoadType {
    LOADED,
    SPAWNABLE
}