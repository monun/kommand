package io.github.monun.kommand.plugin

import io.github.monun.kommand.kommand
import org.bukkit.plugin.java.JavaPlugin

class KommandPlugin: JavaPlugin() {
    override fun onEnable() {
        kommand {
            register("kommand") {
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
}