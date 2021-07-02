package io.github.monun.kommand.v1_17_R1

import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.tree.CommandNode
import io.github.monun.kommand.KommandContext
import io.github.monun.kommand.internal.Brigadier
import io.github.monun.kommand.internal.KommandDispatcherImpl
import io.github.monun.kommand.internal.KommandImpl
import io.github.monun.kommand.internal.LiteralKommandImpl
import net.minecraft.commands.CommandSourceStack
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_17_R1.CraftServer

class NMSBrigadier : Brigadier {
    override fun register(dispatcher: KommandDispatcherImpl) {
        val nms = (Bukkit.getServer() as CraftServer).handle.server.commands.dispatcher

        dispatcher.roots.forEach { root ->
            nms.register(root.convert() as LiteralArgumentBuilder<CommandSourceStack>)
            println("REGISTER")
        }
    }
}

private fun KommandImpl.convert(): ArgumentBuilder<CommandSourceStack, *> {
    return when (this) {
        is LiteralKommandImpl -> literal<CommandSourceStack>(name)
        else -> error("???")
    }.also { nms ->
        executor?.run {
            nms.executes {
                invoke(KommandContext())
                2
            }
        }

        nodes.forEach { node ->
            nms.then(node.convert())
        }
    }
}