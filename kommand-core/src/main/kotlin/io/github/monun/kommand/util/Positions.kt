package io.github.monun.kommand.util

import org.bukkit.Location
import org.bukkit.World
import org.bukkit.util.Vector

data class Position(
    val x: Double,
    val y: Double,
    val z: Double
)

data class Position2D(
    val x: Double,
    val z: Double
)

data class BlockPosition(
    val x: Int,
    val y: Int,
    val z: Int
)

data class BlockPosition2D(
    val x: Int,
    val z: Int
)

fun Position.toVector() = Vector(x, y, z)

fun Position.toLocation(world: World?, yaw: Float, pitch: Float) = Location(world, x, y, z, yaw, pitch)

fun Position.toLocation(world: World?, rotation: Rotation) = Location(world, x, y, z, rotation.yaw, rotation.pitch)

fun Position2D.to3D(y: Double) = Position(x, y, z)

fun BlockPosition.toVector() = Vector(x, y, z)

fun BlockPosition.toLocation(world: World?, yaw: Float, pitch: Float) =
    Location(world, x.toDouble(), y.toDouble(), z.toDouble(), yaw, pitch)

fun BlockPosition.toLocation(world: World?, rotation: Rotation) =
    Location(world, x.toDouble(), y.toDouble(), z.toDouble(), rotation.yaw, rotation.pitch)

fun BlockPosition2D.to3D(y: Int) = BlockPosition(x, y, z)