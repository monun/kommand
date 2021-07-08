package io.github.monun.kommand.util

import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.util.Vector

data class Position(val x: Double, val y: Double, val z: Double) {
    val asVector: Vector
        get() = Vector(x, y, z)

    fun toLocation(world: World?, yaw: Float, pitch: Float): Location {
        return Location(world, x, y, z, yaw, pitch)
    }

    fun toLocation(world: World?, rotation: Rotation): Location {
        return Location(world, x, y, z, rotation.yaw, rotation.pitch)
    }
}

data class Position2D(val x: Double, val z: Double) {
    fun withY(y: Double): Position {
        return Position(x, y, z)
    }
}

data class BlockPosition(val x: Int, val y: Int, val z: Int) {
    val asVector: Vector
        get() = Vector(x, y, z)

    fun toBlock(world: World): Block {
        return world.getBlockAt(x, y, z)
    }
}

data class BlockPosition2D(val x: Int, val z: Int) {
    fun withY(y: Int): BlockPosition {
        return BlockPosition(x, y, z)
    }
}