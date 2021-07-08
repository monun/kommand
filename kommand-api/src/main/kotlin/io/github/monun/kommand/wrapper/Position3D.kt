package io.github.monun.kommand.wrapper

import org.bukkit.Location
import org.bukkit.World
import org.bukkit.util.Vector

data class Position3D(val x: Double, val y: Double, val z: Double) {
    val asVector: Vector
        get() = Vector(x, y, z)

    fun toLocation(world: World?, yaw: Float, pitch: Float): Location {
        return Location(world, x, y, z, yaw, pitch)
    }

    fun toLocation(world: World?, rotation: Rotation): Location {
        return Location(world, x, y, z, rotation.yaw, rotation.pitch)
    }
}