package io.github.monun.kommand.plugin

import io.github.monun.kommand.Kommand
import io.github.monun.kommand.KommandArgument
import io.github.monun.kommand.KommandSuggestion
import io.github.monun.kommand.getValue
import net.kyori.adventure.text.Component.text
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class KommandPlugin : JavaPlugin() {
    override fun onEnable() {
        val bool = KommandArgument.bool()
        val int = KommandArgument.int()

        Kommand.register("my") {
            then("age") {
                then("age" to int) {
                    executes {
                        val age: Int by it
                        Bukkit.broadcast(text("내 나이는 $age 살입니다."))
                    }
                }
            }
            then("flag") {
                then("flag" to bool) {
                    executes {
                        val flag: Boolean by it
                        Bukkit.broadcast(text("플래그 $flag"))
                    }
                }
            }
        }
    }
}