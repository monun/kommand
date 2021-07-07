package io.github.monun.kommand.v1_17_R1.wrapper

import io.github.monun.kommand.wrapper.EntityAnchor
import io.github.monun.kommand.wrapper.WrapperSupport
import net.minecraft.commands.arguments.EntityAnchorArgument

class NMSWrapperSupport: WrapperSupport {
    override fun entityAnchorFeet(): EntityAnchor {
        return NMSEntityAnchor(EntityAnchorArgument.Anchor.FEET)
    }

    override fun entityAnchorEyes(): EntityAnchor {
        return NMSEntityAnchor(EntityAnchorArgument.Anchor.EYES)
    }
}