package io.github.monun.kommand.internal.v1_17_R1

import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import io.github.monun.kommand.KommandContext
import io.github.monun.kommand.KommandSource
import io.github.monun.kommand.internal.AbstractKommand
import io.github.monun.kommand.internal.Brigadier
import io.github.monun.kommand.internal.LiteralKommand
import io.github.monun.kommand.internal.RootKommand
import net.minecraft.commands.CommandSourceStack
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_17_R1.CraftServer

class NMSBrigadier : Brigadier {
    override fun register(kommand: RootKommand) {
        val nms = (Bukkit.getServer() as CraftServer).handle.server.commands.dispatcher
        nms.register(kommand.convert() as LiteralArgumentBuilder<CommandSourceStack>)
    }
}

private fun AbstractKommand.convert(): ArgumentBuilder<CommandSourceStack, *> {
    return when (this) {
        is LiteralKommand -> literal<CommandSourceStack>(name)
        else -> error("???")
    }.also { nms ->
        requires?.run { nms.requires { invoke(NMSKommandSource(it)) } }
        executor?.run { nms.executes { invoke(NMSKommandContext(it)) } }
        nodes.forEach { nms.then(it.convert()) }
    }
}