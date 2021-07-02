package io.github.monun.kommand.internal.v1_17_R1

import com.mojang.brigadier.context.CommandContext
import io.github.monun.kommand.KommandContext
import io.github.monun.kommand.KommandSource
import net.minecraft.commands.CommandSourceStack
import org.bukkit.command.CommandSender

class NMSKommandContext(
    val nms: CommandContext<CommandSourceStack>
) : KommandContext {
    override val source by lazy { NMSKommandSource(nms.source) }
}

class NMSKommandSource(
    val nms: CommandSourceStack
) : KommandSource {
    val sender: CommandSender
        get() = nms.bukkitSender

    val entity: org.bukkit.entity.Entity
        get() = nms.entityOrException.bukkitEntity

    val player: org.bukkit.entity.Player
        get() = nms.playerOrException.bukkitEntity
}



