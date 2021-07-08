package io.github.monun.kommand

import com.destroystokyo.paper.profile.PlayerProfile
import com.google.gson.JsonObject
import io.github.monun.kommand.loader.LibraryLoader
import io.github.monun.kommand.wrapper.*
import net.kyori.adventure.text.Component
import org.bukkit.Axis
import org.bukkit.ChatColor
import org.bukkit.NamespacedKey
import org.bukkit.Particle
import org.bukkit.World
import org.bukkit.advancement.Advancement
import org.bukkit.block.Block
import org.bukkit.block.data.BlockData
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe
import org.bukkit.potion.PotionEffectType
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Objective
import org.bukkit.scoreboard.Team
import java.util.EnumSet
import java.util.UUID

// 인수
interface KommandArgument<T> {
    companion object : KommandArgumentSupport by KommandArgumentSupport.INSTANCE

    fun suggests(provider: KommandSuggestion.(context: KommandContext) -> Unit)
}

interface KommandArgumentSupport {
    companion object {
        val INSTANCE = LibraryLoader.loadNMS(KommandArgumentSupport::class.java)
    }

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

    fun entityAnchor(): KommandArgument<EntityAnchor>

    fun entity(): KommandArgument<Entity>

    fun entities(): KommandArgument<Collection<Entity>>

    fun player(): KommandArgument<Player>

    fun players(): KommandArgument<Collection<Player>>

    fun summonableEntity(): KommandArgument<NamespacedKey>

    fun profile(): KommandArgument<Collection<PlayerProfile>>

    fun enchantment(): KommandArgument<Enchantment>

    fun message(): KommandArgument<Component>

    fun mobEffect(): KommandArgument<PotionEffectType>

    //    fun nbtPath(): KommandArgument<*> [NbtTagArgument]

    fun objective(): KommandArgument<Objective>

    fun objectiveCriteria(): KommandArgument<String>

    //    fun operation(): KommandArgument<*> [OperationArgument]

    fun particle(): KommandArgument<Particle>

    fun intRange(): KommandArgument<IntRange>

    fun doubleRange(): KommandArgument<ClosedRange<Double>>

    fun advancement(): KommandArgument<Advancement>

    fun recipe(): KommandArgument<Recipe>

    //    ResourceLocationArgument#getPredicate()

    //    ResourceLocationArgument#getItemModifier()

    fun displaySlot(): KommandArgument<DisplaySlot>

    fun score(): KommandArgument<String>

    fun scores(): KommandArgument<Collection<String>>

    fun slot(): KommandArgument<Int>

    fun team(): KommandArgument<Team>

    fun time(): KommandArgument<Int>

    fun uuid(): KommandArgument<UUID>

    // net.minecraft.commands.arguments.blocks

    fun blockPredicate(): KommandArgument<(Block) -> Boolean>

    fun blockState(): KommandArgument<BlockData>

    // net.minecraft.commands.arguments.coordinates

    fun blockPosition(type: PositionLoadType = PositionLoadType.LOADED): KommandArgument<BlockPosition3D>

    fun blockPosition2D(): KommandArgument<BlockPosition2D>

    fun position(): KommandArgument<Position3D>

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