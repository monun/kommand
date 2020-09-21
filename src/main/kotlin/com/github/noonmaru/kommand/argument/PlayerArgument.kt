package com.github.noonmaru.kommand.argument

import com.github.noonmaru.kommand.KommandContext
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class PlayerArgument internal constructor() : KommandArgument<Player> {
    override val parseFailMessage: String
        get() = "${KommandArgument.TOKEN} 플레이어를 찾지 못했습니다."

    override fun parse(context: KommandContext, param: String): Player? {
        return Bukkit.getPlayerExact(param)
    }

    override fun listSuggestion(context: KommandContext, target: String): Collection<String> {
        return Bukkit.getOnlinePlayers().suggestions(target) { it.name }
    }

    companion object {
        internal val instance by lazy {
            PlayerArgument()
        }
    }
}