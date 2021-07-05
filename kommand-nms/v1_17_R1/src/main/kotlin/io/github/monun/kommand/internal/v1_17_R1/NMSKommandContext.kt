package io.github.monun.kommand.internal.v1_17_R1

import com.mojang.brigadier.context.CommandContext
import io.github.monun.kommand.KommandContext
import net.minecraft.commands.CommandSourceStack

class NMSKommandContext(
    val nms: CommandContext<CommandSourceStack>
) : KommandContext {
    override val input: String
        get() = nms.input

    override val source by lazy { NMSKommandSource(nms.source) }

    override fun <T> get(name: String?): T {

    }
}



