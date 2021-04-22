/*
 * Copyright (c) 2020 Noonmaru
 *
 *  Licensed under the General Public License, Version 3.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://opensource.org/licenses/gpl-3.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.monun.kommand

import net.kyori.adventure.text.ComponentLike
import org.bukkit.GameRule
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

fun CommandSender.sendFeedback(message: () -> ComponentLike) {
    if (this is Player) {
        world.getGameRuleValue(GameRule.SEND_COMMAND_FEEDBACK)?.let { r ->
            if (!r)
                return
        }
    }

    sendMessage(message())
}

fun CommandSender.sendFeedback(message: String) {
    if (this is Player) {
        world.getGameRuleValue(GameRule.SEND_COMMAND_FEEDBACK)?.let { r ->
            if (!r)
                return
        }
    }

    sendMessage(message)
}