package io.github.monun.kommand.wrapper

import org.bukkit.Location
import org.bukkit.World
import org.bukkit.util.Vector

class Position(
    val x: Double,
    val y: Double,
    val z: Double
)

fun Position.toVector() = Vector(x, y, z)

fun Position.toLocation(world: World?, yaw: Float, pitch: Float) = Location(world, x, y, z, yaw, pitch)

fun Position.toLocation(world: World?, rotation: Rotation) = Location(world, x, y, z, rotation.yaw, rotation.pitch)