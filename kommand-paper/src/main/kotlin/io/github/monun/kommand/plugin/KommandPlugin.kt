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
        val ageArgument = KommandArgument.int().apply {

            suggests {
                suggest("A") { text("HELLO") }
                suggest("B") { text("WORLD") }
                suggest("C") { text("BRIGADIER") }
            }
        }

        Kommand.register("my") {
            then("age") {
                then("age" to ageArgument) {
                    executes {
                        val age: Int by it
                        Bukkit.broadcast(text("내 나이는 $age 살입니다."))
                    }
                }
            }
        }
    }
}