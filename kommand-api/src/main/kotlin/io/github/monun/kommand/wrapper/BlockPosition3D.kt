package io.github.monun.kommand.wrapper

import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.util.Vector

data class BlockPosition3D(val x: Int, val y: Int, val z: Int) {
    val asVector: Vector
        get() = Vector(x, y, z)

    fun toBlock(world: World): Block {
        return world.getBlockAt(x, y, z)
    }
}