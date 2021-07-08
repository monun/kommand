package io.github.monun.kommand

import io.github.monun.kommand.wrapper.Position
import io.github.monun.kommand.wrapper.Rotation
import org.bukkit.command.CommandSender
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

// 명령 발신자 정보
interface KommandSource {
    val sender: CommandSender
    val entity: Entity
    val entityOrNull: Entity?
    val player: Player
    val playerOrNull: Player?
    val position: Position
    val rotation: Rotation
}