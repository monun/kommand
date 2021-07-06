package io.github.monun.kommand.plugin

import io.github.monun.kommand.*
import net.kyori.adventure.text.Component.text
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class KommandPlugin : JavaPlugin() {
    override fun onEnable() {
        val bool = KommandArgument.bool()
        val int = KommandArgument.int()
        val word = KommandArgument.string(StringType.SINGLE_WORD)
        val string = KommandArgument.string(StringType.QOUTABLE_PHRASE)
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
        }
    }
}