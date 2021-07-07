package io.github.monun.kommand.v1_17_R1.wrapper

import io.github.monun.kommand.KommandSource
import io.github.monun.kommand.v1_17_R1.internal.NMSKommandSource
import io.github.monun.kommand.wrapper.EntityAnchor
import net.minecraft.commands.arguments.EntityAnchorArgument
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity
import org.bukkit.entity.Entity
import org.bukkit.util.Vector

class NMSEntityAnchor(
    private val nms: EntityAnchorArgument.Anchor
) : EntityAnchor {
    override val name: String
        get() = nms.name

    override fun applyTo(entity: Entity): Vector {
        return nms.apply((entity as CraftEntity).handle).run { Vector(x, y, z) }
    }

    override fun applyTo(source: KommandSource): Vector {
        return nms.apply((source as NMSKommandSource).nms).run { Vector(x, y, z) }
    }
}