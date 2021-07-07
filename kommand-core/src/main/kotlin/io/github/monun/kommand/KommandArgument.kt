package io.github.monun.kommand

import com.google.gson.JsonObject
import io.github.monun.kommand.loader.LibraryLoader
import net.kyori.adventure.text.Component
import net.md_5.bungee.api.ChatColor
import org.bukkit.World

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

    // net.mincraft.commands.arguments

    fun angle(): KommandArgument<Float>

    fun color(): KommandArgument<ChatColor>

    fun component(): KommandArgument<Component>

    fun compoundTag(): KommandArgument<JsonObject>

    fun dimension(): KommandArgument<World>

    // net.mincraft.commands.arguments.blocks

    // net.mincraft.commands.arguments.coordinates

    // net.mincraft.commands.arguments.item
}

enum class StringType {
    SINGLE_WORD,
    QOUTABLE_PHRASE,
    GREEDY_PHRASE
}