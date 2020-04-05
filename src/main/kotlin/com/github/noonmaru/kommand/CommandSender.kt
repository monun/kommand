package com.github.noonmaru.kommand

import org.bukkit.GameRule
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

fun CommandSender.sendFeedback(message: String) {
    if (this is Player) {
        world.getGameRuleValue(GameRule.SEND_COMMAND_FEEDBACK)?.let { r ->
            if (r.not())
                return
        }
    }

    sendMessage(message)
}