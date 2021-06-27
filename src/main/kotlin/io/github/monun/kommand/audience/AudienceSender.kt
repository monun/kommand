package io.github.monun.kommand.audience

//import net.kyori.adventure.text.ComponentLike
//import org.bukkit.GameRule
//import org.bukkit.command.CommandSender
//import org.bukkit.entity.Player
//
//fun CommandSender.sendFeedback(message: () -> ComponentLike) {
//    if (this is Player) {
//        world.getGameRuleValue(GameRule.SEND_COMMAND_FEEDBACK)?.let { r ->
//            if (!r)
//                return
//        }
//    }
//
//    sendMessage(message())
//}