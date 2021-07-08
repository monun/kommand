package io.github.monun.kommand.wrapper

import io.github.monun.kommand.KommandSource
import org.bukkit.entity.Entity
import org.bukkit.util.Vector

interface EntityAnchor {
    companion object {
        val FEET by lazy { WrapperSupport.entityAnchorFeet() }
        val EYES by lazy { WrapperSupport.entityAnchorEyes() }
    }

    val name: String

    fun applyTo(entity: Entity): Vector

    fun applyTo(source: KommandSource): Vector
}