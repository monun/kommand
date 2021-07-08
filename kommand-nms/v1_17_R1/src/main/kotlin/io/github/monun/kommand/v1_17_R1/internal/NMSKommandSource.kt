package io.github.monun.kommand.v1_17_R1.internal

import io.github.monun.kommand.KommandSource
import io.github.monun.kommand.util.Position
import io.github.monun.kommand.util.Rotation
import io.github.monun.kommand.v1_17_R1.wrapper.NMSEntityAnchor
import io.github.monun.kommand.wrapper.EntityAnchor
import io.papermc.paper.brigadier.PaperBrigadier
import net.kyori.adventure.text.Component
import net.minecraft.commands.CommandSourceStack
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.command.CommandSender
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

class NMSKommandSource(
    val nms: CommandSourceStack
) : KommandSource {
    override val displayName: Component
        get() = PaperBrigadier.componentFromMessage(nms.displayName)

    override val sender: CommandSender
        get() = nms.bukkitSender

    override val entity: Entity
        get() = nms.entityOrException.bukkitEntity

    override val entityOrNull: Entity?
        get() = nms.entity?.bukkitEntity

    override val player: Player
        get() = nms.playerOrException.bukkitEntity

    override val playerOrNull: Player?
        get() = nms.entity?.bukkitEntity?.takeIf { it is Player } as Player?

    override val position: Position
        get() = nms.position.run { Position(x, y, z) }

    override val rotation: Rotation
        get() = nms.rotation.run { Rotation(x, y) }

    override val anchor: EntityAnchor
        get() = NMSEntityAnchor(nms.anchor)

    override val world: World
        get() = nms.level.world

    override val location: Location
        get() = position.toLocation(nms.level.world, rotation)

    override fun hasPermission(level: Int): Boolean {
        return nms.hasPermission(level)
    }

    override fun hasPermission(level: Int, bukkitPermission: String): Boolean {
        return nms.hasPermission(level, bukkitPermission)
    }
}