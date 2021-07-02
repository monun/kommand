package io.github.monun.kommand.plugin

import io.github.monun.kommand.Kommand
import org.bukkit.plugin.java.JavaPlugin

class KommandPlugin : JavaPlugin() {
    override fun onEnable() {
        Kommand.register("kommand", "kmd") {
            then("first") {
                executes {
                    println("HELLO")
                }
            }
            then("second") {
                executes {
                    println("WORLD")
                }
            }
        }
    }
}