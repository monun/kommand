package io.github.monun.kommand.v1_17_1.wrapper

import io.github.monun.kommand.KommandSource
import io.github.monun.kommand.wrapper.EntityAnchor
import net.minecraft.commands.arguments.EntityAnchorArgument
import net.minecraft.world.entity.Entity
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity
import org.bukkit.util.Vector

class NMSEntityAnchor(
    private val nms: EntityAnchorArgument.Anchor
) : EntityAnchor {
    override val name: String
        get() = nms.name

    override fun applyTo(entity: org.bukkit.entity.Entity): Vector {
        val nmsEntity: Entity = (entity as CraftEntity).handle
        return nms.apply(nmsEntity).run { Vector(x, y, z) }
    }

    override fun applyTo(source: KommandSource): Vector {
        return applyTo(source.entity)
    }
}