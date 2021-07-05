package io.github.monun.kommand.plugin

import io.github.monun.kommand.Kommand
import io.github.monun.kommand.KommandArgument
import io.github.monun.kommand.getValue
import net.kyori.adventure.text.Component.text
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class KommandPlugin : JavaPlugin() {
    override fun onEnable() {
        val ageArgument = KommandArgument.int()

        Kommand.register("my") {
            then("age") {
                then("age" to ageArgument) {
                    executes {
                        println("실행")
                        val age: Int by it
                        Bukkit.broadcast(text("내 나이는 $age 살입니다."))
                    }
                }
            }
        }
    }
}