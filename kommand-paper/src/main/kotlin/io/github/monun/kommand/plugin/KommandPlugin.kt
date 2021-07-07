package io.github.monun.kommand.plugin

import com.google.gson.JsonObject
import io.github.monun.kommand.Kommand
import io.github.monun.kommand.KommandArgument
import io.github.monun.kommand.StringType
import io.github.monun.kommand.getValue
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.plugin.java.JavaPlugin

class KommandPlugin : JavaPlugin() {
    override fun onEnable() {
        val bool = KommandArgument.bool()
        val int = KommandArgument.int()
        val word = KommandArgument.string(StringType.SINGLE_WORD)
        val string = KommandArgument.string(StringType.QUOTABLE_PHRASE)
        val greedy = KommandArgument.string(StringType.GREEDY_PHRASE)

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
            then("word") {
                then("text" to word) {
                    executes {
                        val text: String by it
                        Bukkit.broadcast(text("word $text"))
                    }
                }
            }
            then("string") {
                then("text" to string) {
                    executes {
                        val text: String by it
                        Bukkit.broadcast(text("quote $text"))
                    }
                }
            }
            then("greedy") {
                then("text" to greedy) {
                    executes {
                        val text: String by it
                        Bukkit.broadcast(text("greedy $text"))
                    }
                }
            }
            then("color") {
                then("color" to KommandArgument.color()) {
                    executes {
                        val color: ChatColor by it
                        Bukkit.broadcast(text("$color color"))
                    }
                }
            }
            then("component") {
                then("component" to KommandArgument.component()) {
                    executes {
                        val component: Component by it
                        Bukkit.broadcast(component)
                    }
                }
            }
            then("compoundTag") {
                then("compoundTag" to KommandArgument.compoundTag()) {
                    executes {
                        val compoundTag: JsonObject by it
                        Bukkit.broadcast(text(compoundTag.toString()))
                    }
                }
            }
            then("dimension") {
                then("world" to KommandArgument.dimension()) {
                    executes {
                        val world: World by it
                        Bukkit.broadcast(text(world.toString()))
                    }
                }
            }
        }
    }
}