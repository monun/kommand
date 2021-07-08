package io.github.monun.kommand

import io.github.monun.kommand.util.Position
import io.github.monun.kommand.util.Rotation
import io.github.monun.kommand.wrapper.EntityAnchor
import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.command.CommandSender
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

// 명령 발신자 정보
interface KommandSource {
    val displayName: Component
    val sender: CommandSender
    val entity: Entity
    val entityOrNull: Entity?
    val player: Player
    val playerOrNull: Player?
    val position: Position
    val rotation: Rotation
    val anchor: EntityAnchor
    val world: World
    val location: Location

    fun hasPermission(level: Int): Boolean
    fun hasPermission(level: Int, bukkitPermission: String): Boolean
}